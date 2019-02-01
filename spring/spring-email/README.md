# spring 邮件发送（xml配置方式）
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-依赖说明">1.2 依赖说明</a><br/>
<a href="#二spring-email">二、spring email</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-邮件发送配置">2.1 邮件发送配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-新建邮件发送基本类">2.2 新建邮件发送基本类</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-邮件发送的测试">2.3 邮件发送的测试</a><br/>
## 正文<br/>


## 一、说明

### 1.1 项目结构说明

1. 邮件发送配置文件为springApplication.xml;
2. 简单邮件发送、附件邮件发送、内嵌资源邮件发送、模板邮件发送的方法封装在SpringMail类中；
3. 项目以单元测试的方法进行测试，测试类为SendEmail。



<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-email.png"/> </div>



### 1.2 依赖说明

除了spring的基本依赖外，需要导入邮件发送的支持包spring-context-support

```xml
 <!--邮件发送依赖包-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>${spring-base-version}</version>
</dependency>
 <!--模板引擎-->
        <!--这里采用的是beetl,beetl性能很卓越并且功能也很全面 官方文档地址 <a href="http://ibeetl.com/guide/#beetl">-->
<dependency>
    <groupId>com.ibeetl</groupId>
    <artifactId>beetl</artifactId>
    <version>2.9.7</version>
</dependency>
```



## 二、spring email

#### 2.1 邮件发送配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.heibaiying.email"/>

    <!--在这里可以声明不同的邮件服务器主机，通常是SMTP主机,而具体的用户名和时授权码则建议在业务中从数据库查询-->
    <bean id="qqMailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
        <!--qq 邮箱配置 <a href="https://service.mail.qq.com/cgi-bin/help?subtype=1&no=167&id=28"> -->
        <property name="host" value="smtp.qq.com"/>
        <property name="port" value="587"/>
    </bean>

    <!--配置beetle模板引擎 如果不使用模板引擎，以下的配置不是必须的-->
    <bean id="resourceLoader" class="org.beetl.core.resource.ClasspathResourceLoader">
        <!--指定加载模板资源的位置 指定在classpath:beetl下-->
        <constructor-arg name="root" value="beetl"/>
    </bean>
    <!--beetl 配置  这里采用默认的配置-->
    <bean id="configuration" class="org.beetl.core.Configuration" init-method="defaultConfiguration"/>
    <bean id="groupTemplate" class="org.beetl.core.GroupTemplate">
        <constructor-arg name="loader" ref="resourceLoader"/>
        <constructor-arg name="conf" ref="configuration"/>
    </bean>

</beans>
```

#### 2.2 新建邮件发送基本类

```java
/**
 * @author : heibaiying
 * @description : 邮件发送基本类
 */
@Component
public class SpringMail {

    @Autowired
    private JavaMailSenderImpl qqMailSender;
    @Autowired
    private GroupTemplate groupTemplate;

    /**
     * 发送简单邮件
     * 在qq邮件发送的测试中，测试结果表明不管是简单邮件还是复杂邮件都必须指定发送用户，
     * 且发送用户已经授权不然都会抛出异常: SMTPSendFailedException 501 mail from address must be same as authorization user
     * qq 的授权码 可以在 设置/账户/POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务 中开启服务后获取
     */
    public void sendTextMessage(String from, String authWord, String to, String subject, String content) {
        // 设置发送人邮箱和授权码
        qqMailSender.setUsername(from);
        qqMailSender.setPassword(authWord);
        // 实例化消息对象
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        try {
            // 发送消息
            this.qqMailSender.send(msg);
            System.out.println("发送邮件成功");
        } catch (MailException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

    /**
     * 发送带附件的邮件
     */
    public void sendEmailWithAttachments(String from, String authWord, String to,
                                         String subject, String content, Map<String, File> files) {
        try {
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 需要指定第二个参数为true 代表创建支持可选文本，内联元素和附件的多部分消息
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            // 传入附件
            for (Map.Entry<String, File> entry : files.entrySet()) {
                helper.addAttachment(entry.getKey(), entry.getValue());
            }
            // 发送消息
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }


    /**
     * 发送带内嵌资源的邮件
     */
    public void sendEmailWithInline(String from, String authWord, String to,
                                    String subject, String content, File file) {
        try {
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 需要指定第二个参数为true 代表创建支持可选文本，内联元素和附件的多部分消息
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 使用true标志来指示包含的文本是HTML 固定格式资源前缀 cid:
            helper.setText("<html><body><img src='cid:image'></body></html>", true);
            // 需要先指定文本 再指定资源文件
            FileSystemResource res = new FileSystemResource(file);
            helper.addInline("image", res);
            // 发送消息
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

    /**
     * 使用模板邮件
     */
    public void sendEmailByTemplate(String from, String authWord, String to,
                                    String subject, String content) {
        try {
            Template t = groupTemplate.getTemplate("template.html");
            t.binding("subject", subject);
            t.binding("content", content);
            String text = t.render();
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 指定 utf-8 防止乱码
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 为true 时候 表示文本内容以 html 渲染
            helper.setText(text, true);
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

}

```

**关于模板邮件的说明：**

- 模板引擎最主要的作用是，在对邮件格式有要求的时候，采用拼接字符串不够直观，所以采用模板引擎；

- 这里我们使用的beetl模板引擎，原因是其性能优异，官网是介绍其性能6倍与freemaker,并有完善的文档支持。当然大家也可以换成任何其他的模板引擎（freemarker,thymeleaf）

  一个简单的模板template.html如下：

```html
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <h1>邮件主题:<span style="color: chartreuse"> ${subject}</span></h1>
    <h4 style="color: blueviolet">${content}</h4>
</body>
</html>
```



#### 2.3 邮件发送的测试

```java
/**
 * @author : heibaiying
 * @description : 发送邮件测试类
 */
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class SendEmail {

    @Autowired
    private SpringMail springMail;

    // 发送方邮箱地址
    private static final String from = "发送方邮箱地址@qq.com";
    // 发送方邮箱地址对应的授权码
    private static final String authWord = "授权码";
    // 接收方邮箱地址
    private static final String to = "接收方邮箱地址@qq.com";


    /**
     * 简单邮件测试
     */
    @Test
    public void sendMessage() {
        springMail.sendTextMessage(from, authWord, to, "spring简单邮件", "Hello Spring Email!");
    }

    /**
     * 发送带附件的邮件
     */
    @Test
    public void sendComplexMessage() {
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("image1.jpg", new File("D:\\LearningNotes\\picture\\msm相关依赖.png"));
        fileMap.put("image2.jpg", new File("D:\\LearningNotes\\picture\\RabbitMQ模型架构.png"));
        springMail.sendEmailWithAttachments(from, authWord, to, "spring多附件邮件"
                , "Hello Spring Email!", fileMap);
    }

    /**
     * 发送内嵌资源的邮件
     */
    @Test
    public void sendEmailWithInline() {
        springMail.sendEmailWithInline(from, authWord, to, "spring内嵌资源邮件"
                , "Hello Spring Email!", new File("D:\\LearningNotes\\picture\\RabbitMQ模型架构.png"));
    }

    /**
     * 发送模板邮件
     */
    @Test
    public void sendEmailByTemplate() {
        springMail.sendEmailByTemplate(from, authWord, to,
                "spring模板邮件", "Hello Spring Email!");
    }
}

```
