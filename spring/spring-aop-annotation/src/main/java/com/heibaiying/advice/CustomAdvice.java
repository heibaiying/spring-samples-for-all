package com.heibaiying.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author : heibaiying
 * @description : 自定义切面
 */
@Aspect
@Component //除了加上@Aspect外 还需要声明为spring的组件 @Aspect只是一个切面声明
public class CustomAdvice {


    /**
     * 使用 || , or  表示或
     * 使用 && , and 表示与
     * ! 表示非
     */
    @Pointcut("execution(* com.heibaiying.service.OrderService.*(..)) && !execution(* com.heibaiying.service.OrderService.deleteOrder(..))")
    private void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        //获取节点名称
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println(name + "方法调用前：获取调用参数" + Arrays.toString(args));
    }

    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("后置返回通知结果" + result);
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知-前");
        //调用目标方法
        Object proceed = joinPoint.proceed();
        System.out.println("环绕通知-后");
        return proceed;
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        System.err.println("后置异常通知:" + exception);
    }


    @After("pointCut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("后置通知");
    }
}
