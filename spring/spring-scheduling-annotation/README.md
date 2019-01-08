# spring 定时任务（注解方式）

## 一、说明

### 1.1 项目结构说明

关于任务的调度配置定义在ServletConfig.java中，为方便观察项目定时执行的情况，项目以web的方式构建。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-scheduling-annotation.png"/> </div>



### 1.2 依赖说明

导入基本依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.heibaiying</groupId>
    <artifactId>spring-scheduling</artifactId>
    <version>1.0-SNAPSHOT</version>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <properties>
        <spring-base-version>5.1.3.RELEASE</spring-base-version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```



## 二、spring scheduling

#### 2.1 创建定时任务

```java
/**
 * @author : heibaiying
 */
@Component
public class Task {

    /**
     * 基于间隔的触发器，其中间隔是从上一个任务的  完成时间  开始计算, 时间单位值以毫秒为单位。
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 1000)
    public void methodA() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodA方法执行", LocalDateTime.now()));
    }

    /**
     * 基于间隔的触发器，其中间隔是从上一个任务的  开始时间  开始测量的。
     */
    @Scheduled(fixedRate = 5000)
    @Async
    public void methodB() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodB方法执行", LocalDateTime.now()));
        Thread.sleep(10 * 1000);
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void methodC() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodC方法执行", LocalDateTime.now()));
    }
}

```

#### 2.2 配置定时任务

```java
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
}
```

**关于调度程序线程池作用说明**：

按照例子 我们有methodA 、 methodB 、methodC 三个方法 其中 methodB 是耗时的方法如果不声明调度程序线程池 则methodB 会阻塞  methodA 、methodC 方法的执行 因为调度程序是单线程的

**关于任务执行线程池作用说明**：

按照例子 如果我们声明 methodB 是按照 fixedRate=5000 方法执行的 ，理论上不管任务耗时多久，任务都应该是每5秒执行一次，但是实际上任务是被加入执行队列，也不会立即被执行，因为默认执行任务是单线程的，这个时候需要开启@EnableAsync 并指定方法是 @Async 异步的，并且配置执行任务线程池(如果不配置就使用默认的线程池配置)

