# spring 定时任务（xml配置方式）

## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-依赖说明">1.2 依赖说明</a><br/>
<a href="#二spring-scheduling">二、spring scheduling</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-创建定时任务">2.1 创建定时任务</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-配置定时任务">2.2 配置定时任务</a><br/>
## 正文<br/>


## 一、说明

### 1.1 项目结构说明

关于任务的调度配置定义在 springApplication.xml 中，为方便观察项目定时执行的情况，项目以 web 的方式构建。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-scheduling.png"/> </div>



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
package com.heibaiying.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * @author : heibaiying
 */
public class Task {

    public void methodA() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodA 方法执行", LocalDateTime.now()));
    }
   
    @Async
    public void methodB() throws InterruptedException {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodB 方法执行", LocalDateTime.now()));
        Thread.sleep(10 * 1000);
    }

    public void methodC() {
        Thread thread = Thread.currentThread();
        System.out.println(String.format("线程名称：%s ; 线程 ID：%s ; 调用方法：%s ; 调用时间：%s",
                thread.getName(), thread.getId(), "methodC 方法执行", LocalDateTime.now()));
    }
}

```

#### 2.2 配置定时任务

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.1.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd
        http://www.springframework.org/schema/task
        http://www.springframework.org/schema/task/spring-task.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.heibaiying.*"/>

    <!-- 开启注解驱动 -->
    <mvc:annotation-driven/>

    <!--配置定时任务-->
    <bean id="task" class="com.heibaiying.task.Task"/>
    <task:scheduled-tasks scheduler="myScheduler">
        <!--基于间隔的触发器，其中间隔是从上一个任务的  完成时间  开始计算, 时间单位值以毫秒为单位。-->
        <task:scheduled ref="task" method="methodA" fixed-delay="5000" initial-delay="1000"/>
        <!--基于间隔的触发器，其中间隔是从上一个任务的  开始时间  开始测量的。-->
        <task:scheduled ref="task" method="methodB" fixed-rate="5000"/>
        <!-- cron 表达式-->
        <task:scheduled ref="task" method="methodC" cron="0/10 * * * * ?"/>
    </task:scheduled-tasks>

    <!--定义任务调度器线程池的大小-->
    <task:scheduler id="myScheduler" pool-size="10"/>

    <!--定义任务执行器的线程池大小、等待队列的容量、和拒绝策略-->
    <task:executor
            id="executorWithPoolSizeRange"
            pool-size="5-25"
            queue-capacity="100"
            rejection-policy="CALLER_RUNS"/>
    <!--拒绝策略默认值为 ABORT
        CALLER_RUNS 来限制入栈任务
        DISCARD 删除当前任务
        DISCARD_OLDEST 将任务放在队列的头部。-->

    <!--允许在任何 Spring 管理的对象上检测@Async 和@Scheduled 注释, 如果存在，将生成用于异步执行带注释的方法的代理。-->
    <task:annotation-driven/>

</beans>
```

**关于调度程序线程池作用说明**：

按照例子 我们有 methodA 、 methodB 、methodC 三个方法 其中 methodB 是耗时的方法如果不声明调度程序线程池 则 methodB 会阻塞  methodA 、methodC 方法的执行 因为调度程序是单线程的

**关于任务执行线程池作用说明**：

按照例子 如果我们声明 methodB 是按照 fixedRate=5000 方法执行的 ，理论上不管任务耗时多久，任务都应该是每 5 秒执行一次，但是实际上任务是被加入执行队列，也不会立即被执行，因为默认执行任务是单线程的，这个时候需要开启@EnableAsync 并指定方法是 @Async 异步的，并且配置执行任务线程池 (如果不配置就使用默认的线程池配置)

