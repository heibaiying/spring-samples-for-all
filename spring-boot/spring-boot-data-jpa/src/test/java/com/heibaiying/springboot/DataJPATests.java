package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.dao.ProgRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataJPATests {

    @Autowired
    private ProgRepository repository;


    /**
     * 保存数据测试
     */
    @Test
    public void save() {
        // 保存单条数据
        repository.save(new Programmer("pro01", 12, 2121.34f, new Date()));
        // 保存多条数据
        List<Programmer> programmers = new ArrayList<>();
        programmers.add(new Programmer("pro02", 22, 3221.34f, new Date()));
        programmers.add(new Programmer("pro03", 32, 3321.34f, new Date()));
        programmers.add(new Programmer("pro04", 44, 4561.34f, new Date()));
        programmers.add(new Programmer("pro01", 44, 4561.34f, new Date()));
        repository.saveAll(programmers);
    }


    /**
     * 查询数据测试
     */
    @Test
    public void get() {

        // 遵循命名规范的查询
        List<Programmer> programmers = repository.findAllByName("pro01");
        programmers.forEach(System.out::println);

        // 传入参数名称
        Programmer param = repository.findByParam("pro02", 22);
        System.out.println("findByParam:" + param);

        // 占位符查询
        List<Programmer> byCondition = repository.findByConditionAndOrder("pro03", 3321.34f, Sort.Order.asc("salary"));
        System.out.println("byCondition:" + byCondition);

        //条件与分页查询 需要注意的是这里的页数是从第0页开始计算的
        Page<Programmer> page = repository.findAll(PageRequest.of(0, 10, Sort.Direction.DESC, "salary"));
        page.get().forEach(System.out::println);
    }


    /**
     * 更新数据测试
     */
    @Test
    public void update() {
        // 保存主键相同的数据就认为是更新操作
        repository.save(new Programmer(1, "updatePro01", 12, 2121.34f, new Date()));
        Optional<Programmer> programmer = repository.findById(1);
        Assert.assertEquals(programmer.get().getName(), "updatePro01");
    }


    /**
     * 删除数据测试
     */
    @Test
    public void delete() {
        Optional<Programmer> programmer = repository.findById(2);
        if (programmer.isPresent()) {
            repository.deleteById(2);
        }
        Assert.assertFalse(programmer.isPresent());
    }


}

