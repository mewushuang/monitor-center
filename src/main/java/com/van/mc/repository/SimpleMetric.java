package com.van.mc.repository;

import com.van.monitor.api.Metric;

/**
 * Created by van on 2016/12/20.
 */
public class SimpleMetric extends Metric {
    @Override
    public Level getLevel() {
        return level;
    }

    public SimpleMetric() {
    }
}
