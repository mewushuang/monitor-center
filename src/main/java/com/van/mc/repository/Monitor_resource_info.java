package com.van.mc.repository;

import java.io.Serializable;

public class Monitor_resource_info   implements Serializable {
  private String obj_id;
  private java.sql.Date item_date;
  private String hour;
  private String minute;
  private String cpu_used;
  private String mem_used;
  private String disk_used;
  private String metric_a;
  private String metric_b;
  private String extra;
  private String stat;

  public String getObj_id() {
    return obj_id;
  }

  public void setObj_id(String obj_id) {
    this.obj_id = obj_id;
  }

  public java.sql.Date getItem_date() {
    return item_date;
  }

  public void setItem_date(java.sql.Date item_date) {
    this.item_date = item_date;
  }

  public String getHour() {
    return hour;
  }

  public void setHour(String hour) {
    this.hour = hour;
  }

  public String getMinute() {
    return minute;
  }

  public void setMinute(String minute) {
    this.minute = minute;
  }

  public String getCpu_used() {
    return cpu_used;
  }

  public void setCpu_used(String cpu_used) {
    this.cpu_used = cpu_used;
  }

  public String getMem_used() {
    return mem_used;
  }

  public void setMem_used(String mem_used) {
    this.mem_used = mem_used;
  }

  public String getDisk_used() {
    return disk_used;
  }

  public void setDisk_used(String disk_used) {
    this.disk_used = disk_used;
  }

  public String getMetric_a() {
    return metric_a;
  }

  public void setMetric_a(String metric_a) {
    this.metric_a = metric_a;
  }

  public String getMetric_b() {
    return metric_b;
  }

  public void setMetric_b(String metric_b) {
    this.metric_b = metric_b;
  }

  public String getExtra() {
    return extra;
  }

  public void setExtra(String extra) {
    this.extra = extra;
  }

  public String getStat() {
    return stat;
  }

  public void setStat(String stat) {
    this.stat = stat;
  }
}
