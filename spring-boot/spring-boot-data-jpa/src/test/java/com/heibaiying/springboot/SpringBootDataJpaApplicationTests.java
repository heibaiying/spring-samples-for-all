package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.dao.ProgRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDataJpaApplicationTests {

    @Autowired
    private ProgRepository repository;

    @Test
    public void save() {
        repository.save(new Programmer("xiaoxiao", 12, 2121.34f, new Date()));
    }


    @Test
    public void get() {
        // 遵循命名规范的查询
        List<Programmer> programmers = repository.findAllByName("xiaominng");
        programmers.forEach(System.out::println);
        // 传入参数名称
        Programmer param = repository.findByParam("xiaolan", 21);
        System.out.println("findByParam:" + param);
        // 占位符查询
        List<Programmer> byCondition = repository.findByCondition("xiaolan", 347.34f);
        System.out.println("byCondition:" + byCondition);
        //条件与分页查询
        List<Programmer> programmerList = repository.findByAndSort("xiaominng", Sort.by("salary"));
        programmerList.forEach(System.out::println);
    }

    @Test
    public void delete() {
        Optional<Programmer> programmer = repository.findById(4);
        if (programmer.isPresent()) {
            repository.deleteById(4);
        }
    }
}

