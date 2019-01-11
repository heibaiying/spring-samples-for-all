# spring-boot-yml-profile

## 一、项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-yml-profile.png"/> </div>

## 二、常用 yaml 语法讲解

项目中的yml配置文件如下：

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

#### 2.1 基本规则

1. 大小写敏感 
2. 使用缩进表示层级关系 
3. 缩进长度没有限制，只要元素对齐就表示这些元素属于一个层级。 
4. 使用#表示注释 
5. 字符串默认不用加单双引号，但单引号和双引号都可以使用，双引号不会对特殊字符转义。
6. YAML中提供了多种常量结构，包括：整数，浮点数，字符串，NULL，日期，布尔，时间。

#### 2.2 对象的写法

```yaml
key: value
```

#### 2.3 map的写法

```yaml
# 写法一 同一缩进的所有键值对属于一个map
key: 
    key1: value1
    key2: value2

# 写法二
{key1: value1, key2: value2}
```

#### 2.3 数组的写法

```yaml
# 写法一 使用一个短横线加一个空格代表一个数组项
- a
- b
- c

# 写法二
[a,b,c]
```

#### 2.5 单双引号

单引号和双引号都可以使用，双引号不会对特殊字符转义。

```yaml
s1: '内容\n字符串'
s2: "内容\n字符串"

转换后：
{ s1: '内容\\n字符串', s2: '内容\n字符串' }
```

#### 2.6 特殊符号

---  YAML可以在同一个文件中，使用---表示一个文档的开始。



## 三、spring boot 与 yaml

#### 3.1  spring boot 支持使用 ${app.name} 引用预先定义的值

```properties
appName: MyApp
appDescription: ${app.name} is a Spring Boot application
```

#### 3.2 spring boot 支持使用 ${random.xxx} 配置随机值

```properties
my.secret: ${random.value}
my.number: ${random.int}
my.bignumber: ${random.long}
my.number.less.than.ten: ${random.int(10)}
my.number.in.range: ${random.int[1024,65536]}
```



## 四、@ConfigurationProperties实现属性绑定

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

Spring Boot将环境属性绑定到@ConfigurationProperties beans时会使用一些宽松的规则，称之为松散绑定。所以Environment属性名和bean属性名不需要精确匹配。常见的示例中有用的包括虚线分割（比如，context-path绑定到contextPath），将环境属性转为大写字母（比如，PORT绑定port）。

需要注意的是`@Value`是不支持松散绑定的，所以建议除非有特殊的需求，否则在`ConfigurationProperties`和`value` 配置属性的时候最好都保持属性和变量的一致，以免造成不必要的勿扰。



## 五、多配置文件

多配置文件可以在同一个yml中使用 --- 分割为多个配置，或者遵循application-xxx.yml 的方式命名拆分为多个文件，并在主配置文件application.yml 中确定激活哪个配置文件，当然也可在命令行中确定，命令行的优先级大于配置文件。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/profile.png"/> </div>

```yaml
# 配置文件中激活配置
spring:
  profiles:
    active: dev
```

```shell
# 命令行参数激活配置
--spring.profiles.active=dev
```



## 六、优先级的说明

Spring Boot设计了一个非常特别的PropertySource顺序，以允许对属性值进行合理的覆盖，属性会以如下的顺序进行设值：

1. home目录下的devtools全局设置属性（~/.spring-boot-devtools.properties，如果devtools激活）。
2. 测试用例上的@TestPropertySource注解。
3. 测试用例上的@SpringBootTest#properties注解。
4. 命令行参数
5. 来自SPRING_APPLICATION_JSON的属性（环境变量或系统属性中内嵌的内联JSON）。
6. ServletConfig初始化参数。
7. ServletContext初始化参数。
8. 来自于java:comp/env的JNDI属性。
9. Java系统属性（System.getProperties()）。
10. 操作系统环境变量。
11. RandomValuePropertySource，只包含random.*中的属性。
12. 没有打进jar包的Profile-specific应用属性（application-{profile}.properties和YAML变量）
13. 打进jar包中的Profile-specific应用属性（application-{profile}.properties和YAML变量）。
14. 没有打进jar包的应用配置（application.properties和YAML变量）。
15. 打进jar包中的应用配置（application.properties和YAML变量）。
16. @Configuration类上的@PropertySource注解。
17. 默认属性（使用SpringApplication.setDefaultProperties指定）。

这里做一下说明，上文第12,14 点没有打进jar包的文件指的是在启动时候通过`spring.config.location`参数指定的外部配置文件，外部配置文件的优先级应该是大于jar中的配置文件。

对上面的配置中常用的规则可以精简如下：

**命令行 > application-{profile}.yml > application.yml > 默认属性**