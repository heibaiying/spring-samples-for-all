package com.heibaiying.aop;

import com.heibaiying.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : heibaiying
 * @description : 关于多个切面在同一个切入点上执行顺序的例子 可以在spring-aop-annotation 中查看
 */

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:aop.xml")
public class AopTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void save() {
        orderService.createOrder(1283929319L, "手机");
        orderService.queryOrder(4891894129L);
    }
}
