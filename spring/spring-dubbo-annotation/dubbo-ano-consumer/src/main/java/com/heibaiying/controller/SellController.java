package com.heibaiying.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.heibaiying.api.IProductService;
import com.heibaiying.bean.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author : heibaiying
 * @description :
 */
@Controller
@RequestMapping("sell")
public class SellController {

    // dubbo远程引用注解
    @Reference
    private IProductService productService;

    @RequestMapping
    public String productList(Model model) {
        List<Product> products = productService.queryAllProducts();
        model.addAttribute("products", products);
        return "products";
    }

    @RequestMapping("product/{id}")
    public String productDetail(@PathVariable int id, Model model) {
        Product product = productService.queryProductById(id);
        model.addAttribute("product", product);
        return "product";
    }
}
