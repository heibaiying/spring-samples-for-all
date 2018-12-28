package com.heibaiying.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

/**
 * @author : heibaiying
 * @description : 自定义切面
 */
public class CustomAdvice {


    //前置通知
    public void before(JoinPoint joinPoint) {
        //获取节点名称
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println(name + "方法调用前：获取调用参数" + Arrays.toString(args));
    }

    //后置通知(抛出异常后不会被执行)
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("后置返回通知结果" + result);
    }

    //环绕通知
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知-前");
        //调用目标方法
        Object proceed = joinPoint.proceed();
        System.out.println("环绕通知-后");
        return proceed;
    }

    //异常通知
    public void afterException(JoinPoint joinPoint, Exception exception) {
        System.err.println("后置异常通知:" + exception);
    }

    ;

    // 后置通知 总会执行 但是不能访问到返回值
    public void after(JoinPoint joinPoint) {
        System.out.println("后置通知");
    }
}
