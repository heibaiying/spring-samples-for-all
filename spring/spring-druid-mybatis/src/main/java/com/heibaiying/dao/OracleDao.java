package com.heibaiying.dao;

import com.heibaiying.bean.Flow;

import java.util.List;

/**
 * @author : heibaiying
 * @description :
 */
public interface OracleDao {

    List<Flow> queryById(long id);
}
