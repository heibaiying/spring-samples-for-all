# spring boot actuator

## 目录<br/>
<a href="#一用例涉及到的概念综述">一、用例涉及到的概念综述</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-端点">1.1 端点</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-启用端点">1.2 启用端点</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-暴露端点">1.3 暴露端点</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#14-健康检查信息">1.4 健康检查信息</a><br/>
<a href="#二项目说明">二、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-主要依赖">1.2 主要依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-项目配置">1.3 项目配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#14-查看监控状态">1.4 查看监控状态</a><br/>
<a href="#三自定义健康检查指标">三、自定义健康检查指标</a><br/>
<a href="#四自定义健康状态聚合规则">四、自定义健康状态聚合规则</a><br/>
<a href="#五Endpoint自定义端点">五、@Endpoint自定义端点</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#51-自定义端点">5.1 自定义端点</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#52-访问自定义端点http//1270018080/actuator/customEndPoint">5.2 访问自定义端点http://127.0.0.1:8080/actuator/customEndPoint</a><br/>
## 正文<br/>




## 一、用例涉及到的概念综述

### 1.1 端点

执行器端点（endpoints）可用于监控应用及与应用进行交互，Spring  Boot 包含很多内置的端点，你也可以添加自己的。例如，health 端点提供了应用的基本健康信息。  端点暴露的方式取决于你采用的技术类型，大部分应用选择 HTTP 监控，端点的 ID 映射到一个 URL。例如，health 端点默认映射到/health。

下面的端点都是可用的：

| ID          | 描述                                                         | 是否敏感 |
| ----------- | ------------------------------------------------------------ | -------- |
| actuator    | 为其他端点提供基于超文本的导航页面，需要添加 Spring HATEOAS 依赖 | true     |
| autoconfig  | 显示一个自动配置类的报告，该报告展示所有自动配置候选者及它们被应用或未被应用的原因 | true     |
| beans       | 显示一个应用中所有 Spring Beans 的完整列表                     | true     |
| configprops | 显示一个所有@ConfigurationProperties 的集合列表               | true     |
| dump        | 执行一个线程转储                                             | true     |
| env         | 暴露来自 Spring ConfigurableEnvironment 的属性                 | true     |
| flyway      | 显示数据库迁移路径，如果有的话                               | true     |
| health      | 展示应用的健康信息（当使用一个未认证连接访问时显示一个简单的'status'，使用认证连接访问则显示全部信息详情） | false    |
| info        | 显示任意的应用信息                                           | false    |
| liquibase   | 展示任何 Liquibase 数据库迁移路径，如果有的话                  | true     |
| metrics     | 展示当前应用的'metrics'信息                                  | true     |
| mappings    | 显示一个所有@RequestMapping 路径的集合列表                    | true     |
| shutdown    | 允许应用以优雅的方式关闭（默认情况下不启用）                 | true     |
| trace       | 显示 trace 信息（默认为最新的 100 条 HTTP 请求）                   | true     |

如果使用 Spring MVC，你还可以使用以下端点：

| ID       | 描述                                                         | 是否敏感 |
| -------- | ------------------------------------------------------------ | -------- |
| docs     | 展示 Actuator 的文档，包括示例请求和响应，需添加 spring-boot-actuator-docs 依赖 | false    |
| heapdump | 返回一个 GZip 压缩的 hprof 堆转储文件                            | true     |
| jolokia  | 通过 HTTP 暴露 JMX beans（依赖 Jolokia）                         | true     |
| logfile  | 返回日志文件内容（如果设置 logging.file 或 logging.path 属性），支持使用 HTTP Range 头接收日志文件内容的部分信息 |          |

注：根据端点暴露的方式，sensitive 属性可用做安全提示，例如，在使用 HTTP 访问敏感（sensitive）端点时需要提供用户名/密码（如果没有启用 web 安全，可能会简化为禁止访问该端点）。



### 1.2 启用端点

默认情况下，除了以外的所有端点 shutdown 都已启用。要配置端点的启用，请使用其 management.endpoint.<id>.enabled 属性。以下示例启用 shutdown 端点：

```properties
management.endpoint.shutdown.enabled = true
```



### 1.3 暴露端点

由于端点可能包含敏感信息，因此应仔细考虑何时公开它们。下表显示了内置端点的默认曝光情况：

| ID             | JMX   | Web  |
| -------------- | ----- | ---- |
| auditevents    | 是    | 没有 |
| beans          | 是    | 没有 |
| conditions     | 是    | 没有 |
| configprops    | 是    | 没有 |
| env            | 是    | 没有 |
| flyway         | 是    | 没有 |
| health         | 是    | 是   |
| heapdump       | N / A | 没有 |
| httptrace      | 是    | 没有 |
| info           | 是    | 是   |
| jolokia        | N / A | 没有 |
| logfile        | N / A | 没有 |
| loggers        | 是    | 没有 |
| liquibase      | 是    | 没有 |
| metrics        | 是    | 没有 |
| mappings       | 是    | 没有 |
| prometheus     | N / A | 没有 |
| scheduledtasks | 是    | 没有 |
| sessions       | 是    | 没有 |
| shutdown       | 是    | 没有 |
| threaddump     | 是    | 没有 |

**可以选择是否暴露端点（include）或者排除端点（exclude）,其中排除优先于暴露：**

| 属性                                      | 默认         |
| ----------------------------------------- | ------------ |
| management.endpoints.jmx.exposure.exclude |              |
| management.endpoints.jmx.exposure.include | *            |
| management.endpoints.web.exposure.exclude |              |
| management.endpoints.web.exposure.include | info, health |



### 1.4 健康检查信息

您可以使用健康信息来检查正在运行的应用程序的状态。health 端点公开的信息取决于 management.endpoint.health.show-details 可以使用以下值之一配置的属性：

| 名称            | 描述                                                         |
| --------------- | ------------------------------------------------------------ |
| never           | 细节永远不会显示。                                           |
| when-authorized | 详细信息仅向授权用户显示。授权角色可以使用配置 management.endpoint.health.roles。 |
| always          | 详细信息显示给所有用户。                                     |



## 二、项目说明

#### 1.1 项目结构说明

1. CustomHealthIndicator 自定义健康指标；
2. CustomHealthAggregator：自定义健康聚合规则；
3. CustomEndPoint：自定义端点。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-actuator.png"/> </div>

#### 1.2 主要依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 1.3 项目配置

```yaml
management:
  endpoints:
    web:
      exposure:
        # 这里用* 代表暴露所有端点只是为了观察效果，实际中按照需进行端点暴露
        include: "*"
  endpoint:
    health:
      # 详细信息显示给所有用户。
      show-details: always
  health:
    status:
      http-mapping:
        # 自定义健康检查返回状态码对应的 http 状态码
        FATAL:  503
```

#### 1.4 查看监控状态

导入 actuator 的 start 并进行配置后，访问 http://127.0.0.1:8080/actuator/health 就可以看到对应的项目监控状态。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/health.png"/> </div>

需要注意的是这里的监控状态根据实际项目所用到的技术不同而不同。因为以下 HealthIndicators 情况在适当时由 Spring Boot 自动配置的：

| 名称                                                         | 描述                             |
| ------------------------------------------------------------ | -------------------------------- |
| [CassandraHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/cassandra/CassandraHealthIndicator.java) | 检查 Cassandra 数据库是否启动。    |
| [DiskSpaceHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/system/DiskSpaceHealthIndicator.java) | 检查磁盘空间不足。               |
| [DataSourceHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/jdbc/DataSourceHealthIndicator.java) | 检查是否可以获得连接 DataSource。 |
| [ElasticsearchHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/elasticsearch/ElasticsearchHealthIndicator.java) | 检查 Elasticsearch 集群是否启动。  |
| [InfluxDbHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/influx/InfluxDbHealthIndicator.java) | 检查 InfluxDB 服务器是否启动。     |
| [JmsHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/jms/JmsHealthIndicator.java) | 检查 JMS 代理是否启动。            |
| [MailHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/mail/MailHealthIndicator.java) | 检查邮件服务器是否启动。         |
| [MongoHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/mongo/MongoHealthIndicator.java) | 检查 Mongo 数据库是否启动。        |
| [Neo4jHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/neo4j/Neo4jHealthIndicator.java) | 检查 Neo4j 服务器是否启动。        |
| [RabbitHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/amqp/RabbitHealthIndicator.java) | 检查 Rabbit 服务器是否启动。       |
| [RedisHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/redis/RedisHealthIndicator.java) | 检查 Redis 服务器是否启动。        |
| [SolrHealthIndicator](https://github.com/spring-projects/spring-boot/tree/v2.0.1.RELEASE/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/solr/SolrHealthIndicator.java) | 检查 Solr 服务器是否已启动。       |



## 三、自定义健康检查指标

```java
/**
 * @author : heibaiying
 * @description : 自定义健康检查指标
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        double random = Math.random();
        // 这里用随机数模拟健康检查的结果
        if (random > 0.5) {
            return Health.status("FATAL").withDetail("error code", "某健康专项检查失败").build();
        } else {
            return Health.up().withDetail("success code", "自定义检查一切正常").build();
        }

    }
}
```

自定义检查通过的情况下：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/actuator-health-up.png"/> </div>

自定义检查失败的情况：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/health-fatal-200.png"/> </div>



这里我们可以看到自定义检查不论是否通过都不会影响整体的 status,两种情况下都是 status 都是“up”。如果我们想通过自定义的检查检查去影响最终的检查结果，比如我们健康检查针对的是支付业务，在支付业务的不可用的情况下，我们就认为整个服务是不可用的。这个时候就需要实现自定义实现健康状态的聚合。



## 四、自定义健康状态聚合规则

```java
/**
 * @author : heibaiying
 * @description : 对所有的自定义健康指标进行聚合，按照自定义规则返回总和健康状态
 */
@Component
public class CustomHealthAggregator implements HealthAggregator {

    @Override
    public Health aggregate(Map<String, Health> healths) {
        for (Health health : healths.values()) {
            // 聚合规则可以自定义,这里假设我们自定义的监控状态中有一项 FATAL,就认为整个服务都是不可用的,否则认为整个服务是可用的
            if (health.getStatus().getCode().equals("FATAL")) {
                return Health.status("FATAL").withDetail("error code", "综合判断后服务宕机").build();
            }
        }
        return Health.up().build();
    }
}
```

当我们自定义健康检查不通过时候的结果如下：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/actuator-heath-503.png"/> </div>

这里需要注意的是返回我们自定义的聚合状态的时候，状态码也变成了 503,这是我们在配置文件中进行定义的：

```properties
management.health.status.http-mapping.FATAL = 503
```

下表显示了内置状态的默认状态映射：

| Status         | Mapping                                      |
| -------------- | -------------------------------------------- |
| DOWN           | SERVICE_UNAVAILABLE (503)                    |
| OUT_OF_SERVICE | SERVICE_UNAVAILABLE (503)                    |
| UP             | No mapping by default, so http status is 200 |
| UNKNOWN        | No mapping by default, so http status is 200 |



## 五、@Endpoint自定义端点

#### 5.1 自定义端点

spring boot 支持使用@Endpoint 来自定义端点暴露应用信息。这里我们采用第三方 sigar 来暴露服务所在硬件的监控信息。

Sigar 是 Hyperic-hq 产品的基础包，是 Hyperic HQ 主要的数据收集组件。Sigar.jar 的底层是用 C 语言编写的，它通过本地方法来调用操作系统 API 来获取系统相关数据 [jar 包下载地址](https://sourceforge.net/projects/sigar/)。

Sigar 为不同平台提供了不同的库文件,下载后需要将库文件放到服务所在主机的对应位置：

- Windows 下配置：根据自己的操作系统版本选择 sigar-amd64-winnt.dll 或 sigar-x86-winnt.dll 拷贝到 C:\Windows\System32 中

- Linux 下配置：将 libsigar-amd64-linux.so 或 libsigar-x86-linux.so 拷贝到/usr/lib64 或/lib64 或/lib 或/usr/lib 目录下，如果不起作用，还需要 sudochmod 744 修改 libsigar-amd64-linux.so 文件权限

```java
@Endpoint(id = "customEndPoint")
@Component
public class CustomEndPoint {

    @ReadOperation
    public Map<String, Object> getCupInfo() throws SigarException {

        Map<String, Object> cupInfoMap = new LinkedHashMap<>();

        Sigar sigar = new Sigar();

        CpuInfo infoList[] = sigar.getCpuInfoList();
        CpuPerc[] cpuList = sigar.getCpuPercList();

        for (int i = 0; i < infoList.length; i++) {
            CpuInfo info = infoList[i];
            cupInfoMap.put("CPU " + i + " 的总量 MHz", info.getMhz());                            // CPU 的总量 MHz
            cupInfoMap.put("CPU " + i + " 生产商", info.getVendor());                            // 获得 CPU 的生产商，如：Intel
            cupInfoMap.put("CPU " + i + " 类别", info.getModel());                               // 获得 CPU 的类别，如：Core
            cupInfoMap.put("CPU " + i + " 缓存数量", info.getCacheSize());                       // 缓冲存储器数量
            cupInfoMap.put("CPU " + i + " 用户使用率", CpuPerc.format(cpuList[i].getUser()));    // 用户使用率
            cupInfoMap.put("CPU " + i + " 系统使用率", CpuPerc.format(cpuList[i].getSys()));     // 系统使用率
            cupInfoMap.put("CPU " + i + " 当前等待率", CpuPerc.format(cpuList[i].getWait()));    // 当前等待率
            cupInfoMap.put("CPU " + i + " 当前错误率", CpuPerc.format(cpuList[i].getNice()));    // 当前错误率
            cupInfoMap.put("CPU " + i + " 当前空闲率", CpuPerc.format(cpuList[i].getIdle()));    // 当前空闲率
            cupInfoMap.put("CPU " + i + " 总的使用率", CpuPerc.format(cpuList[i].getCombined()));// 总的使用率
        }
        return cupInfoMap;
    }

}
```

其中可用的方法注解由 http 操作决定：

| operation        | HTTP 方法 |
| ---------------- | -------- |
| @ReadOperation   | GET      |
| @WriteOperation  | POST     |
| @DeleteOperation | DELETE   |

#### 5.2 访问自定义端点http://127.0.0.1:8080/actuator/customEndPoint

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/actuator-customEndPoint.png"/> </div>



关于 Sigar 的 更多监控参数可以参考博客：[java 读取计算机 CPU、内存等信息（Sigar 使用）](https://blog.csdn.net/wudiazu/article/details/73829324)

Sigar 下载包中也提供了各种参数的参考用例：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/sigar.png"/> </div>
