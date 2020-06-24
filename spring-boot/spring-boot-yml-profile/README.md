# Spring Boot YAML

<nav>
<a href="#一项目结构">一、项目结构</a><br/>
<a href="#二YAML-语法">二、YAML 语法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-基本规则">2.1 基本规则</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-对象的写法">2.2 对象的写法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-Map的写法">2.3 Map的写法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-数组的写法">2.4 数组的写法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25-单双引号">2.5 单双引号</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#26-特殊符号">2.6 特殊符号</a><br/>
<a href="#三Spring-Boot-与-YAML">三、Spring Boot 与 YAML</a><br/>
<a href="#四ConfigurationProperties">四、@ConfigurationProperties</a><br/>
<a href="#五多环境配置文件">五、多环境配置文件</a><br/>
<a href="#六优先级的说明">六、优先级的说明</a><br/>
</nav>

## 一、项目结构

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-boot-yml-profile.png"/> </div>

## 二、YAML 语法

Spring Boot 支持使用 Yaml 语法来书写配置文件，相比于 properties 文件键值对的配置格式，Yaml 语法的配置更加精简，层次也更加分明。其基本规则和语法如下：

### 2.1 基本规则

- 大小写敏感 。 
- 使用缩进表示层级关系 。 
- 缩进长度没有限制，只要元素对齐就表示这些元素属于一个层级。 
- 使用#表示注释 。 
- 字符串默认不用加单双引号，但单引号和双引号都可以使用，双引号不会对特殊字符转义。
- YAML 中提供了多种常量结构，包括：整数，浮点数，字符串，NULL，日期，布尔，时间。

### 2.2 对象的写法

支持使用键值对的格式来配置对象属性，但 `:` 符号后面需要有一个空格：

```yaml
key: value
```

### 2.3 Map的写法

```yaml
# 写法一 同一缩进的所有键值对属于一个map
key: 
    key1: value1
    key2: value2

# 写法二
{key1: value1, key2: value2}
```

### 2.4 数组的写法

```yaml
# 写法一 使用一个短横线加一个空格代表一个数组项
- a
- b
- c

# 写法二
[a,b,c]
```

### 2.5 单双引号

Yaml 支持单引号和双引号，但双引号不会对特殊字符转义：

```yaml
s1: '内容\n 字符串'
s2: "内容\n 字符串"

转换后：
{ s1: '内容\\n 字符串', s2: '内容\n 字符串' }
```

### 2.6 特殊符号

可以在同一个文件中包含多个 YAML 文档，并使用 `---` 进行分割。

## 三、Spring Boot 与 YAML

Spring Boot 中支持使用 ${app.name} 引用预先定义的值：

```properties
appName: MyApp
appDescription: ${app.name} is a Spring Boot application
```

Spring Boot 支持使用 ${random.xxx} 配置随机值：

```properties
my.secret: ${random.value}
my.number: ${random.int}
my.bignumber: ${random.long}
my.number.less.than.ten: ${random.int(10)}
my.number.in.range: ${random.int[1024,65536]}
```



## 四、@ConfigurationProperties

Spring Boot 支持使用 @ConfigurationProperties 注解来将 Yaml 中的配置与实体类进行绑定，示例如下：

```java
@Component
@ConfigurationProperties(prefix = "programmer")
@Data
@ToString
public class Programmer {

    private String name;
    private int age;
    private boolean married;
    private Date hireDate;
    private float salary;
    private int random;
    private Map<String, String> skill;
    private List company;
    private School school;

}
```

```yaml
programmer:
  name: xiaoming-DEV
  married: false
  hireDate: 2018/12/23
  salary: 66666.88
  random: ${random.int[1024,65536]}
  skill: {java: master, jquery: proficiency}
  company: [baidu,tengxun,alibaba]
  school:
    name: unviersity
    location: shanghai
```

Spring Boot 在将环境属性绑定到 `@ConfigurationProperties` beans 时会使用一些宽松的规则，称之为松散绑定。因此配置的属性名和 bean 属性名不需要精确匹配。它会自动执行一些驼峰转换或大小写转换，例如将 context-path 绑定到 contextPath，或将 PORT 绑定 port 。

但需要注意的是只有 `@ConfigurationProperties` 注解才支持松散绑定，而另一个属性绑定注解  `@Value` 是不支持松散绑定的，所以除非有特殊的需求，否则建议保持类中的属性名和配置文件中的属性名完全一致。



## 五、多环境配置文件

可以在同一个 yml 文件中包含多个配置文件，并使用 `---` 进行分割。或者遵循 application-xxx.yml 命名方式来为不同的环境（如开发环境，生产环境，测试环境）分别生成不同的配置文件，然后再在主配置文件 application.yml 中来决定使用哪个具体的配置，或在启动时候通过命令行参数来决定，命令行的优先级大于配置文件的优先级。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/profile.png"/> </div>

```yaml
# 配置文件中激活开发环境配置
spring:
  profiles:
    active: dev
```

```shell
# 启动项目时候在命令行中通过参数进行激活
--spring.profiles.active=dev
```



## 六、优先级的说明

Spring Boot 支持在多个地方进行配置的定义，按照配置方式的不同，属性优先级由高到低的顺序如下：

1. home 目录下的 devtools 全局设置属性（~/.spring-boot-devtools.properties，如果 devtools 激活）。
2. 测试用例上的@TestPropertySource 注解。
3. 测试用例上的@SpringBootTest#properties 注解。
4. 命令行参数
5. 来自 SPRING_APPLICATION_JSON 的属性（环境变量或系统属性中内嵌的内联 JSON）。
6. ServletConfig 初始化参数。
7. ServletContext 初始化参数。
8. 来自于 java:comp/env 的 JNDI 属性。
9. Java 系统属性（System.getProperties()）。
10. 操作系统环境变量。
11. RandomValuePropertySource，只包含 random.*中的属性。
12. 没有打进 jar 包的 Profile-specific 应用属性（application-{profile}.properties 和 YAML 变量）
13. 打进 jar 包中的 Profile-specific 应用属性（application-{profile}.properties 和 YAML 变量）。
14. 没有打进 jar 包的应用配置（application.properties 和 YAML 变量）。
15. 打进 jar 包中的应用配置（application.properties 和 YAML 变量）。
16. @Configuration 类上的@PropertySource 注解。
17. 默认属性（使用 SpringApplication.setDefaultProperties 指定）。

这里做一点说明：上文第 12 和 14 点没有打进 JAR 包的文件指的是在启动时候通过 `spring.config.location` 参数指定的外部配置文件，外部配置文件的优先级大于 JAR 中配置文件的优先级。另外，常用的优先级规则可以精简如下：

```shell
命令行 > application-{profile}.yml > application.yml > 默认属性
```
