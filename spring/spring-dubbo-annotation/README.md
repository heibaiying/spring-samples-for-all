# spring 整合 dubbo（注解方式）

## 一、 项目结构说明

1.1  按照dubbo 文档推荐的服务最佳实践，建议将服务接口、服务模型、服务异常等均放在 API 包中，所以项目采用maven多模块的构建方式，在spring-dubbo-annotation下构建三个子模块：

1. dubbo-ano-common 是公共模块，用于存放公共的接口和bean,被dubbo-ano-provider和dubbo-ano-provider在pom.xml中引用；
2. dubbo-ano-provider 是服务的提供者，提供商品的查询服务；
3. dubbo-ano-provider 是服务的消费者，调用provider提供的查询服务。

1.2  本项目dubbo的搭建采用zookeeper作为注册中心， 关于zookeeper的安装和基本操作可以参见我的手记[Zookeeper 基础命令与Java客户端](https://github.com/heibaiying/LearningNotes/blob/master/notes/%E4%B8%AD%E9%97%B4%E4%BB%B6/ZooKeeper/ZooKeeper%E9%9B%86%E7%BE%A4%E6%90%AD%E5%BB%BA%E4%B8%8EJava%E5%AE%A2%E6%88%B7%E7%AB%AF.md)

![spring-scheduling](D:\spring-samples-for-all\pictures\spring-dubbo.png)



## 二、项目依赖

**在父工程的项目中统一导入依赖dubbo依赖的的jar包**

这里需要注意的是ZooKeeper 3.5.x 和 ZooKeeper 3.4.x 是存在不兼容的情况 详见官网解释[ZooKeeper Version Compatibility](https://curator.apache.org/zk-compatibility.html), zookeeper 3.5 目前是beta版本，所以zookeeper 我选择的版本是 zookeeper-3.4.9 作为服务端。但默认情况下 curator-framework自动引用的最新的3.5的版本客户端，会出现 KeeperException$UnimplementedException 异常

```xml
<!--dubbo 依赖-->
<groupId>com.alibaba</groupId>
<artifactId>dubbo</artifactId>
<version>2.6.2</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>4.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.13</version>
</dependency>
```



## 三、公共模块（dubbo-ano-common）

- api 下为公共的调用接口；
- bean 下为公共的实体类。

![spring-scheduling](D:\spring-samples-for-all\pictures\dubbo-ano-common.png)

## 四、 服务提供者（dubbo-ano-provider）

![spring-scheduling](D:\spring-samples-for-all\pictures\dubbo-ano-provider.png)

#### 4.1 提供方配置

```java
@Configuration
public class DubboConfiguration {

    /**
     * 提供方应用信息，用于计算依赖关系
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dubbo-ano-provider");
        return applicationConfig;
    }

    /**
     *  使用zookeeper注册中心暴露服务地址
     */
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181");
        registryConfig.setClient("curator");
        return registryConfig;
    }

    /**
     * 用dubbo协议在20880端口暴露服务
     */
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(20880);
        return protocolConfig;
    }
}
```

#### 4.2  使用注解@Service暴露服务

需要注意的是这里的@Service注解不是spring的注解，而是dubbo的注解 com.alibaba.dubbo.config.annotation.Service

```java
package com.heibaiying.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.heibaiying.api.IProductService;
import com.heibaiying.bean.Product;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : heibaiying
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

#### 

## 五、服务消费者（dubbo-ano-consumer）

![spring-scheduling](D:\spring-samples-for-all\pictures\dubbo-ano-consumer.png)

#### 1.消费方的配置

```java
@Configuration
public class DubboConfiguration {

    /**
     * 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dubbo-ano-consumer");
        return applicationConfig;
    }

    /**
     * 设置调用服务超时时间
     * 关闭所有服务的启动时检查
     */
    @Bean
    public ConsumerConfig consumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(3000);
        consumerConfig.setCheck(false);
        return consumerConfig;
    }

    /**
     * 使用zookeeper注册中心暴露发现服务地址
     */
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181");
        registryConfig.setClient("curator");
        return registryConfig;
    }

}
```

#### 2.使用注解@Reference引用远程服务

```java
package com.heibaiying.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.heibaiying.api.IProductService;
import com.heibaiying.bean.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("sell")
public class SellController {

    // dubbo远程引用注解
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

## 六、项目构建的说明

因为在项目中，consumer和provider模块均依赖公共模块,所以在构建consumer和provider项目前需要将common 模块安装到本地仓库，**依次**对**父工程**和**common模块**执行：

```shell
mvn install -Dmaven.test.skip = true
```

consumer中 pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-dubbo-annotation</artifactId>
        <groupId>com.heibaiying</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-ano-consumer</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.heibaiying</groupId>
            <artifactId>dubbo-ano-common</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

</project>
```

provider中 pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-dubbo-annotation</artifactId>
        <groupId>com.heibaiying</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-ano-provider</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.heibaiying</groupId>
            <artifactId>dubbo-ano-common</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

</project>
```

## 七、关于dubbo新版本管理控制台的安装说明

安装:

```sh
git clone https://github.com/apache/incubator-dubbo-ops.git /var/tmp/dubbo-ops
cd /var/tmp/dubbo-ops
mvn clean package
```

配置：

```sh
配置文件为：
dubbo-admin-backend/src/main/resources/application.properties
主要的配置有 默认的配置就是127.0.0.1:2181：
dubbo.registry.address=zookeeper://127.0.0.1:2181
```

启动:

```sh
mvn --projects dubbo-admin-backend spring-boot:run
```

访问:

```
http://127.0.0.1:8080
```