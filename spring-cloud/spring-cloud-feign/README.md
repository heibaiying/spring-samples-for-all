# spring-cloud-feign
## 目录<br/>
<a href="#一feign-简介">一、feign 简介</a><br/>
<a href="#二项目结构">二、项目结构</a><br/>
<a href="#三服务提供者的实现">三、服务提供者的实现</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-产品服务由`ProductService`提供并通过`ProducerController`将服务暴露给外部调用。">3.1 产品服务由`ProductService`提供，并通过`ProducerController`将服务暴露给外部调用。</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-指定注册中心地址并在启动类上开启自动注册@EnableDiscoveryClient">3.2 指定注册中心地址,并在启动类上开启自动注册@EnableDiscoveryClient</a><br/>
<a href="#四服务消费者的实现">四、服务消费者的实现</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41-导入openfeign依赖">4.1 导入openfeign依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42-指定注册中心地址并在启动类上添加注解@EnableDiscoveryClient和@EnableFeignClients">4.2 指定注册中心地址,并在启动类上添加注解@EnableDiscoveryClient和@EnableFeignClients</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#43-创建服务调用公共接口">4.3 创建服务调用公共接口</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#44-继承公共接口创建CProductFeign-用@FeignClient声明为feign客户端">4.4 继承公共接口，创建CProductFeign， 用@FeignClient声明为feign客户端</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#45--注入使用-feign-服务调用接口">4.5  注入使用 feign 服务调用接口</a><br/>
<a href="#五启动测试">五、启动测试</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#51-启动一个Eureka服务三个producer服务注意区分端口和一个消费者服务">5.1 启动一个Eureka服务、三个producer服务（注意区分端口）、和一个消费者服务</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#52--访问http//localhost8080/sell/products-查看负载均衡的调用结果">5.2  访问http://localhost:8080/sell/products 查看负载均衡的调用结果</a><br/>
<a href="#六-feign-的服务容错">六、 feign 的服务容错</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#61-feign-的依赖中默认导入了hystrix-的相关依赖我们不需要额外导入只需要开启相关配置即可">6.1 feign 的依赖中默认导入了hystrix 的相关依赖，我们不需要额外导入，只需要开启相关配置即可</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#62-在applicationyml-中开启hystrix">6.2 在application.yml 中开启hystrix</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#63-创建`CProductFeignImpl`继承feign接口CProductFeign定义熔断时候的回退处理">6.3 创建`CProductFeignImpl`,继承feign接口（CProductFeign），定义熔断时候的回退处理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#64-在-@FeignClient-注解中用fallback参数指定熔断时候的回退处理">6.4 在 @FeignClient 注解中，用fallback参数指定熔断时候的回退处理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#65-测试熔断处理">6.5 测试熔断处理</a><br/>
## 正文<br/>

## 一、feign 简介

在上一个用例中，我们使用ribbon+restTemplate 实现服务之间的远程调用，实际上每一个调用都是模板化的内容，所以spring cloud Feign 在此基础上进行了进一步的封装。我们只需要定义一个接口并使用feign注解的方式来进行配置，同时采用springMvc 注解进行参数绑定就可以完成服务的调用。feign同时还内置实现了负载均衡、服务容错等功能。



## 二、项目结构

+ common: 公共的接口和实体类；
+ consumer: 服务的消费者，采用feign调用产品服务；
+ producer：服务的提供者；
+ eureka: 注册中心。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-feign.png"/> </div>





## 三、服务提供者的实现

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-producer.png"/> </div>

#### 3.1 产品服务由`ProductService`提供，并通过`ProducerController`将服务暴露给外部调用。

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

#### 3.2 指定注册中心地址,并在启动类上开启自动注册@EnableDiscoveryClient

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

#### 4.1 导入openfeign依赖

```xml
<!-- feign 依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 4.2 指定注册中心地址,并在启动类上添加注解@EnableDiscoveryClient和@EnableFeignClients

@EnableFeignClients 会去扫描工程中所有用 @FeignClient 声明的 feign 客户端。

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

#### 4.3 创建服务调用公共接口

```java
/**
 * @author : heibaiying
 * @description : 声明式服务调用
 */
public interface ProductFeign {

    @GetMapping("products")
    List<Product> productList();

    /**
     * 这是需要强调的是使用feign时候@PathVariable一定要用value指明参数，
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



#### 4.4 继承公共接口，创建CProductFeign， 用@FeignClient声明为feign客户端

```java
/**
 * @author : heibaiying
 * @description : 声明式接口调用
 */
@FeignClient(value = "producer",configuration = FeignConfig.class)
public interface CProductFeign extends ProductFeign {

}
```

#### 4.5  注入使用 feign 服务调用接口

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

#### 5.1 启动一个Eureka服务、三个producer服务（注意区分端口）、和一个消费者服务

feign 的依赖中导入了spring-cloud-starter-netflix-ribbon依赖，并且在内部实现了基于ribbon的客户端负载均衡，所以我们这里启动三个producer实例来观察负载均衡的情况。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-app.png"/> </div>

**服务注册中心：**

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-eureka.png"/> </div>

#### 5.2  访问http://localhost:8080/sell/products 查看负载均衡的调用结果

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-products-8020.png"/> </div>

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-products-8030.png"/> </div>

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-8040.png"/> </div>





## 六、 feign 的服务容错

#### 6.1 feign 的依赖中默认导入了hystrix 的相关依赖，我们不需要额外导入，只需要开启相关配置即可

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/feign-hystrix-maven.png"/> </div>



#### 6.2 在application.yml 中开启hystrix

```yml
feign:
  hystrix:
    # 如果为true，则OpenFign客户端将使用Hystrix断路器进行封装 默认为false
    enabled: true
```

#### 6.3 创建`CProductFeignImpl`,继承feign接口（CProductFeign），定义熔断时候的回退处理

```java
/**
 * @author : heibaiying
 * @description : 定义发生熔断时候的回退处理。除了继承自CProductFeign,还需要用@Component声明为spring的组件
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

#### 6.4 在 @FeignClient 注解中，用fallback参数指定熔断时候的回退处理

```java
/**
 * @author : heibaiying
 * @description : 声明式接口调用
 */
@FeignClient(value = "producer",configuration = FeignConfig.class,fallback = CProductFeignImpl.class)
public interface CProductFeign extends ProductFeign {

}
```

#### 6.5 测试熔断处理

hystrix 默认调用超时时间为2s ,这里我们使用线程休眠的方式来模拟超时熔断。

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