package com.van.mc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by van on 2016/12/19.
 */
@ConfigurationProperties(prefix = "push")
@Component
public class StatusConfiguration implements EnvironmentAware{

    /**
     * destination:
     notice: /status-info-resp
     alert:  /status-alert-resp
     threshold:
     memory: 90
     disk: 80
     cpu: 95
     */

    private String destinationNotice;
    private String destinationAlert;
    private String thresholdMemory;
    private String thresholdDisk;
    private String thresholdCpu;

    @Override
    public void setEnvironment(Environment environment) {
        //System.out.println(environment);
    }

    public String getDestinationNotice() {
        return destinationNotice;
    }

    public void setDestinationNotice(String destinationNotice) {
        this.destinationNotice = destinationNotice;
    }

    public String getDestinationAlert() {
        return destinationAlert;
    }

    public void setDestinationAlert(String destinationAlert) {
        this.destinationAlert = destinationAlert;
    }

    public String getThresholdMemory() {
        return thresholdMemory;
    }

    public void setThresholdMemory(String thresholdMemory) {
        this.thresholdMemory = thresholdMemory;
    }

    public String getThresholdDisk() {
        return thresholdDisk;
    }

    public void setThresholdDisk(String thresholdDisk) {
        this.thresholdDisk = thresholdDisk;
    }

    public String getThresholdCpu() {
        return thresholdCpu;
    }

    public void setThresholdCpu(String thresholdCpu) {
        this.thresholdCpu = thresholdCpu;
    }
}
