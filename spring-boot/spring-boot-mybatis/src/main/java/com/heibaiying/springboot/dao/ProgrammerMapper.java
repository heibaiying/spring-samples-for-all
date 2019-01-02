package com.heibaiying.springboot.dao;

import com.heibaiying.springboot.bean.Programmer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : heibaiying
 */

@Mapper
public interface ProgrammerMapper {

    void save(Programmer programmer);

    Programmer selectById(int id);

    int modify(Programmer programmer);

    void delete(int id);
}
