# Spring Boot 整合 Dubbo

<nav>
<a href="#一-项目结构">一、 项目结构</a><br/>
<a href="#二基本依赖">二、基本依赖</a><br/>
<a href="#三公共模块">三、公共模块</a><br/>
<a href="#四服务提供者">四、服务提供者</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41-提供者配置">4.1 提供者配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42-暴露服务">4.2 暴露服务</a><br/>
<a href="#五服务消费者">五、服务消费者</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#51-消费者配置">5.1 消费者配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#52-调用服务">5.2 调用服务</a><br/>
<a href="#六项目构建">六、项目构建</a><br/>
<a href="#七Dubbo-控制台">七、Dubbo 控制台</a><br/>
</nav>

## 一、 项目结构

按照 Dubbo 文档推荐的服务最佳化实践的要求，建议将服务接口、服务模型、服务异常等均放在 API 包中，所以项目采用 Maven 多模块的构建方式，在 spring-boot-dubbo 下构建三个子模块：

- **boot-dubbo-common** ：是公共模块，用于存放公共的接口和 Java Bean，被 boot-dubbo-provider 和 boot-dubbo-consumer 在 pom.xml 中引用；
- **boot-dubbo-provider** ：服务的提供者，提供商品的查询服务；
- **boot-dubbo-consumer** ：是服务的消费者，调用 provider 提供的查询服务。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-dubbo.png"/> </div>

## 二、基本依赖

在父工程的项目中统一导入依赖 Dubbo 的 starter，父工程的 pom.xml 如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <packaging>pom</packaging>

    <modules>
        <module>boot-dubbo-common</module>
        <module>boot-dubbo-consumer</module>
        <module>boot-dubbo-provider</module>
    </modules>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.heibaiying</groupId>
    <artifactId>spring-boot-dubbo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-boot-dubbo</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--引入 dubbo start 依赖-->
        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>0.2.0</version>
        </dependency>
    </dependencies>

</project>
```



## 三、公共模块

- api 下为公共的调用接口；
- bean 下为公共的实体类。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/boot-dubbo-common.png"/> </div>
## 四、服务提供者

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/boot-dubbo-provider.png"/> </div>
### 4.1 提供者配置

```yaml
dubbo:
  application:
    name: boot-duboo-provider
  # 指定注册协议和注册地址  dubbo 推荐使用 zookeeper 作为注册中心，并且在 start 依赖中引入了 zookeeper 的 java 客户端 Curator
  registry:
    protocol: zookeeper
    address: 127.0.0.1:2181
  protocol.name: dubbo
```

### 4.2 暴露服务

 使用注解 @Service 暴露服务，需要注意的是这里的 @Service 不是 Spring 的注解，而是 Dubbo 的注解：

```java
import com.alibaba.dubbo.config.annotation.Service;

/**
 * @description : 产品提供接口实现类
 */
@Service(timeout = 5000)
public class ProductService implements IProductService {

    private static List<Product> productList = new ArrayList<>();

    static {
        for (int i = 0; i < 20; i++) {
            productList.add(new Product(i, "产品" + i, i / 2 == 0, new Date(), 66.66f * i));
        }
    }

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
}

```

## 五、服务消费者

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/boot-dubbo-consumer1.png"/> </div>
### 5.1 消费者配置

```yaml
dubbo:
  application:
    name: boot-duboo-provider
  # 指定注册协议和注册地址  dubbo 推荐使用 zookeeper 作为注册中心，并且在 start 依赖中引入了 zookeeper 的 java 客户端 Curator
  registry:
    protocol: zookeeper
    address: 127.0.0.1:2181
  protocol.name: dubbo
  # 关闭所有服务的启动时检查 (没有提供者时报错）视实际情况设置
  consumer:
    check: false
server:
port: 8090
```

### 5.2 调用服务

使用 @Reference 注解引用远程服务：

```java
import com.alibaba.dubbo.config.annotation.Reference;

@Controller
@RequestMapping("sell")
public class SellController {

    // dubbo 远程引用注解
    @Reference
    private IProductService productService;

    @RequestMapping
    public String productList(Model model) {
        List<Product> products = productService.queryAllProducts();
        model.addAttribute("products", products);
        return "products";
    }

    @RequestMapping("product/{id}")
    public String productDetail(@PathVariable int id, Model model) {
        Product product = productService.queryProductById(id);
        model.addAttribute("product", product);
        return "product";
    }
}
```

## 六、项目构建

因为在项目中，consumer 和 provider 模块均依赖公共模块,所以在构建 consumer 和 provider 项目前需要将 common 模块安装到本地仓库，依次对父工程和 common 模块执行：

```shell
mvn install -Dmaven.test.skip = true
```

## 七、Dubbo 控制台

Dubbo 新版本管理控制台的安装步骤如下：

```sh
git clone https://github.com/apache/incubator-dubbo-ops.git /var/tmp/dubbo-ops
cd /var/tmp/dubbo-ops
mvn clean package
```

配置：

```properties
# 配置文件为：
dubbo-admin-backend/src/main/resources/application.properties

# 可以在其中修改zookeeper的地址
dubbo.registry.address=zookeeper://127.0.0.1:2181
```

启动：

```sh
mvn --projects dubbo-admin-backend spring-boot:run
```

访问：

```shell
http://127.0.0.1:8080
```
