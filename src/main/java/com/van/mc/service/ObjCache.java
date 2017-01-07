package com.van.mc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.IllegalParamException;
import com.van.mc.repository.Monitor_metric;
import com.van.mc.repository.Monitor_metricDao;
import com.van.mc.repository.Monitor_obj;
import com.van.mc.repository.Monitor_objDao;
import com.van.monitor.api.ControllerMXBean;
import com.van.monitor.api.LogViewerMXBean;
import com.van.monitor.api.SysInfoMonitorMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by van on 2016/11/30.
 */
@Component
public class ObjCache extends ConcurrentHashMap<String, Monitor_obj> {
    private final Logger logger = LoggerFactory.getLogger(ObjCache.class);
    @Autowired
    Monitor_objDao monitor_objDao;

    @Autowired
    Monitor_metricDao monitor_metricDao;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void loadCache() {
        this.clear();
        for (Monitor_obj obj : monitor_objDao.findAll()) {
            String error = obj.validateInput();
            if (!"ok".equals(error)) {
                logger.warn("invalid monitor_obj from database: " + error);
                continue;
            }
            cache(obj);
        }
        for (Monitor_metric metric : monitor_metricDao.loadAllMetrics()) {
            if (containsKey(metric.getObj_id())) {
                metric.setPersist(true);
                if (metric.getParamname() == null) {
                    get(metric.getObj_id()).addPublicMetric(metric);
                }
                if (metric.getParamname() == null) {
                    get(metric.getObj_id()).addPublicMetric(metric);
                } else {
                    get(metric.getObj_id()).addParamedMetric(metric.getParamname(), metric);

                }
            }
        }
    }

    public void deleteFromCache(String objId){
        Monitor_obj ori=get(objId);
        if(ori!=null&&ori.cachedJMXClient!=null){
            ori.cachedJMXClient.close();
        }
        remove(objId);
    }

    /**
     * 重新加载某个对象
     * @param objId
     */
    public void loadCache(String objId){
        deleteFromCache(objId);
        Monitor_obj obj=monitor_objDao.findById(objId);
        cache(obj);

        //重置metric
        List<Monitor_metric> i=monitor_metricDao.findMetricByObjId(objId);
        if(i!=null) {
            for (Monitor_metric metric :i) {
                metric.setPersist(true);
                if (metric.getParamname() == null) {
                    obj.addPublicMetric(metric);
                } else {
                    obj.addParamedMetric(metric.getParamname(), metric);
                }
            }
        }
    }

    /**
     * 预先已做了验证
     *
     * @param obj
     */
    public void cache(Monitor_obj obj) {
        if (obj.getId() == null)
            throw new IllegalParamException("cannot cache monitor_obj with name[" + obj.getName() + "],id is null");
        //解析参数
        String flag=obj.validateInput();
        if(!"ok".equals(flag)){
            logger.warn("invalid obj with id["+obj.getId()+"]");
        }
        try {
            obj.parseParams();
        } catch (IOException e) {
            logger.warn("invalid params of obj with id["+obj.getId()+"]");
        }
        this.put(obj.getId(), obj);
    }

    public <T> T getBean(String objId, Class<T> clazz) {
        if (objId == null) throw new IllegalParamException("illegal arg objId:null");
        Monitor_obj obj = this.get(objId);
        if (obj == null) throw new IllegalParamException("wrong obj id,or obj add not by browser");

        String beanName;
        if (clazz == ControllerMXBean.class) {
            beanName = obj.getCtrlname() + ControllerMXBean.BEAN_TYPE;
        } else if (clazz == SysInfoMonitorMXBean.class) {
            beanName = SysInfoMonitorMXBean.BEAN_NAME;
        } else if (clazz == LogViewerMXBean.class) {
            beanName = LogViewerMXBean.BEAN_NAME;
        } else {
            throw new IllegalParamException("unsupported mbean interface:" + clazz.getName());
        }

        T bean = obj.getCachedJMXClient()
                .getMBean(clazz, beanName);
        return bean;
    }
}
