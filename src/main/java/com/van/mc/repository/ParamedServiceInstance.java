package com.van.mc.repository;



import java.util.Map;

/**
 * 同一个服务可能因配置了不同的参数而启动不同的实例，此类代表了一个实例对象具有的一些元素
 * 根据名称做唯一区分
 * Created by van on 2016/12/26.
 */
public class ParamedServiceInstance {
    private String name;
    private String val;//调用start,stop,status方法时传递的参数
    private Map<String,Monitor_metric> metrics;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Map<String, Monitor_metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Monitor_metric> metrics) {
        this.metrics = metrics;
    }
}
