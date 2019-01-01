package com.heibaiying.api;

import com.heibaiying.bean.Product;

import java.util.List;

/**
 * @author : heibaiying
 * @description : 产品服务接口类
 */
public interface IProductService {

    Product queryProductById(int id);

    List<Product> queryAllProducts();
}
