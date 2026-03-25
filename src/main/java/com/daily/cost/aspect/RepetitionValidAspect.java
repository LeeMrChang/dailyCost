package com.daily.cost.aspect;

import cn.hutool.core.text.CharSequenceUtil;
import com.daily.cost.aspect.anno.RepetitionValid;
import com.daily.cost.constant.RedisConstant;
import com.daily.cost.utils.AdminUtil;
import com.daily.cost.utils.AspectUtil;
import com.daily.cost.utils.BizException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 防止同个用户重复请求校验切面
 */
@Component
@Aspect
@Slf4j
public class RepetitionValidAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(repetitionValid)")
    public Object around(ProceedingJoinPoint point, RepetitionValid repetitionValid) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        String clazz = signature.getDeclaringTypeName();
        String method = signature.getName();
        Object param;
        String keyParam = repetitionValid.keyParam();
        if (CharSequenceUtil.isNotBlank(keyParam)) {
            Map<String, Object> methodArgMap = AspectUtil.getMethodArgMap(point);
            param = AspectUtil.getValue(keyParam, methodArgMap);
        } else if (AdminUtil.merchantAuth() || AdminUtil.headAuth()) {
            param = AdminUtil.getUserId();
        } else {
            param = "用户ID";
        }
        String validKey = RedisConstant.format(RedisConstant.REPETITION_VALID, CharSequenceUtil.join("#", clazz, method), param);
        if (!lock((validKey))) {
            throw new BizException("上次请求未结束，请稍后重试");
        }
        try {
            return point.proceed();
        } finally {
            unlock(validKey);
        }
    }

    //现代加锁写法
    public boolean lock(String key) {
        ValueOperations<String, String> op = stringRedisTemplate.opsForValue();
        //这里只是为了加锁，value值是没有用到的。so 设置一直为1是没问题的
        Boolean result = op.setIfAbsent(key, "1", 5, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

//    老式写法
//    public boolean lock(String key) {
//        long currentMillis = System.currentTimeMillis();
//        String expireMillis = String.valueOf(currentMillis + RedisConstant.M_SEC_5000);
//        ValueOperations<String, String> op = stringRedisTemplate.opsForValue();
//        if (Objects.equals(op.setIfAbsent(key, expireMillis), Boolean.TRUE)) {
//            return true;
//        }
//        String cur = op.get(key);
//        currentMillis = System.currentTimeMillis();
//        if (CharSequenceUtil.isNotBlank(cur) && currentMillis > Long.parseLong(cur)) {
//            expireMillis = String.valueOf(currentMillis + RedisConstant.M_SEC_5000);
//            String old = op.getAndSet(key, expireMillis);
//            return CharSequenceUtil.equals(old, cur);
//        }
//        return false;
//    }

    public void unlock(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("redis分布式锁-解锁失败", e);
        }
    }
}
