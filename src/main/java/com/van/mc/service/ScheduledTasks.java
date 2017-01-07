package com.van.mc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.IllegalParamException;
import com.van.mc.common.MetricHandler;
import com.van.mc.repository.*;
import com.van.monitor.api.Metric;
import com.van.monitor.api.RunningStatusMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Created by van on 2016/12/6.
 */
@Service
public class ScheduledTasks {

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private Monitor_metricDao monitor_metricDao;
    @Autowired
    private CollectService collectService;
    @Autowired
    private PushService pushService;
    private ObjectMapper mapper = new ObjectMapper();

    private final RunningStatusMetric deamonStop=new RunningStatusMetric(RunningStatusMetric.RunningStatus.daemonStopped);

    @Async
    public void statusSingleObj(Monitor_obj monitor_obj, boolean save) {
        try {
            //有一处连接获取到了正确的返回，则将此连接状态更新为true
            boolean connStatus=false;

            //资源使用率信息
            String runtimeInfo = collectService.getRuntimeInfo(monitor_obj.getId());
            if (logger.isDebugEnabled()) {
                logger.debug("objId:" + monitor_obj.getId() + " sysInfo:\n" + runtimeInfo);
            }
            if(dealResourceResponse(monitor_obj, runtimeInfo)){
                connStatus=true;
            }

            //各服务实例的运行状态及其它自定义状态信息
            if (monitor_obj.getParamedServiceInstances() != null) {
                for (ParamedServiceInstance pi : monitor_obj.getParamedServiceInstances().values()) {
                    String status = collectService.getRemoteStatus(monitor_obj.getId(), pi.getVal());
                    if (logger.isDebugEnabled()) {
                        logger.debug("objId:" + monitor_obj.getId() + " status:\n" + status);
                    }
                    if(dealStatusResponse(monitor_obj, status, pi.getName())){
                        connStatus=true;
                    }
                }
            }

            if(!connStatus){//连接失败，重置缓存中的状态为失败
                //如果之前连接成功，此处将缓存的
                if(monitor_obj.cachedJMXClient!=null){
                    monitor_obj.cachedJMXClient.close();
                    monitor_obj.cachedJMXClient=null;
                }
                monitor_obj.iterateMetrics(new MetricHandler() {
                    @Override
                    public void handle(Monitor_metric metric) {
                        if(deamonStop.getName().equals(metric.getName())){
                            metric.setValue(RunningStatusMetric.RunningStatus.daemonStopped.name());
                        }else {
                            metric.setValue(null);
                            metric.reset();
                        }
                    }
                });
            }

            //异步向页面异步推送状态信息/告警，并进行存库
            pushService.parseAndPush(monitor_obj, save);

        } catch (IllegalParamException e3) {
            //logger.error("invalid jmx params:" + e3.getMessage());
            pushService.pushWarn("invalid jmx params:name: " + monitor_obj.getName() + " url: " + monitor_obj.getUrl() + " controllerName: " + monitor_obj.getCtrlname() + " error msg:" + e3.getMessage());
        } catch (Exception e1) {
            pushService.pushWarn("error on monitorObj:" + monitor_obj.getId() + "," + monitor_obj.getName() + ", caused by " + e1.getMessage());
            //obj状态更新
            //logger.error("error on monitorObj:" + monitor_obj.getId() + "," + monitor_obj.getName() + ", caused by " + e1.getMessage() + "\n", e1);
        }
    }

    //{"totalMemGB":7,"cpuTotalNum":4,"cpuUsagePercent":6,"memUsagePercent":59,"diskUsagePercent":34,"freeMemGB":3,"totalDiskGB":579.0,"hostname":"DESKTOP-H3EDSE1"}
    private boolean dealResourceResponse(Monitor_obj monitor_obj, String response) throws IOException {
        //{code:200,msg:"ok",data:{}}
        if(!checkResourceMetric(monitor_obj)){
            return false;
        }
        Map res = mapper.readValue(response, Map.class);
        if ("200".equals(res.get("code").toString())) {
            Map<String, Object> info = (Map<String, Object>) res.get("data");
            monitor_obj.setHostname(info.get("hostname").toString());
            //非空检查
            Monitor_metric memoryMetric = monitor_obj.getPublicMetric(Monitor_metric.MEMORY_METRIC);
            Monitor_metric diskMetric = monitor_obj.getPublicMetric(Monitor_metric.DISK_METRIC);
            Monitor_metric cpuMetric = monitor_obj.getPublicMetric(Monitor_metric.CPU_METRIC);

            memoryMetric.add(Double.parseDouble(info.get("memUsagePercent").toString()));
            memoryMetric.setValue(info.get("memUsagePercent").toString());
            diskMetric.add(Double.parseDouble(info.get("diskUsagePercent").toString()));
            diskMetric.setValue(info.get("diskUsagePercent").toString());
            cpuMetric.add(Double.parseDouble(info.get("cpuUsagePercent").toString()));
            cpuMetric.setValue(info.get("cpuUsagePercent").toString());

            return true;
        } else {
            pushService.pushWarn("error on monitor obj, name:" + monitor_obj.getName() + ", id:" + monitor_obj.getId() + "msg:" + res.get("msg"));
            return false;
        }
    }

    private boolean dealStatusResponse(Monitor_obj monitor_obj, String response, String paramName) throws IOException {
        //{code:200,msg:"ok",data:[{},..]}
        JsonNode res = mapper.readTree(response);

        if (res == null || res.get("data") == null||!"200".equals(res.get("code").asText())) return false;
        for (JsonNode node : res.get("data")) {
            //metric对象结构{name:,value:,description:,level:,needSave}
            if (node == null)
                continue;

            Metric m = mapper.convertValue(node, SimpleMetric.class);
            //从缓存中查找是否包含，是则更新状态，否则先向数据库添加此metric信息
            Monitor_metric mm;
            if (monitor_obj.getMetric(paramName, m.getName()) == null) {
                mm = new Monitor_metric();
                mm.setObj_id(monitor_obj.getId());
                mm.setName(m.getName());
                mm.setParamname(paramName);
                mm.setDescription(m.getDescription());
                mm.setPersist(m.isNeedSave());
                mm.setWarning(Metric.Level.error == m.getLevel());
                //放入数据库
                if (mm.isPersist()) mm = monitor_metricDao.addMeta(mm);
                //缓存
                monitor_obj.addParamedMetric(paramName, mm);
            }
            //写入值
            mm = monitor_obj.getMetric(paramName, m.getName());
            if (mm.isPersist()) {
                try {
                    mm.add(Double.parseDouble(m.getValue()));
                } catch (NumberFormatException e) {
                    String msg = "metric只有为数值时才可以存历史，上下文信息：" +
                            "监测对象 id:" + monitor_obj.getId() + " name:" + monitor_obj.getName() +
                            "测点 id:" + mm.getId() + " name:" + mm.getName();
                    //logger.warn(msg);
                    pushService.pushWarn(msg);
                }
            } else
                mm.setValue(m.getValue());

        }
        return true;
    }


    /**
     * objCache初始化时已加载所有metric，如obj是新添加的对象，则可能尚未添加三种公共metric，
     * 此处做判断并添加到Monitor_obj对象
     *
     * @param obj
     */

    private boolean checkResourceMetric(Monitor_obj obj) {
        if (obj.getPublicMetric(Monitor_metric.CPU_METRIC) == null
                || obj.getPublicMetric(Monitor_metric.DISK_METRIC) == null
                || obj.getPublicMetric(Monitor_metric.MEMORY_METRIC) == null) {
            String msg = "metric data of obj with id[" + obj.getId() + "] may be lost or not loaded";
            //logger.warn(msg);
            pushService.pushWarn(msg);
            return false;
        }
        return true;
    }

}
