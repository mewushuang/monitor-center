package com.van.mc.controller;

import com.van.mc.common.IllegalParamException;
import com.van.mc.repository.Monitor_obj;
import com.van.mc.repository.Monitor_objDao;
import com.van.mc.service.CollectService;
import com.van.mc.service.ObjCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by van on 2016/11/23.
 */
@RestController
public class MonitorObjController {

    private final Logger logger= LoggerFactory.getLogger(MonitorObjController.class);

    @Autowired
    private Monitor_objDao monitor_objDao;

    @Autowired
    private ObjCache objCache;

    @Autowired
    private CollectService collectService;

    @RequestMapping("/obj/list")
    public String showMonitorObj(){

        return  collectService.getAllObjs();
    }



    @RequestMapping("/obj/start")
    public String shutup(@RequestBody CtrlRequest ctrlRequest){
        String s = collectService.startRemotely(ctrlRequest.getObjId(),ctrlRequest.getArgs());
        //logger.info(s);
        return s;
    }

    @RequestMapping("/obj/stop")
    public String  shutdown(@RequestBody CtrlRequest ctrlRequest){
        String s = collectService.stopRemotely(ctrlRequest.getObjId(),ctrlRequest.getArgs());
        //logger.info(s);
        return s;
    }

    @RequestMapping("/obj/restart")
    public String  restart(@RequestBody CtrlRequest ctrlRequest){
        String s = collectService.restartDaemon(ctrlRequest.getObjId());
        //logger.info(s);
        return s;
    }

    @RequestMapping("/obj/delete")
    public String  delete(@RequestBody CtrlRequest ctrlRequest){
        String s = collectService.deleteMonitorObj(ctrlRequest.getObjId());
        //logger.info(s);
        return s;
    }

    @RequestMapping("/obj/reload")
    public RestResponse reloadCache(){
        objCache.loadCache();
        return RestResponse.getSuccResponse(null);
    }

    @RequestMapping("/update")
    public RestResponse updateObj(@RequestBody Monitor_obj monitor_obj){
        if(monitor_obj.getName()==null||monitor_obj.getUrl()==null||monitor_obj.getCtrlname()==null){
            throw new IllegalParamException("param cannot be empty,name:"+monitor_obj.getName()+",url:"+monitor_obj.getUrl()+",ctrlname:"+monitor_obj.getCtrlname());
        }
        //System.out.println(monitor_obj);
        if(monitor_obj.getId()==null){//插入
            monitor_obj=monitor_objDao.add(monitor_obj);
        }else {
            monitor_objDao.update(monitor_obj);
        }
        objCache.cache(monitor_obj);
        return RestResponse.getSuccResponse(monitor_obj);
    }
    @RequestMapping("/config/context_path.js")
    @ResponseBody
    public ResponseEntity getContextPath(HttpServletRequest request){
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CACHE_CONTROL, "public,max-age=25920000")
                .header(HttpHeaders.CONTENT_TYPE,"application/javascript")
                .body("window.___context___path='"+request.getContextPath()+"'");
    }
    //  2016/12/1 异常在service层处理
}
