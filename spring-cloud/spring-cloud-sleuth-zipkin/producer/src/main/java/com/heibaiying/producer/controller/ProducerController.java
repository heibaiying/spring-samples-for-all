package com.heibaiying.producer.controller;


import com.heibaiying.common.bean.Product;
import com.heibaiying.common.feign.ProductFeign;
import com.heibaiying.producer.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : heibaiying
 */
@RestController
public class ProducerController implements ProductFeign {

    @Autowired
    private IProductService productService;

    @GetMapping("products")
    public List<Product> productList() {
        return productService.queryAllProducts();
    }

    @GetMapping("product/{id}")
    public Product productDetail(@PathVariable int id) {
        return productService.queryProductById(id);
    }

    @PostMapping("product")
    public void save(@RequestBody Product product) {
        productService.saveProduct(product);
    }
}
