package com.heibaiying.springboot.repository;

import com.heibaiying.springboot.bean.Programmer;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author : heibaiying
 * @description : jpa 方式查询 对于mongo而言 更推荐这种查询方式 比起原生的语法更加简洁
 */
public interface ProgrammerRepository extends MongoRepository<Programmer, String> {

    void deleteAllByName(String name);

    Programmer findAllByName(String names);

    Programmer findByNameAndAge(String name, int age);

}
