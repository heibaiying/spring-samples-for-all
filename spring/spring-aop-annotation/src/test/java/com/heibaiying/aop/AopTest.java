package com.heibaiying.aop;

import com.heibaiying.config.AopConfig;
import com.heibaiying.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : heibaiying
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AopConfig.class)
public class AopTest {


    @Autowired
    private OrderService orderService;

    @Test
    public void saveAndQuery() {
        orderService.createOrder(1283929319L, "手机");
        orderService.queryOrder(4891894129L);
    }

    /**
     * 多个切面作用于同一个切入点时，可以用@Order指定切面的执行顺序
     * 优先级高的切面在切入方法前执行的通知(before)会优先执行，但是位于方法后执行的通知(after,afterReturning)反而会延后执行
     */
    @Test
    public void delete() {
        orderService.deleteOrder(12793179L);
    }
}
