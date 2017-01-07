package com.van.mc.repository;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.CachedJMXClient;
import com.van.mc.common.IllegalParamException;
import com.van.mc.common.JMXConnectException;
import com.van.mc.common.MetricHandler;

import java.beans.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitor_obj implements Serializable {

    public static final int TYPE_AVAILABLE = 1;
    public static final int TYPE_DISABLE = 4;
    public static final ObjectMapper objectMapper = new ObjectMapper();


    private String id;
    private String name;
    private String type;
    private String params;
    private String url;
    private String ctrlname;
    private String hostname;


    private Map<String, ParamedServiceInstance> paramedServiceInstances;
    private Map<String, Monitor_metric> publicMetrics;
    @JsonIgnore
    public CachedJMXClient cachedJMXClient;

    @JsonIgnore
    private volatile AtomicInteger counter = new AtomicInteger(0);

    /**
     * JMX连接超时时间约20秒(在connect处)，google上也没找到如何设置建立连接的超时时间，
     * 轮询状态的频度为5秒，这样会导致任务堆积，线程池队列占满产生TaskRejectedException
     * 此处采用计数器过滤掉重复的连接请求。
     *
     */
    private CachedJMXClient repeatableConnect() {
        if(counter.compareAndSet(0,1)) {
            CachedJMXClient client = null;
            try {
                client = new CachedJMXClient(url);
                this.cachedJMXClient=client;
                counter.set(0);
                return client;
            } catch (MalformedURLException e) {
                counter.set(0);
                throw new JMXConnectException("illegal URL[" + url + "], ip:port expected");
            } catch (IOException e) {
                counter.set(0);
                //e.printStackTrace();
                throw new JMXConnectException("jmx connect url[" + url + "] failed, with exception:" + e.getMessage() + "]");
            }
        }else {
            return null;
        }
    }
    @Transient
    public CachedJMXClient getCachedJMXClient() {
        if (url == null) throw new IllegalParamException("illegal url");
        return cachedJMXClient == null ? repeatableConnect() : cachedJMXClient;
    }

    @Transient
    public String validateInput() {
        if (name == null || "".equals(name)) {
            return "illegal value on field name:" + name;
        }
        if (url == null || "".equals(url)) {
            return "illegal value on field url:" + url;
        }
        if (ctrlname == null || "".equals(ctrlname)) {
            return "illegal value on field ctrlname:" + ctrlname;
        }
        if (params == null && "".equals(params)) {
            return "illegal value on field params:" + params;
        }
        return "ok";
    }

    @Transient
    public void parseParams() throws IOException {
        Map<String, ParamedServiceInstance> ret= new ConcurrentHashMap<>();
        if ("".equals(params)) {
            setParamedServiceInstances(ret);
        } else {
            JavaType i = objectMapper.getTypeFactory().constructParametricType(List.class,  ParamedServiceInstance.class);
            List<ParamedServiceInstance> pis=objectMapper.readValue(params, i);
            for(ParamedServiceInstance pi:pis){
                ret.put(pi.getName(),pi);
            }
            setParamedServiceInstances(ret);
        }
    }

    public void addPublicMetric(Monitor_metric metric) {
        if (publicMetrics == null) {
            publicMetrics = new ConcurrentHashMap<>();
        }
        publicMetrics.put(metric.getName(), metric);
    }


    public Monitor_metric getPublicMetric(String metricName) {
        if (publicMetrics == null) {
            return null;
        }
        return publicMetrics.get(metricName);
    }

    public void addParamedMetric(String paramName, Monitor_metric metric) {
        if (paramedServiceInstances == null) {
            paramedServiceInstances = new ConcurrentHashMap<>();
        }
        ParamedServiceInstance pi = paramedServiceInstances.get(paramName);
        if (pi == null) {
            throw new IllegalParamException("param named " + paramName + " does exist in monitor_obj:id" + getId() + ",name:" + getName());
        }
        if (pi.getMetrics() == null) {
            pi.setMetrics(new ConcurrentHashMap<String, Monitor_metric>());
        }
        pi.getMetrics().put(metric.getName(), metric);
    }

    public Monitor_metric getMetric(String paramName, String metricName) {
        if (paramedServiceInstances != null) {
            ParamedServiceInstance pi = paramedServiceInstances.get(paramName);
            if (pi != null && pi.getMetrics() != null) {
                return pi.getMetrics().get(metricName);
            }
        }
        return null;
    }

    public Monitor_obj() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCtrlname() {
        return ctrlname;
    }

    public void setCtrlname(String ctrlname) {
        this.ctrlname = ctrlname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }


    public Map<String, ParamedServiceInstance> getParamedServiceInstances() {
        return paramedServiceInstances;
    }

    public void setParamedServiceInstances(Map<String, ParamedServiceInstance> paramedServiceInstances) {
        this.paramedServiceInstances = paramedServiceInstances;
    }
    public Map<String, Monitor_metric> getPublicMetrics() {
        return publicMetrics;
    }

    public void setPublicMetrics(Map<String, Monitor_metric> publicMetrics) {
        this.publicMetrics = publicMetrics;
    }

    /**
     * 遍历所有metric，使用handler回调处理每个metric
     * @param handler
     */
    public void iterateMetrics(MetricHandler handler){
        if(getPublicMetrics()!=null){
            for(Monitor_metric metric:getPublicMetrics().values()){
                handler.handle(metric);
            }
        }
        if(getParamedServiceInstances()!=null){
            for(ParamedServiceInstance pi:getParamedServiceInstances().values()){
                if(pi.getMetrics()!=null){
                    for (Monitor_metric metric:pi.getMetrics().values()){
                        handler.handle(metric);
                    }
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Monitor_obj that = (Monitor_obj) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        if(id==null){
            return super.hashCode();
        }
        return id.hashCode();
    }
}
