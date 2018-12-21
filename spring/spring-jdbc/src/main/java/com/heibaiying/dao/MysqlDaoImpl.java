package com.heibaiying.dao;

import com.heibaiying.bean.Relation;
import com.heibaiying.dao.impl.MysqlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author : heibaiying
 * @description :
 */

@Repository
public class MysqlDaoImpl implements MysqlDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 更多JDBC 的使用可以参考官方文档
     * @see <a href="https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/data-access.html#jdbc-JdbcTemplate">JdbcTemplate</a>
     */
    public List<Relation> get() {
        List<Relation> relations = jdbcTemplate.query("select * from help_keyword where help_keyword_id = ? ", new Object[]{691},
                new RowMapper<Relation>() {
                    public Relation mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Relation relation = new Relation();
                        relation.setId(rs.getString("help_keyword_id"));
                        relation.setName(rs.getString("name"));
                        return relation;
                    }

                });
        return relations;
    }
}
