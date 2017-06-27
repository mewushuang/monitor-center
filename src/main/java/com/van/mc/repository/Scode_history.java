package com.van.mc.repository;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class Scode_history {
  private String scode_id;
  private String data_src;
  private String frequency;
  private java.sql.Timestamp updated_at;
  private java.sql.Timestamp local_server_time;

  public String getScode_id() {
    return scode_id;
  }

  public void setScode_id(String scode_id) {
    this.scode_id = scode_id;
  }

  public String getData_src() {
    return data_src;
  }

  public void setData_src(String data_src) {
    this.data_src = data_src;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
  public Timestamp getUpdated_at() {
    return updated_at;
  }

  public void setUpdated_at(Timestamp updated_at) {
    this.updated_at = updated_at;
  }
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
  public Timestamp getLocal_server_time() {
    return local_server_time;
  }

  public long getInterval(){
    return System.currentTimeMillis()-updated_at.getTime();
  }

  public void setLocal_server_time(Timestamp local_server_time) {
    this.local_server_time = local_server_time;
  }
}
