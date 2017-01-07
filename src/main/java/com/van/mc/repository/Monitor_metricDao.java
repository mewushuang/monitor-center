package com.van.mc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by van on 2016/12/20.
 */
@Component
public class Monitor_metricDao {
    static final String TABLE_NAME = "monitor_metric";
    static final String SEQ_NAME = "monitor_seq";

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Monitor_metric addMeta(final Monitor_metric metric) {

        //获取主键
        final String  sql="insert into "+ TABLE_NAME+" (id,obj_id,description,name,paramname,min,max) " +
                "values("+SEQ_NAME+".NEXTVAL,?,?,?,?,?,?)";
        GeneratedKeyHolder keyHolder=new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps=con.prepareStatement(sql,new String[]{"id"});
                ps.setString(1, metric.getObj_id());
                ps.setString(2,metric.getDescription());
                ps.setString(3,metric.getName());
                ps.setString(4,metric.getParamname());
                ps.setString(5,metric.getMin());
                ps.setString(6, metric.getMax());
                return ps;
            }
        },keyHolder);
        Number id = keyHolder.getKey();
        metric.setId(id.toString());
        return metric;
    }

    public List<Monitor_metric> loadAllMetrics() {
        return jdbcTemplate.query("select * from " + TABLE_NAME, new BeanPropertyRowMapper<>(Monitor_metric.class));
    }

    public List<Monitor_metric> findMetricByObjId(String objId){
        return jdbcTemplate.query("select * from " + TABLE_NAME+" where OBJ_ID=?",new Integer[]{Integer.parseInt(objId)},new int[]{Types.INTEGER}, new BeanPropertyRowMapper<>(Monitor_metric.class));
    }
    public void deleteByObjId(String objId){
        jdbcTemplate.update("DELETE from " + TABLE_NAME+" where OBJ_ID=?",new Integer[]{Integer.parseInt(objId)},new int[]{Types.INTEGER});
    }

    /**
     * 分段写入一段时间的平均值
     *
     * @param metrics
     */
    public void save(Collection<Monitor_metric> metrics) {
        List<Monitor_metric> ret = new LinkedList<>();
        for (Monitor_metric m : metrics) {
            if (!m.isPersist() || m.getId() == null || m.getObj_id() == null || m.getValue() == null) continue;
            ret.add(m);
        }
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(ret.toArray());
        namedParameterJdbcTemplate.batchUpdate(
                "INSERT INTO MONITOR_HISTORY(METRIC_ID,TIME,VAL) VALUES (" +
                        ":id, sysdate,:value )",
                batch);
    }
}
