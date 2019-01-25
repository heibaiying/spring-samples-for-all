package com.heibaiying.springboot.controller;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.constant.Data;
import com.heibaiying.springboot.dao.ProgrammerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author : heibaiying
 * @description : 测试分布式事务
 */
@RestController
public class XATransactionController {

    @Autowired
    private ProgrammerMapper programmerDao;


    @RequestMapping("/db/change")
    @Transactional
    public void changeDb() {
        Programmer programmer01 = new Programmer(1, "xiaolandb1", 100, 6662.32f, new Date());
        Programmer programmer02 = new Programmer(1, "xiaohongdb2", 100, 6662.32f, new Date());
        programmerDao.modify(Data.DATASOURCE1, programmer01);
        programmerDao.modify(Data.DATASOURCE2, programmer02);
    }

    @RequestMapping("ts/db/change")
    @Transactional
    public void changeTsDb() {
        Programmer programmer01 = new Programmer(1, "xiaolandb1", 99, 6662.32f, new Date());
        Programmer programmer02 = new Programmer(1, "xiaohongdb2", 99, 6662.32f, new Date());
        programmerDao.modify(Data.DATASOURCE1, programmer01);
        programmerDao.modify(Data.DATASOURCE2, programmer02);
        int i = 1 / 0;
    }


}
