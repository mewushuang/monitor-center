package com.van.mc.service;

import com.van.mc.repository.Monitor_obj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by van on 2016/12/31.
 */
@Component
public class ScheduleArrangement {
    private final Logger logger = LoggerFactory.getLogger(ScheduleArrangement.class);

    @Autowired
    private ScheduledTasks scheduledTasks;
    @Autowired
    private ObjCache objCache;

    private final int secondsToSave = 600;//10分钟求得平均值入库一次
    private final int collectRateInSeconds = 5;
    private AtomicInteger saveFlag = new AtomicInteger(0);


    @Scheduled(fixedRate = 1000 * collectRateInSeconds)
    public void scheduledCollect() {
        saveFlag.addAndGet(1);
        boolean save = saveFlag.compareAndSet(secondsToSave / collectRateInSeconds, 0);
        if (save) {
            logger.info("collecting " + secondsToSave / collectRateInSeconds + " times, will get average and save to db");
        }
        /**
         * 多个线程可以同时迭代ConcurrentHashMap
         * ConcurrentHashMap保证遍历的时候更新元素不会break,但是不能保证数据的一致性，
         * 而迭代器保证的是：它反映的是创建迭代器时容器的状态。
         * 从ConcurrentHashMap那里得到的iterator是为单线程设计的，即不可以传递它们，每一个线程都必须有自己的iterator。
         */
        for (Map.Entry<String, Monitor_obj> e : objCache.entrySet()) {

            Monitor_obj monitor_obj = e.getValue();
            scheduledTasks.statusSingleObj(monitor_obj, save);

        }


    }
}
