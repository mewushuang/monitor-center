package com.van.mc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created by van on 2016/11/21.
 */
@Component
public class Monitor_objDao {

    @Autowired
    Monitor_metricDao monitor_metricDao;

    static final String TABLE_NAME = "monitor_obj";
    static final String SEQ_NAME = "monitor_seq";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns("id");
    }

    /**
     * 添加监控对象时初始化三个资源监控metric
     *
     * @param obj
     * @return 返回添加了id和资源监控metric的Monitor_obj
     */
    @Transactional
    public Monitor_obj add(final Monitor_obj obj) {
        //获取主键
        final String sql = "INSERT INTO " + TABLE_NAME + " (id,url,ctrlname,name,params) " +
                "VALUES(" + SEQ_NAME + ".NEXTVAL,?,?,?,?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, obj.getUrl());
                ps.setString(2, obj.getCtrlname());
                ps.setString(3, obj.getName());
                ps.setString(4, obj.getParams());
                return ps;
            }
        }, keyHolder);
        Number id = keyHolder.getKey();
        obj.setId(id.toString());

        //生成默认metric
        obj.addPublicMetric(monitor_metricDao.addMeta(Monitor_metric.cpuMetric(obj.getId())));
        obj.addPublicMetric(monitor_metricDao.addMeta(Monitor_metric.diskMetric(obj.getId())));
        obj.addPublicMetric(monitor_metricDao.addMeta(Monitor_metric.memoryMetric(obj.getId())));

        return obj;
    }


    public void del(String objId) {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " where ID=?" ,objId);
    }

    public Monitor_obj findById(String objId) {
        List<Monitor_obj> i= jdbcTemplate.query("SELECT * FROM " + TABLE_NAME + " WHERE id=? " +
                " AND type=" + Monitor_obj.TYPE_AVAILABLE, new Integer[]{Integer.parseInt(objId)}, new int[]{Types.INTEGER}, new BeanPropertyRowMapper<>(Monitor_obj.class));
        if(i!=null&&i.size()>0){
            return i.get(0);
        }else return null;
    }

    public void update(Monitor_obj obj) {
        jdbcTemplate.update("UPDATE " + TABLE_NAME + " SET " +
                        "url=?,name=?," +
                        "ctrlname=?,params=? WHERE id=?"
                , obj.getUrl(), obj.getName(), obj.getCtrlname()
                , obj.getParams(), obj.getId());
    }

    public List<Monitor_obj> findAll() {
        return jdbcTemplate.query("SELECT * FROM " + TABLE_NAME + " WHERE " +
                "type=" + Monitor_obj.TYPE_AVAILABLE, new BeanPropertyRowMapper(Monitor_obj.class));
    }


    /**
     * 检查是否存在表特定表，否则执行建表语句
     */
    @PostConstruct//初始化方法的注解方式
    public void init() {
        /*List<String> tab=jdbcTemplate
                .queryForList("SELECT TABLE_NAME from USER_TABLES where TABLE_NAME like 'monitor_%'",String.class);
        if(tab.contains("monitor_meta")){
            jdbcTemplate.ex
        }*/
    }

    private void creatTable(String tablename, String[] cols) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cols.length; i++) {
            sb.append(cols[i]);
            if (i > 0) sb.append(',');
        }

        jdbcTemplate.execute("CREATE TABLE " + tablename + " (" + sb.toString() + " )");
    }
}
