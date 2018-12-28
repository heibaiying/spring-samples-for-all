package com.heibaiying;

import com.heibaiying.email.SpringMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void sendMessage() {

        springMail.sendTextMessage(from, authWord, to, "spring简单邮件", "Hello Spring Email!");
    }


    @Test
    public void sendComplexMessage() {
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("image1.jpg", new File("D:\\LearningNotes\\picture\\msm相关依赖.png"));
        fileMap.put("image2.jpg", new File("D:\\LearningNotes\\picture\\RabbitMQ模型架构.png"));
        springMail.sendEmailWithAttachments(from, authWord, to, "spring多附件邮件"
                , "Hello Spring Email!", fileMap);
    }

    @Test
    public void sendEmailWithInline() {
        springMail.sendEmailWithInline(from, authWord, to, "spring内嵌资源邮件"
                , "Hello Spring Email!", new File("D:\\LearningNotes\\picture\\RabbitMQ模型架构.png"));
    }

    @Test
    public void sendEmailByTemplate() {
        springMail.sendEmailByTemplate(from, authWord, to,
                "spring模板邮件", "Hello Spring Email!");
    }
}
