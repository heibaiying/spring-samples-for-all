package com.heibaiying.springboot.dao;

import com.heibaiying.springboot.bean.Programmer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : heibaiying
 */
@Mapper
public interface ProgrammerMapper {

    List<Programmer> selectAll(String dataSource);

    void save(Programmer programmer);

    Programmer selectById(int id);

    int modify(String dataSource,@Param("pro") Programmer programmer);

    void delete(int id);
}
