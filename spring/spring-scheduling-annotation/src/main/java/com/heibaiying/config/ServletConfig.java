package com.heibaiying.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author : heibaiying
 * spring 主配置类
 */
@Configuration
@EnableWebMvc
@EnableScheduling  //启用Spring的计划任务执行功能
@EnableAsync       //启用Spring的异步方法执行功能
@ComponentScan(basePackages = {"com.heibaiying.task"})
public class ServletConfig implements WebMvcConfigurer, AsyncConfigurer, SchedulingConfigurer {

    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // 任务执行器线程池配置
    @Override
    public Executor getAsyncExecutor() {
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MyExecutor-");
        executor.initialize();
        return executor;
    }

    // 这个方法可以监听到异步程序发生的错误
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                System.out.println(method.getName() + "发生错误:" + ex.getMessage());
            }
        };
    }

    // 如果程序结束，需要关闭线程池 不然程序无法完全退出 只能kill才能完全退出
    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    // 调度程序线程池配置
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    // 如果程序结束，需要关闭线程池
    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(50);
    }


    /*关于调度程序线程池作用说明
     *
     * 按照例子 我们有methodA 、 methodB 、methodC 三个方法 其中 methodB 是耗时的方法,且不支持异步
     * 如果不声明调度程序线程池 则methodB 会阻塞  methodA 、methodC 方法的执行 因为调度程序是单线程的
     */


    /*关于任务执行线程池作用说明
     *
     * 按照例子 如果我们声明 methodB 是按照 fixedRate=5000 方法执行的 ，理论上不管任务耗时多久，任务都应该是
     * 每5秒执行一次，但是实际上任务是被加入执行队列，也不会立即被执行，因为默认执行任务是单线程的，这个时候需要开启
     * @EnableAsync 并指定方法是 @Async 异步的，并且配置执行任务线程池(如果不配置就使用默认的线程池配置)
     */

    /*配置建议 如果不需要细粒度的控制 以上代码配置都不是必须的 但是建议耗时方法采用异步执行*/

}
