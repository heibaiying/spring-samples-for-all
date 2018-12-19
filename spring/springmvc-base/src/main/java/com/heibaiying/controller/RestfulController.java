package com.heibaiying.controller;

import com.heibaiying.bean.Pet;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : heibaiying
 * @description : Restful 风格的请求
 */

@RestController
public class RestfulController {


    @GetMapping("restful/owners/{ownerId}/pets/{petId}")
    public void get(@PathVariable String ownerId, @PathVariable String petId) {
        System.out.println("ownerId:" + ownerId);
        System.out.println("petId:" + petId);
    }


    @GetMapping("restful2/owners/{ownerId}/pets/{petId}")
    public void get(@ModelAttribute Pet pet) {
        System.out.println("ownerId:" + pet.getOwnerId());
        System.out.println("petId:" + pet.getPetId());
    }

}
