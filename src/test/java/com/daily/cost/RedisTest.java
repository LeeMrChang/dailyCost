package com.daily.cost;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(args = "--spring.config.location=optional:classpath:application-local.yml,optional:classpath:application.yml")
@Slf4j
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 加锁 Lua 脚本
    private final RedisScript<Long> lockScript = new DefaultRedisScript<>(
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('pexpire', KEYS[1], ARGV[2]) return 1 else return 0 end",
            Long.class
    );

    // 解锁 Lua 脚本
    private final RedisScript<Long> unlockScript = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
    );

    /**
     * 尝试获取锁
     *
     * @param key       锁的键
     * @param requestId 唯一标识
     * @param expireMs  过期时间（毫秒）
     * @return 是否成功
     */
    public boolean tryLock(String key, String requestId, long expireMs) {
        Long result = stringRedisTemplate.execute(lockScript,
                Collections.singletonList(key),
                requestId, String.valueOf(expireMs));
        return result == 1L;
    }

    /**
     * 释放锁
     *
     * @param key       锁的键
     * @param requestId 唯一标识（必须与加锁时一致）
     * @return 是否成功
     */
    public boolean unlock(String key, String requestId) {
        Long result = stringRedisTemplate.execute(unlockScript,
                Collections.singletonList(key),
                requestId);
        return result == 1L;
    }

    /**
     * 测试单线程加解锁（原有方法）
     */
    @Test
    public void testSingle() throws InterruptedException {
        String lockKey = "myLock";
        String requestId = UUID.randomUUID().toString();
        long expireMs = 30000; // 30秒

        if (tryLock(lockKey, requestId, expireMs)) {
            log.info("获取锁成功，requestId: {}", requestId);
            try {
                stringRedisTemplate.opsForValue().set("hello", "world");
                stringRedisTemplate.opsForValue().set("事事顺心", "日富一日");
                Thread.sleep(5000); // 模拟业务处理
            } finally {
                unlock(lockKey, requestId);
                log.info("锁已释放");
            }
        } else {
            log.warn("获取锁失败");
        }
    }

    /**
     * 多线程并发抢锁测试
     */
    @Test
    public void testConcurrentLock() throws InterruptedException {
        String lockKey = "concurrentLock";
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);    // 控制所有线程同时开始
        CountDownLatch doneLatch = new CountDownLatch(threadCount); // 等待所有线程结束
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // 所有线程在此等待，直到主线程发出开始信号

                    String requestId = UUID.randomUUID().toString();
                    log.info("线程{} 尝试获取锁", threadId);

                    // 可添加重试机制（例如最多重试3次）
                    boolean locked = false;
                    int maxRetries = 3;
                    for (int retry = 0; retry < maxRetries; retry++) {
                        locked = tryLock(lockKey, requestId, 5000); // 锁过期时间5秒
                        if (locked) {
                            break;
                        }
                        // 重试前稍等片刻，避免过于频繁
                        Thread.sleep(100);
                    }

                    if (locked) {
                        log.info("线程{} 获取锁成功，开始执行业务", threadId);
                        // 模拟业务操作：写入 Redis 并停留一段时间
                        stringRedisTemplate.opsForValue().set("thread:" + threadId, "working at " + System.currentTimeMillis());
                        Thread.sleep(2000); // 模拟业务耗时2秒
                        boolean unlocked = unlock(lockKey, requestId);
                        log.info("线程{} 释放锁{}", threadId, unlocked ? "成功" : "失败");
                    } else {
                        log.warn("线程{} 最终获取锁失败", threadId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("线程{} 被中断", threadId);
                } finally {
                    doneLatch.countDown(); // 线程执行完毕，计数器减一
                }
            });
        }

        log.info("所有线程已提交，准备同时启动...");
        Thread.sleep(1000); // 稍微等待所有线程就绪
        startLatch.countDown(); // 发出开始信号，所有线程同时抢锁

        // 等待所有线程完成，最多等待30秒
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        if (!finished) {
            log.warn("部分线程未在超时时间内完成");
        }
        executor.shutdownNow();
        log.info("多线程测试结束");
    }

    @Test
    public void getRedis() {
        String key = "thread:3";
        log.info("print redis context:{}", stringRedisTemplate.opsForValue().get(key));
        Boolean isSuccess = stringRedisTemplate.delete(key);
        log.info("delete redis key isSuccess?:{}", isSuccess);
    }
}