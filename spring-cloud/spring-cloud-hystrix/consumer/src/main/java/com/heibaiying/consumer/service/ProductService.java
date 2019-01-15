package com.heibaiying.consumer.service;

import com.heibaiying.common.api.IProductService;
import com.heibaiying.common.bean.Product;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : heibaiying
 * @description : 产品提供接口实现类
 */
@Service
public class ProductService implements IProductService {

    @Autowired
    private RestTemplate restTemplate;


    @HystrixCommand(fallbackMethod = "queryProductsFail")
    public List<Product> queryAllProducts() {
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("http://producer/products", List.class);
        List<Product> productList = responseEntity.getBody();
        return productList;
    }

    public Product queryProductById(int id) {
        ResponseEntity<Product> responseEntity = restTemplate.getForEntity("http://producer/product/{1}", Product.class, id);
        return responseEntity.getBody();
    }


    public void saveProduct(Product product) {
        restTemplate.postForObject("http://producer/product", product, Void.class);
    }

    public List<Product> queryProductsFail() {
        return new ArrayList<>();
    }
}
