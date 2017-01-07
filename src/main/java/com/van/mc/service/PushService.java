package com.van.mc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.MetricHandler;
import com.van.mc.repository.Monitor_metric;
import com.van.mc.repository.Monitor_metricDao;
import com.van.mc.repository.Monitor_obj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by van on 2016/12/19.
 */
@Service
public class PushService {
    private final Logger logger = LoggerFactory.getLogger(PushService.class);

    //此处的设置必须与com.van.mc.config.WebSocketConfig类中设置的broker匹配，此处匹配
    //broker:/status-info。否则将无法发送
    public static final String STATUS_DESTINATION = "/status-info-resp";
    public static final String WARN_DESTINATION = "/warn-resp";


    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private Monitor_metricDao monitor_metricDao;

    private ObjectMapper mapper;

    @Async
    public void pushStatus(Object target) {
        template.convertAndSend(STATUS_DESTINATION, target);
    }

    @Async
    void pushWarn(Object target) {
        template.convertAndSend(WARN_DESTINATION, target);
    }

    /**
     * 分析监控对象的参数，向浏览器发出通知/告警
     *
     * @param monitor_obj
     */
    @Async
    public void parseAndPush(Monitor_obj monitor_obj, final boolean save) {

        //缓存中检索persistance为true的metric存库
        if (save) {
            final List<Monitor_metric> toSaveInDB = new ArrayList<>(10);
            monitor_obj.iterateMetrics(new MetricHandler() {
                @Override
                public void handle(Monitor_metric metric) {
                    if (metric.isPersist()) {
                        toSaveInDB.add(metric);
                    }
                }
            });
            monitor_metricDao.save(toSaveInDB);
        }

        pushStatus(monitor_obj);
    }

    /**
     * 分析监控对象的参数，向浏览器发出通知/告警
     *
     * @param monitor_objs
     */
    public void parseAndPush(Iterable<Monitor_obj> monitor_objs) {

    }
}
