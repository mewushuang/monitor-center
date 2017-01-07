package com.van.mc.repository;

/**
 * Created by van on 2016/12/19.
 */
public class ObjStatusRequest {


    private String id;

    public ObjStatusRequest() {
    }

    public ObjStatusRequest(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
