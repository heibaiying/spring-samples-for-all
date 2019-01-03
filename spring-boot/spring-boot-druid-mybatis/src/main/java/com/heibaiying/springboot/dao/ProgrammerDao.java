package com.heibaiying.springboot.dao;

import com.heibaiying.springboot.bean.Programmer;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author : heibaiying
 */
@Mapper
public interface ProgrammerDao {


    @Select("select * from programmer")
    List<Programmer> selectAll();

    @Insert("insert into programmer (name, age, salary, birthday) VALUES (#{name}, #{age}, #{salary}, #{birthday})")
    void save(Programmer programmer);

    @Select("select * from programmer where name = #{id}")
    Programmer selectById(int id);

    @Update("update programmer set name=#{name},age=#{age},salary=#{salary},birthday=#{birthday} where id=#{id}")
    int modify(Programmer programmer);

    @Delete(" delete from programmer where id = #{id}")
    void delete(int id);
}
