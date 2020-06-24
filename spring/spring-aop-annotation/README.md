# Spring AOP（注解方式）

<nav>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-依赖说明">1.2 依赖说明</a><br/>
<a href="#二Spring-AOP">二、Spring AOP</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-准备工作">2.1 准备工作</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-自定义切面类">2.2 自定义切面类</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-配置切面">2.3 配置切面</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-测试切面">2.4 测试切面</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25--切面执行顺序">2.5  切面执行顺序</a><br/>
<a href="#三切面表达式">三、切面表达式</a><br/>
</nav>

## 一、说明

### 1.1 项目结构说明

1. 切面配置位于 com.heibaiying.config 下 `AopConfig` ；
2. 自定义切面位于 advice 包下，其中 `CustomAdvice` 是标准的自定义切面，`FirstAdvice` 和 `SecondAdvice` 用于测试多切面共同作用于同一个切入点时的执行顺序；
3. `OrderService` 是待切入方法。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-aop-annotation.png"/> </div>


### 1.2 依赖说明

除了 Spring 的基本依赖外，需要导入 AOP 依赖包：

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

### 2.2 自定义切面类

使用 @Aspect 来定义切面类，@Pointcut 来定义切面表达式，它可以是多个切面表达式的组合：

```java
@Aspect
@Component //除了加上@Aspect 外 还需要声明为 spring 的组件 @Aspect 只是一个切面声明
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

    // returning 参数用于指定返回结果与哪一个参数绑定
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

    // throwing 参数用于指定抛出的异常与哪一个参数绑定
    @AfterThrowing(pointcut = "pointCut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        System.err.println("后置异常通知:" + exception);
    }


    @After("pointCut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("后置通知");
    }
}

```

### 2.3 配置切面

```java
@Configuration
@ComponentScan("com.heibaiying.*")
@EnableAspectJAutoProxy // 开启@Aspect 注解支持 等价于<aop:aspectj-autoproxy>
public class AopConfig {
}
```

### 2.4 测试切面

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AopConfig.class)
public class AopTest {


    @Autowired
    private OrderService orderService;

    @Test
    public void saveAndQuery() {
        orderService.createOrder(1283929319L, "手机");
        orderService.queryOrder(4891894129L);
    }

    /**
     * 多个切面作用于同一个切入点时，可以用 @Order 指定切面的执行顺序
     * 优先级高的切面在切入方法前执行的通知 (如 before) 会优先执行，但是位于方法后执行的通知 (如 after,afterReturning) 反而会延后执行
     */
    @Test
    public void delete() {
        orderService.deleteOrder(12793179L);
    }
}
```

### 2.5  切面执行顺序

- 多个切面作用于同一个切入点时，可以用 @Order 指定切面的执行顺序。

- 优先级高的切面在切入方法前执行的通知 ( 如 before) 会优先执行，但是位于方法后执行的通知 ( 如 after，afterReturning ) 反而会延后执行，类似于同心圆原理：

  <div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/aop执行顺序.png"/> </div>



## 三、切面表达式

切面表达式遵循以下格式：

```shell
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern)
throws-pattern?)
```

- 除了返回类型模式，名字模式和参数模式以外，所有的部分都是可选的。
- `*` 代表了匹配任意的返回类型。
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
