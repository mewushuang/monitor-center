package com.van.mc.demo;

import com.van.mc.repository.Monitor_objDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by van on 2016/11/18.
 */
@RestController
public class DemoCtr {
    @Autowired
    Monitor_objDao monitor_objDao;

    @RequestMapping("/greeting")
    public Map<String, Object> greet(@RequestParam(value = "name", defaultValue = "world") String name) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("content", String.format("hello %s", name));
        return m;
    }
}
