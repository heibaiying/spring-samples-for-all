package com.heibaiying.consumer.controller;


import com.heibaiying.common.bean.Product;
import com.heibaiying.consumer.feign.CProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author : heibaiying
 */
@Controller
@RequestMapping("sell")
public class SellController {

    @Autowired
    private CProductFeign cproductFeign;

    @RequestMapping
    public String productList(Model model) {
        List<Product> products = cproductFeign.productList();
        model.addAttribute("products", products);
        return "products";
    }

    @RequestMapping("product/{id}")
    public String productDetail(@PathVariable int id, Model model) {
        Product product = cproductFeign.productDetail(id);
        model.addAttribute("product", product);
        return "product";
    }
}
