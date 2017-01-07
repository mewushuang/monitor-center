package com.van.mc.controller;

import com.van.mc.repository.Monitor_obj;
import com.van.mc.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by van on 2016/12/19.
 */
@Controller
public class StatusPushController {

    @Autowired
    private SimpMessagingTemplate template;


    @MessageMapping("/status-info/{objId}")
    //@SendTo("/topic/statuses")
    public Monitor_obj getStatus(@DestinationVariable String objId){
        Monitor_obj m= new Monitor_obj();
        m.setParams("sssing");
        //m.setId(objStatusRequest.getId());
        System.out.println("push recv:"+objId);
        //destination必须匹配提供的broker，才能被订阅到
        template.convertAndSend(PushService.STATUS_DESTINATION,m);
        return m;
    }
}
