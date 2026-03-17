package com.daily.cost;

import com.sun.jdi.Bootstrap;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.MDC;
import sun.misc.Unsafe;

import java.io.BufferedInputStream;
import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.*;

public class HashTest {

    public static void main(String[] args) {
        Map<Integer,Object> map = new HashMap<>();
        for (int i = 1; i < 100; i++) {
            map.put(i, i);
            map.put(null,1000);
        }
        //map.remove()
        System.out.println(map);
        Hashtable<Object, Object> hashtable = new Hashtable<>();
        hashtable.put(1, 1);

        ConcurrentHashMap hashMap = new ConcurrentHashMap();
        hashMap.put(1,1);
        hashMap.put(2,1);

        int core = Runtime.getRuntime().availableProcessors();
        int max = core * 2;
        int queueSize = 100;
        var executor = new ThreadPoolExecutor(
                core,
                max,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize),
               /* new CustomThreadFactory("my-pool")*/null,
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：主线程执行
        );

        executor.execute(()-> System.out.println(executor.getQueue().size()));
        executor.submit(()-> System.out.println(executor.getQueue().size()));
        CountDownLatch latch = new CountDownLatch(10);
        ThreadLocal<Object> local = new ThreadLocal<>();
        latch.countDown();
        Thread thread = new Thread();
        thread.start();

        System.gc();
    }
}
