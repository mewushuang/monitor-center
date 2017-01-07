package com.van.mc.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Monitor_metric implements Serializable{
    public static final String MEMORY_METRIC="memoryMetric";
    public static final String DISK_METRIC="diskMetric";
    public static final String CPU_METRIC="cpuMetric";

    @JsonIgnore
    private String id;
    private String obj_id;
    private String name;
    private String paramname;
    private String description;
    private String min;
    private String max;
    @JsonIgnore
    private boolean persist;
    private String curValue;//最近一次拉取的状态瞬时值
    private String value;//近一段时间的平均值
    @JsonIgnore
    private Deque<Double> total=new ConcurrentLinkedDeque<>();//总值
    private boolean warning;//当前状态是否需要引发告警

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public void add(double value){
        total.offer(value);
        if(total.size()>100){
            total.poll();
        }
    }
    public void reset(){
        total.clear();
    }
    public String getValue() {
        if(total==null||total.size()==0) return value;
        double sum=0;
        synchronized (total){
            for (Double d:total){
                sum+=d;
            }
        }
        return String.format("%.2f",sum/total.size());
    }

    @Transient
    public boolean isPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public String getId() {
        return id;
    }

    public static Monitor_metric memoryMetric(String obj_id){
        Monitor_metric m=new Monitor_metric();
        m.setDescription("内存使用率");
        m.setName(MEMORY_METRIC);
        m.setObj_id(obj_id);
        m.setPersist(true);
        return m;
    }

    public static Monitor_metric diskMetric(String obj_id){
        Monitor_metric m=new Monitor_metric();
        m.setDescription("磁盘使用率");
        m.setName(DISK_METRIC);
        m.setObj_id(obj_id);
        m.setPersist(true);
        return m;
    }

    public static Monitor_metric cpuMetric(String obj_id){
        Monitor_metric m=new Monitor_metric();
        m.setDescription("cpu使用率");
        m.setName(CPU_METRIC);
        m.setObj_id(obj_id);
        m.setPersist(true);
        return m;
    }




    public void setId(String id) {
        this.id = id;
    }

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public String getParamname() {
        return paramname;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }
}
