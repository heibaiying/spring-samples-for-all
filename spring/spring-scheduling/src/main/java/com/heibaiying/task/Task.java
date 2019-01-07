package com.heibaiying.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * @author : heibaiying
 * @description :
 */
public class Task {

    /**
     * 基于间隔的触发器，其中间隔是从上一个任务的  完成时间  开始计算, 时间单位值以毫秒为单位。
     */
    public void methodA() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodA方法执行", LocalDateTime.now()));
    }

    /**
     * 基于间隔的触发器，其中间隔是从上一个任务的  开始时间  开始测量的。
     */
    @Async
    public void methodB() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodB方法执行", LocalDateTime.now()));
        Thread.sleep(10 * 1000);
    }

    public void methodC() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodC方法执行", LocalDateTime.now()));
    }
}
