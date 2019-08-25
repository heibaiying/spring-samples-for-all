# Spring Boot 集成 Swagger2 打造在线接口文档
<nav>
<a href="#一概念综述">一、概念综述</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-SpringFox">1.1 SpringFox </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-Swagger">1.2 Swagger</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-关联关系">1.3 关联关系</a><br/>
<a href="#二集成-Swagger-20">二、集成 Swagger 2.0</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-基本依赖">2.1 基本依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-Swagger--配置">2.2 Swagger  配置 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-Swagger-注解">2.3 Swagger 注解 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-可视化接口文档">2.4 可视化接口文档</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25-接口测试">2.5 接口测试</a><br/>
</nav>

## 一、概念综述

### 1.1 SpringFox 

SpringFox 是一个开源的 API 文档框架， 它的前身是 Swagger-SpringMVC ，能够完美的支持 SpringMVC 并将其中接口方法自动转换为接口文档。 目前 SpringFox 正致力于对更多 JSON API 规范和标准的扩展和支持，例如：[swagger](http://swagger.io/)，[RAML](http://raml.org/) 和 [jsonapi](http://jsonapi.org/)。

### 1.2 Swagger

Swagger 是一个规范框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务，支持 API 全生命周期的开发和管理。它是一个综合的开源项目，包含 [swagger-core](https://github.com/swagger-api/swagger-core)、[swagger-ui](https://github.com/swagger-api/swagger-ui)、[swagger-codegen](https://github.com/swagger-api/swagger-codegen)、[swagger-editor](https://github.com/swagger-api/swagger-editor) 等多个子项目：

+ **swagger-core**：Swagger Core 是 OpenAPI 规范（以前称为 Swagger 规范）的 Java 实现。

+ **swagger-ui**：根据可视化文档，提供与 API 资源的可视化交互。

+ **swagger-codegen**：开源的代码生成器，根据 Swagger 定义的 RESTful API 可以自动建立服务端和客户端的连接。

+ **swagger-editor**：开源的 API 文档编辑器。

下图为 swagger-ui 提供的文档可视化界面示例：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/Swagger_UI.png"/> </div>


### 1.3 关联关系

OpenAPI、Swagger、SpringFox 之间的关联关系可以表述为：**Swagger Core 是 Open API 规范的 Java 实现，而 SpringFox 则提供了 Swagger 与 Spring 的集成支持**。<br/>



## 二、集成 Swagger 2.0

### 2.1 基本依赖

```xml
<!--swagger2-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<!--swagger-ui -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

### 2.2 Swagger  配置 

进行 Swagger 个性化配置，并使用 @EnableSwagger2 开启 Swagger 支持。另外，虽然 Swagger 是一个非常易用的接口调试插件，但是有可能会导致接口信息泄露，所以建议在开发环境和测试环境开启，但在生产环境关闭。这里一共给出三种 Swagger 开启和关闭的切换方法：

1. 如下面代码所示，在配置文件中增加自定义的开关参数，并在创建 Docket 时，在链式调用的 enable() 方法中传入。
2. 在 SwaggerConfig 配置类上添加 `@Profile({"dev","test"}) ` 注解，指明仅在开发环境和测试环境下激活此配置，并在打包部署时使用 spring.profiles.active 指明具体的环境。
3. 在配置文件中配置自定义的开关参数，并在 SwaggerConfig 配置类上添加 `@ConditionalOnProperty(name = "swagger.enable", havingValue = "true") `，指明配置类的生效条件。@ConditionalOnProperty 注解能够控制某个配置类是否生效。具体操作是通过 name 和 havingValue 属性来实现，name 对应配置文件中的某个属性，如果该值为空，则返回 false；如果值不为空，则将该值与 havingValue 指定的值进行比较，如果一样则返回 true；否则返回 false。如果返回值为 false，则对应的配置不生效；为 true 则生效。

以下是第一种开关配置方式的使用示例：

```java
/**
 * @description :  Swagger 配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean swaggerEnable;

    /***
     * 配置 swagger
     * 开发和测试环境下可以开启 swagger 辅助进行调试,而生产环境下可以关闭或者进行相应的权限控制，防止接口信息泄露
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnable)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.heibaiying.springboot.controller"))
                .paths(PathSelectors.any())
                .paths(doFilteringRules())
                .build();
    }

    /***
     * 接口文档的描述信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot swagger2 用例")
                .description("描述")
                .licenseUrl("https://mit-license.org/")
                .version("1.0")
                .build();
    }

    /**
     * 可以使用正则定义 url 过滤规则
     */
    private Predicate<String> doFilteringRules() {
        return not(
                regex("/ignore/*")
        );
    }
}
```

application.properties :

```properties
#swagger启用开关
swagger.enable = true
```



### 2.3 Swagger 注解 

```java
@Slf4j
@Api(value = "产品接口", description = "产品信息接口")
@RestController
public class ProductController {

    /***
     * 一个标准的 swagger 注解
     */
    @ApiOperation(notes = "查询所有产品", value = "产品查询接口")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "产品编号", paramType = "path", defaultValue = "1")
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "无效的请求"),
            @ApiResponse(code = 401, message = "未经过授权认证"),
            @ApiResponse(code = 403, message = "已经过授权认证，但是没有该资源对应的访问权限"),
            @ApiResponse(code = 404, message = "服务器找不到给定的资源,商品不存在"),
            @ApiResponse(code = 500, message = "服务器错误")
    })
    @GetMapping(value = "/product/{id}", produces = "application/json")
    public ResponseEntity<Product> getProduct(@PathVariable long id) {
        Product product = new Product(id, "product" + id, new Date());
        return ResponseEntity.ok(product);
    }


    /***
     * 如果用实体类接收参数,则用实体类对应的属性名称指定参数
     */
    @ApiOperation(notes = "保存产品", value = "产品保存接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "产品编号", paramType = "body", defaultValue = "1"),
            @ApiImplicitParam(name = "name", value = "产品名称", paramType = "body"),
            @ApiImplicitParam(name = "date", value = "产品生产日期", paramType = "body")
    }
    )
    @PostMapping(value = "/product")
    public ResponseEntity<Void> saveProduct(@RequestBody Product product) {
        System.out.println(product);
        return ResponseEntity.ok().build();
    }


    /***
     * 在配置类中指明了该接口不被扫描到,可以在配置类中使用正则指定某一类符合规则的接口不被扫描到
     */
    @ApiOperation(notes = "该接口会被忽略", value = "产品保存接口")
    @PostMapping(value = "/ignore")
    public ResponseEntity<Product> ignore() {
        return ResponseEntity.ok().build();
    }

    /**
     * 不加上任何 swagger 相关的注解也会被扫描到 如果不希望被扫描到，需要用 @ApiIgnore 修饰
     */
    @PostMapping(value = "/normal")
    public ResponseEntity<Void> normal() {
        return ResponseEntity.ok().build();
    }

    @ApiIgnore
    @PostMapping(value = "/apiIgnore")
    public ResponseEntity<Void> apiIgnore() {
        return ResponseEntity.ok().build();
    }
}
```

Swagger 为了最大程度防止对逻辑代码的侵入，基本都是依靠注解来完成文档描述。常用注解如下：

| Annotation       | Attribute    | Target Property           | Description                                                  |
| ---------------- | ------------ | ------------------------- | ------------------------------------------------------------ |
| RequestHeader    | defaultValue | Parameter#defaultValue    | e.g. `@RequestHeader(defaultValue="${param1.defaultValue}")` |
| ApiModelProperty | value        | ModelProperty#description | e.g. `@ApiModelProperty(value="${property1.description}")`   |
| ApiModelProperty | description  | ModelProperty#description | e.g. `@ApiModelProperty(notes="${property1.description}")`   |
| ApiParam         | value        | Parameter#description     | e.g. `@ApiParam(value="${param1.description}")`              |
| ApiImplicitParam | value        | Parameter#description     | e.g. `@ApiImplicitParam(value="${param1.description}")`      |
| ApiOperation     | notes        | Operation#notes           | e.g. `@ApiOperation(notes="${operation1.description}")`      |
| ApiOperation     | summary      | Operation#summary         | e.g. `@ApiOperation(value="${operation1.summary}")`          |
| RequestParam     | defaultValue | Parameter#defaultValue    | e.g. `@RequestParam(defaultValue="${param1.defaultValue}")`  |

1. `@Api`：用在类上，说明该类的作用；

2. `@ApiOperation`：用在方法上，说明方法的作用；

3. `@ApiParam`：用在参数上，说明参数的作用；

4. `@ApiImplicitParams`：用在方法上说明方法参数的作用；

5. `@ApiImplicitParam`：用在 @ApiImplicitParams 注解中，描述每个具体参数；

6. `@ApiResponses`：一组 @ApiResponse 的配置；

7. `@ApiResponse`：请求返回的配置；

8. `@ResponseHeader`：响应头的配置；

9. `@ApiModel`：描述一个 Model 的信息（一般用在 post 创建的时候，使用@RequestBody 接收参数的场景）；

10. `@ApiModelProperty`：描述 model 的属性。

11. `@ApiIgnore`：可以用于类、方法、属性，代表该方法、类、属性不被 Swagger 的文档所管理。

    

### 2.4 可视化接口文档

接口文档访问地址：http://localhost:8080/swagger-ui.html ，文档主界面如下：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/swagger-ui-index.png"/> </div>
### 2.5 接口测试

Swagger-UI 除了提供接口可视化的功能外，还可以用于接口测试。点击对应接口的 `try it out` 按钮，然后输入对应的参数的值，最后点击下方的 `Execute` 按钮发送请求：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/swagger-try-it.png"/> </div>

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/swagger-execute.png"/> </div>
POST 接口可以直接修改 Model 对应的 Json 数据 ，然后点击下方的 `Execute` 按钮发送请求：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/swagger-post-try.png"/> </div>
