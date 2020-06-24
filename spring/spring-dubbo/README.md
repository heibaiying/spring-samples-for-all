# Spring 整合 Dubbo（XML 配置方式）

<nav>
<a href="#一-项目结构">一、 项目结构</a><br/>
<a href="#二项目依赖">二、项目依赖</a><br/>
<a href="#三公共模块">三、公共模块</a><br/>
<a href="#四-服务提供者">四、 服务提供者</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41--开发服务">4.1  开发服务</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42-暴露服务">4.2 暴露服务</a><br/>
<a href="#五服务消费者">五、服务消费者</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#51-调用服务">5.1 调用服务</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#52-消费服务">5.2 消费服务</a><br/>
<a href="#六项目构建">六、项目构建</a><br/>
<a href="#七Dubbo-控制台">七、Dubbo 控制台</a><br/>
</nav>

## 一、 项目结构

按照 Dubbo 官方文档推荐的服务最佳化方案，建议将服务接口、服务模型、服务异常等均放在单独的 API 包中，所以项目采用 maven 多模块的构建方式，在 spring-dubbo 下构建三个子模块：

- **dubbo-common**：公共模块，用于存放公共的接口和 bean，被 dubbo-provider 和 dubbo-provider 所引用；
- **dubbo-provider** ：服务的提供者，提供商品的查询服务；
- **dubbo-provider** ：是服务的消费者，调用 provider 提供的查询服务。

另外，本项目 Dubbo 的搭建采用 ZooKeeper 作为注册中心。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-dubbo.png"/> </div>


## 二、项目依赖

在父工程的项目中统一导入依赖 Dubbo 的依赖：

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

上面之所以要排除 curator-framework 中的 zookeeper，然后再次进行引入，是因为默认情况下 curator-framework 自动引用的最新的 3.5.x 的 zookeeper，但我本地安装是 3.4.x 的 zookeeper （因为我安装时候 zookeeper 3.5 还是 beta 版本），此时会出现 KeeperException$UnimplementedException 异常。因为  ZooKeeper 3.5.x 和 ZooKeeper 3.4.x 存在不兼容的情况，详见官方说明 [ZooKeeper Version Compatibility](https://curator.apache.org/zk-compatibility.html) 。

## 三、公共模块

- api 下为公共的调用接口；
- bean 下为公共的实体类。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/dubbo-common.png"/> </div>
## 四、 服务提供者

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/dubbo-provider.png"/> </div>
### 4.1  开发服务

productService 是服务的提供者，其实现的接口 IProductService 来源于公共模块，这里商品数据用模拟数据展示：

```java
@Service
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

### 4.2 暴露服务

在 `dubbo.xml` 中暴露服务：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://dubbo.apache.org/schema/dubbo
       http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="dubbo-provider"/>

    <!-- 使用 zookeeper 注册中心暴露服务地址 如果是集群配置 用, 分隔地址 -->
    <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"/>

    <!-- 用 dubbo 协议在 20880 端口暴露服务 -->
    <dubbo:protocol name="dubbo" port="20880"/>

    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.heibaiying.api.IProductService" ref="productService"/>

    <!-- 和本地 bean 一样实现服务 -->
    <bean id="productService" class="com.heibaiying.service.ProductService"/>
</beans>
```

## 五、服务消费者

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/dubbo-consumer.png"/> </div>
### 5.1 调用服务

在 `dubbo.xml` 中调用远程的服务：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://dubbo.apache.org/schema/dubbo
       http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="dubbo-consumer">
        <dubbo:parameter key="shutdown.timeout" value="60000"/> <!-- 单位毫秒 -->
    </dubbo:application>

    <!--Dubbo 缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题，默认 check="true"。-->
    <!--可以关闭所有服务的启动时检查 -->
    <dubbo:consumer check="false" />

    <!-- 使用 zookeeper 注册中心暴露发现服务地址 -->
    <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"/>

    <!-- 生成远程服务代理，可以和本地 bean 一样使用 demoService -->
    <dubbo:reference id="sellService" interface="com.heibaiying.api.IProductService"/>


</beans>
```

### 5.2 消费服务

```java
@Controller
@RequestMapping("sell")
public class SellController {

    @Autowired
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

在项目中，consumer 和 provider 模块均依赖公共模块，所以在构建 consumer 和 provider 模块前需要将 common 模块安装到本地仓库，依次对 父工程 和 common 模块执行以下命令：

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
