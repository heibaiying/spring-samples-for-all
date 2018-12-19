package com.heibaiying.controller;

import com.heibaiying.bean.Programmer;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : heibaiying
 * @description :数据校验
 */
@RestController
public class ParamValidController {


    @PostMapping("validate")
    public void valid(@Validated Programmer programmer,
                      BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            System.out.println(error.getDefaultMessage());
        }
    }

}
