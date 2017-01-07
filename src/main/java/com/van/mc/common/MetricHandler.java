package com.van.mc.common;

import com.van.mc.repository.Monitor_metric;

/**
 * Created by van on 2016/12/29.
 */
public interface MetricHandler {
    void handle(Monitor_metric metric);
}
