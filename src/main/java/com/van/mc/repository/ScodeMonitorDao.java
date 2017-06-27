package com.van.mc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by van on 17-4-27.
 */
@Component
public class ScodeMonitorDao {

    static final String TABLE_NAME = "scode_history";

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Scode_history> selectAll(){
        return jdbcTemplate.query(
                "select * from scode_history"
                ,new BeanPropertyRowMapper<>(Scode_history.class));
    }
}
