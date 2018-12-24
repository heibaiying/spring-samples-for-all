package com.heibaiying.dao;

import com.heibaiying.bean.Relation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : heibaiying
 * @description :
 */


public interface MysqlDao {

    List<Relation> queryById(String id);
}
