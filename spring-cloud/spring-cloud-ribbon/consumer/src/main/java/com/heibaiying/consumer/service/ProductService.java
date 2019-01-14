package com.heibaiying.consumer.service;

import com.heibaiying.common.api.IProductService;
import com.heibaiying.common.bean.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author : heibaiying
 * @description : 产品提供接口实现类
 */
@Service
public class ProductService implements IProductService {

    @Autowired
    private RestTemplate restTemplate;

    public Product queryProductById(int id) {
        ResponseEntity<Product> responseEntity = restTemplate.getForEntity("http://producer/product/{1}", Product.class, id);
        return responseEntity.getBody();
    }


    public List<Product> queryAllProducts() {
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("http://producer/products", List.class);
        List<Product> productList = responseEntity.getBody();
        return productList;
    }

}
