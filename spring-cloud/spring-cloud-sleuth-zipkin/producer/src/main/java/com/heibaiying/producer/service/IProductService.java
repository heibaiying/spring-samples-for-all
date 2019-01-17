package com.heibaiying.producer.service;

import com.heibaiying.common.bean.Product;

import java.util.List;
public interface IProductService {

    Product queryProductById(int id) ;

    List<Product> queryAllProducts();

    void saveProduct(Product product);
}
