# spring-cloud-ribbon

## 一、ribbon 简介

ribbon是Netfix公司开源的负载均衡组件，采用服务端负载均衡的方式，即消费者客户端维护可用的服务列表，并通过负载均衡的方式将请求按照指定的策略分摊给消费者，从而达到负载均衡的方式。



## 二、项目结构

+ common: 公共的接口和实体类；
+ consumer: 服务的消费者，采用RestTemplate调用产品服务；
+ producer：服务的提供者；
+ eureka: 注册中心，ribbon 从注册中心获取可用的服务列表，是实现负载均衡的基础。这里使用我们在[服务的注册与发现](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-eureka)这个用例中搭建的简单注册中心作为测试即可。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon.png"/> </div>



## 三、服务提供者的实现

#### 3.1 产品服务由`ProductService`提供，并通过`ProducerController`将服务暴露给外部调用。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/ribbon-producer.png"/> </div>

ProductService.java：

```java
/**
 * @author : heibaiying
 * @description : 产品提供接口实现类
 * 这里为了之后直观的看到负载均衡的结果，我们继承了 ApplicationListener,从 WebServerInitializedEvent 获取服务的端口号，并拼接在产品名称上
 */
@Service
public class ProductService implements IProductService, ApplicationListener<WebServerInitializedEvent> {

    private static List<Product> productList = new ArrayList<>();

    public Product queryProductById(int id) {
        for (Product product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
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
public class ProducerController {

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

#### 3.2 将服务注册到注册中心

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



## 四、服务消费者的实现

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/ribbon-consumer.png"/> </div>

#### 4.1 导入负载均衡需要的依赖

```xml
<!--ribbon 依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

#### 4.2 指定注册中心的地址

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

#### 4.3 使用@LoadBalanced配置RestTemplate即可实现客户端负载均衡

```java
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RibbonConfig {

    @LoadBalanced   // 配置客户端负载均衡
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### 4.4 使用RestTemplate调用远程服务

这里我们在调用远程服务的时候，url填写的是服务名+具体接口地址 ，由于我们的同一个服务会存在多个实例，在使用@LoadBalanced配置RestTemplate调用服务时，客户端就会从按照指定的负载均衡的方式将请求分摊到多个实例上。（默认的负载均衡采用的是RoundRobinRule（轮询）的策略，有特殊需求时候可以采用其他内置的策略规则，或者实现IRule来定义自己的负载均衡策略）。

```java
@Service
public class ProductService implements IProductService {

    @Autowired
    private RestTemplate restTemplate;

    public Product queryProductById(int id) {
        ResponseEntity<Product> responseEntity = restTemplate.getForEntity("http://producer/product/{1}", Product.class, id);
        return responseEntity.getBody();
    }


    public List<Product> queryAllProducts() {
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("http://producer/products", List.class);
        List<Product> productList = responseEntity.getBody();
        return productList;
    }

    public void saveProduct(Product product) {
        restTemplate.postForObject("http://producer/product", product, Void.class);
    }
}

```



## 五、启动测试

#### 5.1 启动一个Eureka服务、三个producer服务（注意区分端口）、和一个消费者服务

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-app.png"/> </div>

**服务注册中心：**

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-eureka.png"/> </div>

#### 5.2  访问http://localhost:8080/sell/products 查看负载均衡的调用结果

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-products-8020.png"/> </div>

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-ribbon-products-8030.png"/> </div>

## 六、 附1： 关于RestTemplate的说明

#### 6.1 restTemplate 调用对应resultful接口时候，使用的方法应该与接口声明方式（@GetMapping、@PostMapping、@PutMapping、@DeleteMapping）保持一致。请求类型与对应的调用方法如下。

- GET请求(getForObject 、getForEntity)
- POST请求（postForObject 、postForEntity）
- PUT请求（put）
- DELETE请求 （delete）

#### 6.2 ForEntity()和ForObject的区别

- `ForEntity()`发送一个请求，返回的ResponseEntity包含了响应体所映射成的对象

- `ForObject()`发送一个请求，返回的请求体将映射为一个对象

例如：

```java
ResponseEntity<Product> responseEntity = restTemplate.getForEntity("http://producer/product/{1}", Product.class, id);
Product product = restTemplate.getForObject("http://producer/product/{1}", Product.class, id);
```



## 七、 附2： 关于ribbon更多负载均衡的策略

Ribbon内置了多种负载均衡策略，如果有更复杂的需求，可以自己实现IRule。

#### 7.1 内置的负载均衡的策略如下图

![Ribbon负载均衡策略.png](http://upload-images.jianshu.io/upload_images/6944619-0355d316f5df9b3f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

图片来源于博客：[Ribbon负载均衡策略与自定义配置](https://blog.csdn.net/jrn1012/article/details/77837680)



#### 7.2 配置文件指定负载均衡的方式

要设置`IRule`名为的服务名称`users`，您可以设置以下属性：

```yaml
users:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule
```



#### 7.3 代码指定负载均衡的方式

```java
@Configuration
@RibbonClient(name = "custom", configuration = CustomConfiguration.class)
public class TestConfiguration {
}
```

```java
@Configuration
public class CustomConfiguration {

	@Bean
	public IRule ribbonRule() {
		return new BestAvailableRule();
	}
}
```

在使用代码方式的时候需要注意 [官方文档](http://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-ribbon.html#_customizing_the_ribbon_client)中关于注解方式有以下强调：


The CustomConfiguration clas must be a @Configuration class, but take care that it is not in a @ComponentScan for the main application context. Otherwise, it is shared by all the @RibbonClients. If you use @ComponentScan (or @SpringBootApplication), you need to take steps to avoid it being included (for instance, you can put it in a separate, non-overlapping package or specify the packages to scan explicitly in the @ComponentScan).


该`CustomConfiguration`类必须是`@Configuration`标注的，但需要注意的它不是在`@ComponentScan`主应用程序上下文。否则，它将由所有`@RibbonClients`共享。如果你使用`@ComponentScan`（或`@SpringBootApplication`），你需要采取一些措施来避免它被扫描到（例如，你可以把它放在一个独立的，非重叠的包，或用`@ComponentScan`时显示扫描指定的包）。
