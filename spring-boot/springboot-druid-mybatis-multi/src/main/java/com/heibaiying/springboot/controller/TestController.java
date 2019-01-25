package com.heibaiying.springboot.controller;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.constant.Data;
import com.heibaiying.springboot.dao.ProgrammerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : heibaiying
 * @description : 测试controller
 */

@RestController
public class TestController {


    @Autowired
    private ProgrammerMapper programmerDao;

    /**
     * 查询全部数据源的数据
     */
    @GetMapping("/db/programmers")
    public List<Programmer> getAllProgrammers() {
        List<Programmer> programmers = programmerDao.selectAll(Data.DATASOURCE1);
        programmers.addAll(programmerDao.selectAll(Data.DATASOURCE2));
        return programmers;
    }

    @GetMapping("ts/db/programmers")
    @Transactional
    public List<Programmer> getAllProgrammersTs() {
        List<Programmer> programmers = programmerDao.selectAll(Data.DATASOURCE1);
        programmers.addAll(programmerDao.selectAll(Data.DATASOURCE2));
        return programmers;
    }

    /**
     * 不指定就使用默认的数据源
     */
    @GetMapping("/db1/programmers")
    public List<Programmer> getDB1Programmers() {

        return programmerDao.selectAll(null);
    }

    /**
     * 从指定数据源查询
     */
    @GetMapping("/db2/programmers")
    public List<Programmer> getDB2Programmers() {
        return programmerDao.selectAll(Data.DATASOURCE2);
    }


}
