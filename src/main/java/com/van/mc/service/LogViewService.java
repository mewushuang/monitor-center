package com.van.mc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.AdviceAnnotation;
import com.van.monitor.api.LogViewerMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by van on 2016/11/30.
 */
@Service
@AdviceAnnotation
public class LogViewService {

    @Autowired
    private ObjCache objCache;

    private ObjectMapper mapper=new ObjectMapper();




    public String getLogFiles(String objId){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.getLogFiles();
    }

    public String getNext(String objId,String fileName,int lineNum){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.getNext(lineNum, fileName);
    }

    public String resetOffset(String objId,String fileName){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.reset(fileName);
    }

    public String searchByPattern(String objId,String fileName,String pattern){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.setLogOffsetByPattern(pattern,fileName);
    }

    public String searchByPercent(String objId,String fileName,double percent){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.setLogOffsetByPercent(percent,fileName);
    }

    public String delete(String objId,String[] fileNames){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.delete(fileNames);
    }

    public String upload(String objId,String path,String filename,byte[] data){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.uploadFile(path,filename,data);
    }
    public String startDownload(String objId, String fullfilename){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        String ret=viewer.startDownload(fullfilename);
        return ret;
    }
    public byte[] download(String objId,String requestId){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.downloadRecursive(requestId);
    }
    public String endDownload(String objId,String requestId){
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.endDownload(requestId);
    }

    public String getParents(String objId) {
        LogViewerMXBean viewer=objCache.getBean(objId,LogViewerMXBean.class);
        return viewer.getParentPaths();
    }
}
