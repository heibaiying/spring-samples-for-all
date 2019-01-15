package com.heibaiying.producer.service;

import com.heibaiying.common.api.IProductService;
import com.heibaiying.common.bean.Product;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author : heibaiying
 * @description : 产品提供接口实现类
 */
@Service
public class ProductService implements IProductService, ApplicationListener<WebServerInitializedEvent> {

    private static List<Product> productList = new ArrayList<>();

    public Product queryProductById(int id) {
        for (Product product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }


    public List<Product> queryAllProducts() {
        // hystrix 默认超时是2秒
        int i = new Random().nextInt(2500);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public void saveProduct(Product product) {
        productList.add(product);
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        for (long i = 0; i < 20; i++) {
            productList.add(new Product(i, port + "产品" + i, i / 2 == 0, new Date(), 66.66f * i));
        }
    }
}
