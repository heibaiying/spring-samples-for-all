# spring boot 整合 dubbo

## 一、 项目结构说明

1.1  按照dubbo 文档推荐的服务最佳实践，建议将服务接口、服务模型、服务异常等均放在 API 包中，所以项目采用maven多模块的构建方式，在spring-boot-dubbo下构建三个子模块：

1. boot-dubbo-common 是公共模块，用于存放公共的接口和bean,被boot-dubbo-provider和boot-dubbo-consumer在pom.xml中引用；
2. boot-dubbo-provider 是服务的提供者，提供商品的查询服务；
3. boot-dubbo-consumer是服务的消费者，调用provider提供的查询服务。

1.2  本项目dubbo的搭建采用zookeeper作为注册中心， 关于zookeeper的安装和基本操作可以参见我的手记  [Zookeeper 基础命令与Java客户端](https://github.com/heibaiying/LearningNotes/blob/master/notes/%E4%B8%AD%E9%97%B4%E4%BB%B6/ZooKeeper/ZooKeeper%E9%9B%86%E7%BE%A4%E6%90%AD%E5%BB%BA%E4%B8%8EJava%E5%AE%A2%E6%88%B7%E7%AB%AF.md)

![spring-scheduling](D:\spring-samples-for-all\pictures\spring-boot-dubbo.png)



## 二、关键依赖

在父工程的项目中统一导入依赖dubbo的starter，父工程的pom.xml如下

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
        <!--引入dubbo start依赖-->
        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>0.2.0</version>
        </dependency>
    </dependencies>

</project>
```



## 三、公共模块（boot-dubbo-common）

- api 下为公共的调用接口；
- bean 下为公共的实体类。

![spring-scheduling](D:\spring-samples-for-all\pictures\boot-dubbo-common.png)

## 四、 服务提供者（boot-dubbo-provider）

![spring-scheduling](D:\spring-samples-for-all\pictures\boot-dubbo-provider.png)

#### 4.1 提供方配置

```yaml
dubbo:
  application:
    name: boot-duboo-provider
  # 指定注册协议和注册地址  dubbo推荐使用zookeeper作为注册中心，并且在start依赖中引入了zookeeper的java客户端Curator
  registry:
    protocol: zookeeper
    address: 127.0.0.1:2181
  protocol.name: dubbo
```

#### 4.2  使用注解@Service暴露服务

需要注意的是这里的@Service注解不是spring的注解，而是dubbo的注解 com.alibaba.dubbo.config.annotation.Service

```java
package com.heibaiying.dubboprovider.service;

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

## 五、服务消费者（boot-dubbo-consumer）

![boot-dubbo-consumer](D:\spring-samples-for-all\pictures\boot-dubbo-consumer1.png)

#### 1.消费方的配置

```yaml
dubbo:
  application:
    name: boot-duboo-provider
  # 指定注册协议和注册地址  dubbo推荐使用zookeeper作为注册中心，并且在start依赖中引入了zookeeper的java客户端Curator
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

#### 2.使用注解@Reference引用远程服务

```java
package com.heibaiying.dubboconsumer.controller;

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
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <artifactId>spring-boot-dubbo</artifactId>
        <groupId>com.heibaiying</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>boot-dubbo-consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>boot-dubbo-consumer</name>
    <description>dubbo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <!--引入对公共模块的依赖-->
    <dependencies>
        <dependency>
            <groupId>com.heibaiying</groupId>
            <artifactId>boot-dubbo-common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

provider中 pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <artifactId>spring-boot-dubbo</artifactId>
        <groupId>com.heibaiying</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>


    <artifactId>boot-dubbo-provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>boot-dubbo-provider</name>
    <description>dubbo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <!--引入对公共模块的依赖-->
    <dependencies>
        <dependency>
            <groupId>com.heibaiying</groupId>
            <artifactId>boot-dubbo-common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

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