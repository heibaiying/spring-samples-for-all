# Spring-Cloud-Feign


<nav>
<a href="#一Feign-简介">一、Feign 简介</a><br/>
<a href="#二项目结构">二、项目结构</a><br/>
<a href="#三服务提供者的实现">三、服务提供者的实现</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-定义服务">3.1 定义服务</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-服务注册">3.2 服务注册</a><br/>
<a href="#四服务消费者的实现">四、服务消费者的实现</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41-基本依赖">4.1 基本依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42-EnableFeignClients">4.2 @EnableFeignClients </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#43-创建服务调用接口">4.3 创建服务调用接口</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#44--Feign-客户端">4.4  Feign 客户端</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#45--调用远程服务">4.5  调用远程服务</a><br/>
<a href="#五启动测试">五、启动测试</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#51-启动服务">5.1 启动服务</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#52--验证负载均衡">5.2  验证负载均衡</a><br/>
<a href="#六Feign-的服务容错">六、Feign 的服务容错</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#61-开启容错配置">6.1 开启容错配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#62-定义降级处理">6.2 定义降级处理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#63-配置降级处理">6.3 配置降级处理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#64-测试熔断">6.4 测试熔断</a><br/>
</nav>

## 一、Feign 简介

在上一个用例中，我们使用 Ribbon + RestTemplate 实现服务之间的远程调用，实际上每一个调用都是模板化的内容，所以 Spring Cloud Feign 在此基础上进行了进一步的封装。我们只需要定义一个接口并使用 Feign 注解的方式来进行配置，同时采用 springMvc 注解进行参数绑定就可以完成服务的调用。Feign 同时还内置实现了负载均衡、服务容错等功能。



## 二、项目结构

+ **common**：公共的接口和实体类；
+ **consumer**：服务的消费者，采用 Feign 调用产品服务；
+ **producer**：服务的提供者；
+ **eureka**：注册中心。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-feign.png"/> </div>






## 三、服务提供者的实现

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-producer.png"/> </div>


### 3.1 定义服务

产品服务由 `ProductService` 提供，并通过 `ProducerController` 将服务暴露给外部调用：

ProductService.java：

```java
/**
 * @author : heibaiying
 * @description : 产品提供接口实现类
 */
@Service
public class ProductService implements IProductService, ApplicationListener<WebServerInitializedEvent> {

    private static List<Product> productList = new ArrayList<>();

    public Product queryProductById(int id) {
        return productList.stream().filter(p->p.getId()==id).collect(Collectors.toList()).get(0);
    }


    public List<Product> queryAllProducts() {
        return productList;
    }

    @Override
    public void saveProduct(Product product) {
        productList.add(product);
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        for (long i = 0; i < 20; i++) {
            productList.add(new Product(i, port + "产品" + i, i / 2 == 0, new Date(), 66.66f * i));
        }
    }
}
```

ProducerController.java：

```java
@RestController
public class ProducerController implements ProductFeign {

    @Autowired
    private IProductService productService;

    @GetMapping("products")
    public List<Product> productList() {
        return productService.queryAllProducts();
    }

    @GetMapping("product/{id}")
    public Product productDetail(@PathVariable int id) {
        return productService.queryProductById(id);
    }

    @PostMapping("product")
    public void save(@RequestBody Product product) {
        productService.saveProduct(product);
    }
}
```

### 3.2 服务注册

指定注册中心地址,并在启动类上开启自动注册 @EnableDiscoveryClient：

```java
server:
  port: 8020
# 指定服务命名
spring:
  application:
    name: producer
# 指定注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8010/eureka/
```

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}

```



## 四、服务消费者的实现

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-consumer.png"/> </div>


### 4.1 基本依赖

```xml
<!-- feign 依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 4.2 @EnableFeignClients 

指定注册中心地址，并在启动类上添加注解 @EnableDiscoveryClient 和 @EnableFeignClients，@EnableFeignClients 会去扫描工程中所有用 @FeignClient 声明的 Feign 客户端：

```java
server:
  port: 8080
# 指定服务命名
spring:
  application:
    name: consumer
# 指定注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8010/eureka/
```

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
```

### 4.3 创建服务调用接口

```java
/**
 * @author : heibaiying
 * @description : 声明式服务调用
 */
public interface ProductFeign {

    @GetMapping("products")
    List<Product> productList();

    /**
     * 这是需要强调的是使用 feign 时候@PathVariable 一定要用 value 指明参数，
     * 不然会抛出.IllegalStateException: PathVariable annotation was empty on param 异常
     */
    @GetMapping("product/{id}")
    Product productDetail(@PathVariable(value = "id") int id);


    @PostMapping("product")
    void save(@RequestBody Product product);
}

```

按照官方对于服务最佳化的推荐，这里我们的服务调用接口放在公共模块中，因为在实际的开发中，同一个服务调用接口可能被多个模块所使用。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/common-feign.png"/> </div>



### 4.4  Feign 客户端

继承公共接口，创建 CProductFeign， 用 @FeignClient 声明为 Feign 客户端：

```java
/**
 * @author : heibaiying
 * @description : 声明式接口调用
 */
@FeignClient(value = "producer",configuration = FeignConfig.class)
public interface CProductFeign extends ProductFeign {

}
```

### 4.5  调用远程服务

注入并使用 Feign 接口调用远程服务：

```java
@Controller
@RequestMapping("sell")
public class SellController {

    @Autowired
    private CProductFeign cproductFeign;

    @GetMapping("products")
    public String productList(Model model) {
        List<Product> products = cproductFeign.productList();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("product/{id}")
    public String productDetail(@PathVariable int id, Model model) {
        Product product = cproductFeign.productDetail(id);
        model.addAttribute("product", product);
        return "product";
    }


    @PostMapping("product")
    public String save(@RequestParam String productName) {
        long id = Math.round(Math.random() * 100);
        Product product = new Product(id, productName, false, new Date(), 88);
        cproductFeign.save(product);
        return "redirect:products";
    }
}
```



## 五、启动测试

### 5.1 启动服务

启动一个Eureka服务、三个生产者服务（注意区分端口）、和一个消费者服务。Feign 的依赖中导入了 spring-cloud-starter-netflix-ribbon 依赖，并且在内部实现了基于 Ribbon 的客户端负载均衡，所以我们这里启动三个生产者服务来观察负载均衡的情况：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-app.png"/> </div>


**服务注册中心：**

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-eureka.png"/> </div>


### 5.2  验证负载均衡

访问 http://localhost:8080/sell/products 查看负载均衡的调用结果：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-products-8020.png"/> </div>


<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-products-8030.png"/> </div>


<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-8040.png"/> </div>






## 六、Feign 的服务容错

### 6.1 开启容错配置

Feign 的依赖中默认导入了 Hystrix （熔断器）的相关依赖，我们不需要额外导入，只需要开启相关配置即可：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-hystrix-maven.png"/> </div>



 在 application.yml 中开启 Hystrix ：

```yml
feign:
  hystrix:
    # 如果为 true，则 OpenFign 客户端将使用 Hystrix 断路器进行封装 默认为 false
    enabled: true
```

### 6.2 定义降级处理

创建 `CProductFeignImpl`，继承 Feign接口（CProductFeign），定义熔断时候的降级处理机制：

```java
/**
 * @author : heibaiying
 * @description : 定义发生熔断时候的降级处理。除了继承自 CProductFeign,还需要用 @Component 声明为 spring 的组件
 */
@Component
public class CProductFeignImpl implements CProductFeign {

    // 发生熔断时候，返回空集合，前端页面会做容错显示
    @Override
    public List<Product> productList() {
        return new ArrayList<>();
    }

    @Override
    public Product productDetail(int id) {
        return null;
    }

    @Override
    public void save(Product product) {

    }
}
```

页面的简单容错处理：

```html
<!doctype html>
<html lang="en">
<head>
    <title>产品列表</title>
</head>
<body>
<h3>产品列表:点击查看详情</h3>
<form action="/sell/product" method="post">
    <input type="text" name="productName">
    <input type="submit" value="新增产品">
</form>
<ul>
    <#if (products?size>0) >
        <#list products as product>
            <li>
                <a href="/sell/product/${product.id}">${product.name}</a>
            </li>
        </#list>
    <#else>
        <h4 style="color: red">当前排队人数过多，请之后再购买！</h4>
    </#if>
</ul>
</body>
</html>
```

### 6.3 配置降级处理

在 @FeignClient 注解中，用 fallback 参数指定熔断时候的降级处理：

```java
/**
 * @author : heibaiying
 * @description : 声明式接口调用
 */
@FeignClient(value = "producer",configuration = FeignConfig.class,fallback = CProductFeignImpl.class)
public interface CProductFeign extends ProductFeign {

}
```

### 6.4 测试熔断

Hystrix 默认调用超时时间为 2s ，这里我们使用线程休眠的方式来模拟超时熔断。

```java
public List<Product> queryAllProducts() {
    /*用于测试 hystrix 超时熔断
    try {
        int i = new Random().nextInt(2500);
        Thread.sleep(i);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }*/
    return productList;
}
```

测试结果：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-hystrix.png"/> </div>


