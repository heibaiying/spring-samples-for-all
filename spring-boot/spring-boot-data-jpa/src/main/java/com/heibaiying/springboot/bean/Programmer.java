package com.heibaiying.springboot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 *  需要注意是的 这里导入的与JPA相关注解全部是 javax.persistence 包下面的
 *  尤其是 @Id不要导成org.springframework.data.annotation.Id
 *  不然会抛出 No identifier specified for entity 异常
 */
import javax.persistence.*;
import java.util.Date;

/**
 * @author : heibaiying
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity                      // 表示该类是一个数据库表映射实体
@Table(name = "programmer") // 指明对应的数据库表的名称 不指定的默认就是类名
public class Programmer {

    /* AUTO:     默认值,主键由程序控制
     * IDENTITY: 数据库自动增长, Oracle不支持这种方式 ORACLE 靠序列来提供类似自增长的功能
     * SEQUENCE: 通过数据库的序列产生主键, MYSQL不支持 MYSQL没有序列的概念
     * Table:    提供特定的数据库产生主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 200)
    private String name;

    private int age;

    @Column(scale = 2)
    private float salary;

    private Date birthday;

    public Programmer(String name, int age, float salary, Date birthday) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.birthday = birthday;
    }

}

