package com.van.mc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.AdviceAnnotation;
import com.van.mc.common.ResponseJsonSerialization;
import com.van.mc.repository.Monitor_metricDao;
import com.van.mc.repository.Monitor_obj;
import com.van.mc.repository.Monitor_objDao;
import com.van.monitor.api.ControllerMXBean;
import com.van.monitor.api.SysInfoMonitorMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by van on 2016/11/28.
 */
@Service
@AdviceAnnotation
public class CollectService extends ResponseJsonSerialization {
    private Logger logger = LoggerFactory.getLogger(CollectService.class);

    private Monitor_objDao monitor_objDao;
    private ObjectMapper mapper = new ObjectMapper();



    @Autowired
    private ObjCache objCache;
    @Autowired
    private Monitor_metricDao monitor_metricDao;
    //private Map<String,Monitor_obj> objCache;

    @Autowired
    public void setMonitor_objDao(Monitor_objDao monitor_objDao) {
        this.monitor_objDao = monitor_objDao;
    }

    @PostConstruct
    public void init() {

        //register(this);
    }

    public String addMonitorObj(Monitor_obj obj) {
        Monitor_obj ret=monitor_objDao.add(obj);
        objCache.put(obj.getId(), ret);
        return getEmptySuccResponse();
    }

    /**
     *
     * @return
     */
    public String getAllObjs() {
        return getSuccResponse(objCache.values());
    }


    public String startRemotely(String objId) {

        ControllerMXBean mBean = objCache.getBean(objId, ControllerMXBean.class);
        return  mBean.startDefault();
    }
    public String startRemotely(String objId,String args) {
        if(logger.isInfoEnabled()){
            logger.info("start object id["+objId+"] with args:"+args);
        }
        ControllerMXBean mBean = objCache.getBean(objId, ControllerMXBean.class);
        return  mBean.start(parseArg(args));
    }

    public String stopRemotely(String objId) {
        ControllerMXBean mBean = objCache.getBean(objId, ControllerMXBean.class);
        return  mBean.stopDefault();
    }

    public String stopRemotely(String objId,String args) {
        if(logger.isInfoEnabled()){
            logger.info("stop object id["+objId+"] with args:"+args);
        }
        ControllerMXBean mBean = objCache.getBean(objId, ControllerMXBean.class);
        return  mBean.stop(parseArg(args));
    }

    @Transactional
    public String deleteMonitorObj(String objId){
        monitor_metricDao.deleteByObjId(objId);
        monitor_objDao.del(objId);
        objCache.deleteFromCache(objId);
        return getSuccResponse(null);
    }

    private AtomicInteger restartFlag=new AtomicInteger(0);
    public String restartDaemon(String objId){

        if(restartFlag.compareAndSet(0,1)) {
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("restarting object id[" + objId + "]");
                }
                ControllerMXBean mBean = objCache.getBean(objId, ControllerMXBean.class);
                String ret= mBean.restartDaemon();
                objCache.loadCache(objId);
                return ret;
            } finally {
                restartFlag.set(0);
            }
        }else {
            return getResponse(400,"duplicate restart operation, ignored",null);
        }
    }

    public String getRuntimeInfo(String objId){
        SysInfoMonitorMXBean info = objCache.getBean(objId,SysInfoMonitorMXBean.class);
        return info.getChangefulInfo();
    }



    public String getRemoteStatus(String objId,String args){
        ControllerMXBean mBean = objCache.getBean(objId,ControllerMXBean.class);
        return mBean.status(parseArg(args));
    }
    private String[] parseArg(String arg){
        return arg==null?new String[]{}:arg.split(" ");
    }


}
