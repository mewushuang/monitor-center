package com.van.mc.common;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存MBean
 * Created by van on 2016/11/15.
 */
public class CachedJMXClient {


    private String url;
    private JMXServiceURL jmxURL;
    private JMXConnector conn;
    private MBeanServerConnection mbsc;
    private Map<String, Object> cache;



    /**
     * 下面的方法超时时间约20秒(在connect处)，google上也没找到如何设置建立连接的超时时间，
     * 轮询状态的频度为5秒，这样会导致任务堆积，线程池队列占满产生TaskRejectedException
     * 此处在上级缓存处Monitor_obj采用计数器过滤掉重复的连接请求。
     *
     * @param url ip:port 类似 127.0.0.1:9999"
     */
    public CachedJMXClient(String url) throws IOException {
        jmxURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi");
            /*HashMap   environment = new HashMap();
            environment.put("sun.rmi.transport.tcp.handshakeTimeout",3000);
            environment.put("sun.rmi.transport.tcp.connectionTimeout",3000);*/


        conn = JMXConnectorFactory.connect(jmxURL, null);
        mbsc = conn.getMBeanServerConnection();
        cache = new HashMap<>();

    }

    public synchronized <T> T getMBean(Class<T> clazz, String objectName) {

        //从缓存拿到MBean
        Object ret = cache.get(objectName);
        if (ret != null) {
            return (T) ret;
        }

        ObjectName mbeanName = null;
        try {
            mbeanName = new ObjectName(objectName);
            // 构建代理并缓存
            T t = JMX.newMBeanProxy(mbsc, mbeanName, clazz, true);
            cache.put(objectName, t);
            return t;
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException("illegal bean name of " + clazz.getName() + " :" + objectName);
            //return null;
        }

    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    public void close(){
        try {
            conn.close();
        } catch (IOException e) {
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
