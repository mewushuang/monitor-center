package com.van.mc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.service.LogViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by van on 2016/11/30.
 */
@RestController
public class LogViewController {

    private Logger logger= LoggerFactory.getLogger(LogViewController.class);

    @Autowired
    private LogViewService logViewService;
    private ObjectMapper mapper=new ObjectMapper();

    @RequestMapping("/obj/fileList")
    public String getLogFiles(@RequestParam(value = "objId")int objId){
        String ret = logViewService.getLogFiles(objId + "");
        //logger.info(ret);
        return ret;
    }

    @RequestMapping("/obj/log/next")
    public String nextLines(@RequestParam(value = "objId")String objId,
                              @RequestParam(value = "fileName")String fileName,
                              @RequestParam(value = "lineNum")int lineNum){
        String ret = logViewService.getNext(objId,fileName,lineNum);
        logger.info(ret);
        return ret;
    }

    @RequestMapping("/obj/log/patternSearch")
    public String searchByPatter(@RequestParam(value = "objId")String objId,
                              @RequestParam(value = "fileName")String fileName,
                              @RequestParam(value = "pattern")String pattern){
        String ret = logViewService.searchByPattern(objId,fileName,pattern);
        //logger.info(ret);
        return ret;
    }

    @RequestMapping("/obj/log/percentSearch")
    public String searchByPercent(@RequestParam(value = "objId")String objId,
                                 @RequestParam(value = "fileName")String fileName,
                                 @RequestParam(value = "percent")double percent){
        String ret = logViewService.searchByPercent(objId,fileName,percent);
        //logger.info(ret);
        return ret;
    }

    @RequestMapping("/obj/log/reset")
    public String resetLogOffset(@RequestParam(value = "objId")String objId,
                                 @RequestParam(value = "fileName")String fileName){
        String ret = logViewService.resetOffset(objId,fileName);
        //logger.info(ret);
        return ret;
    }

    @RequestMapping("/obj/log/delete")
    public String resetLogOffset(@RequestParam(value = "objId")String objId,
                                 @RequestParam(value = "fileNames")String[] fileNames){
        String ret = logViewService.delete(objId,fileNames);
        //logger.info(ret);
        return ret;
    }

    @PostMapping("/file/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
          @RequestParam("parent") String parent, @RequestParam("objId") String objId ) throws IOException {
        byte[] bytes;

        if (!file.isEmpty()) {
            bytes = file.getBytes();
            String ret = logViewService.upload(objId, parent, file.getOriginalFilename(), bytes);
            return ret;
        }
        return mapper.writeValueAsString(new RestResponse(400,"file is empty!",null));

    }
    @RequestMapping("/file/parent")
    public String getParents(@RequestParam("objId") String objId){
        return logViewService.getParents(objId);
    }



    /**
     * 下载
     * @param objId
     * @param fileName
     * @param response
     * @throws IOException
     */
    @RequestMapping("/file/download")
    public void download(@RequestParam("objId")String objId, @RequestParam("fileName") String fileName
        , HttpServletResponse response) throws IOException {
        String ret=logViewService.startDownload(objId,fileName);
        String id=null;
        RestResponse resp;
        try {
            resp=mapper.readValue(ret,RestResponse.class);
            if(resp!=null&&resp.getCode()==200){
                id = resp.getData().toString();
            }
        } catch (IOException e) {
            try {
                ret= mapper.writeValueAsString(new RestResponse(500,"parse failure on  msg: "+ret,null));
            } catch (JsonProcessingException e1) {
            }
        }
        if(id==null){
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter pw=response.getWriter();
            pw.print(ret);
            pw.flush();
        }else {
            response.setHeader("Content-Disposition","attachment;filename="+fileName);
            OutputStream os = response.getOutputStream();
            byte[] data=null;
            while (!Thread.currentThread().isInterrupted()){
                data=logViewService.download(objId,id);
                if(data==null||data.length==0){
                    os.close();
                    break;
                }else {
                    os.write(data);
                    os.flush();
                }
            }
            logViewService.endDownload(objId,id);
        }
    }


}
