package com.heibaiying.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author : heibaiying
 * @description : 自定义切面
 */
@Order(1)
@Aspect
@Component
public class FirstAdvice {


    @Pointcut("execution(* com.heibaiying.service.OrderService.deleteOrder(..)))")
    private void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("第一个切面的before");
    }

    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("第一个切面的AfterReturning");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("第一个切面环绕通知-前");
        //调用目标方法
        Object proceed = joinPoint.proceed();
        System.out.println("第一个切面环绕通知-后");
        return proceed;
    }


    @After("pointCut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("第一个切面后置通知");
    }
}
