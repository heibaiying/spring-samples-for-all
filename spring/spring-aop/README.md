# spring AOP（ XML 配置方式）
<nav>
<a href="#"></a><br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-依赖说明">1.2 依赖说明</a><br/>
<a href="#二Spring-AOP">二、Spring AOP</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-准备工作">2.1 准备工作</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-自定义切面">2.2 自定义切面</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-配置切面">2.3 配置切面</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-测试切面">2.4 测试切面</a><br/>
<a href="#三切面表达式">三、切面表达式</a><br/>
</nav>

## 


## 一、说明

### 1.1 项目结构说明

切面配置位于 resources 下的 `aop.xml` ，其中 CustomAdvice 是自定义切面类，OrderService 是待切入的方法。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-aop.png"/> </div>


### 1.2 依赖说明

除了 Spring 的基本依赖外，还需需要导入 AOP 的依赖：

```xml
<!--aop 相关依赖-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring-base-version}</version>
</dependency>
```



## 二、Spring AOP

### 2.1 准备工作

创建待切入的接口及其实现类：

```java
public interface OrderService {

    Order queryOrder(Long id);

    Order createOrder(Long id, String productName);
}
```

```java
public class OrderServiceImpl implements OrderService {

    public Order queryOrder(Long id) {
        return new Order(id, "product", new Date());
    }

    public Order createOrder(Long id, String productName) {
        // 模拟抛出异常
        // int j = 1 / 0;
        return new Order(id, "new Product", new Date());
    }
}
```

### 2.2 自定义切面

```java
public class CustomAdvice {

    /**
     *前置通知
     */
    public void before(JoinPoint joinPoint) {
        //获取节点名称
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println(name + "方法调用前：获取调用参数" + Arrays.toString(args));
    }

    /**
     *后置通知 (抛出异常后不会被执行)
     */
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("后置返回通知结果" + result);
    }

    /**
     *环绕通知
     */
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知-前");
        //调用目标方法
        Object proceed = joinPoint.proceed();
        System.out.println("环绕通知-后");
        return proceed;
    }

    /**
     *异常通知
     */
    public void afterException(JoinPoint joinPoint, Exception exception) {
        System.err.println("后置异常通知:" + exception);
    };

    /**
     *后置通知 总会执行 但是不能访问到返回值
     */
    public void after(JoinPoint joinPoint) {
        System.out.println("后置通知");
    }
}

```

### 2.3 配置切面

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--开启后允许使用 Spring AOP 的@AspectJ 注解 如果是纯 xml 配置 可以不用开启这个声明-->
    <aop:aspectj-autoproxy/>

    <!-- 1.配置目标对象 -->
    <bean name="orderService" class="com.heibaiying.service.OrderServiceImpl"/>
    <!-- 2.声明切面 -->
    <bean name="myAdvice" class="com.heibaiying.advice.CustomAdvice"/>
    <!-- 3.配置将通知织入目标对象 -->
    <aop:config>
        <!--命名切入点 关于切入点更多表达式写法可以参见 README.md-->
        <aop:pointcut expression="execution(* com.heibaiying.service.OrderService.*(..))" id="cutPoint"/>
        <aop:aspect ref="myAdvice">
            <!-- 前置通知 -->
            <aop:before method="before" pointcut-ref="cutPoint"/>
            <!-- 后置通知 如果需要拿到返回值 则要指明返回值对应的参数名称-->
            <aop:after-returning method="afterReturning" pointcut-ref="cutPoint" returning="result"/>
            <!-- 环绕通知 -->
            <aop:around method="around" pointcut-ref="cutPoint"/>
            <!-- 后置异常 如果需要拿到异常 则要指明异常对应的参数名称 -->
            <aop:after-throwing method="afterException" pointcut-ref="cutPoint" throwing="exception"/>
            <!-- 最终通知 -->
            <aop:after method="after" pointcut-ref="cutPoint"/>
        </aop:aspect>
    </aop:config>

</beans>
```

### 2.4 测试切面

```java
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:aop.xml")
public class AopTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void save() {
        orderService.createOrder(1283929319L, "手机");
        orderService.queryOrder(4891894129L);
    }
}
```



## 三、切面表达式

切面表达式遵循以下格式：

```shell
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
throws-pattern?)
```

- 除了返回类型模式，名字模式和参数模式以外，所有的部分都是可选的。
-  `*` 代表了匹配任意的返回类型。
- `()` 匹配一个不接受任何参数的方法， `(..)` 匹配一个接受任意数量参数的方法（零或者更多）。 `(*)` 匹配一个接受任意类型的参数的方法。 模式 `(*,String)` 匹配了一个接受两个参数的方法，第一个可以是任意类型，第二个则必须是 String 类型。

下面为一些常见切入点表达式：

- 任意公共方法的执行：

  ```java
  execution(public * *(..))
  ```

- 任何一个以 `set` 开头的方法的执行：

  ```java
  execution(* set*(..))
  ```

- `AccountService` 接口上任意方法的执行：

  ```java
  execution(* com.xyz.service.AccountService.*(..))
  ```

- 定义在 service 包里任意方法的执行：

  ```java
  execution(* com.xyz.service.*.*(..))
  ```

- 定义在 service 包或者子包里任意方法的执行：

  ```java
  execution(* com.xyz.service..*.*(..))
  ```

- 在 service 包里的任意连接点（在 Spring AOP 中只是方法执行） ：

  ```java
  within(com.xyz.service.*)
  ```

- 在 service 包或者子包里的任意连接点（在 Spring AOP 中只是方法执行） ：

  ```
  within(com.xyz.service..*)
  ```

- 实现了 `AccountService` 接口的代理对象的任意连接点（在 Spring AOP 中只是方法执行） ：

  ```
  this(com.xyz.service.AccountService)
  ```

更多表达式可以参考官方文档：[Declaring a Pointcut](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/core.html#aop-pointcuts)
