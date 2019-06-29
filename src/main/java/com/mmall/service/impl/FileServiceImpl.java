package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Classname FileServiceImpl
 * @Description TODO
 * @Date 2019/3/18 14:58
 * @Created by oyj
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {
    //private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //获取上传的文件扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        log.info("开始上传文件，上传文件的文件名:{}，上传的路径:{}，新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            //对应的文件目录不存在,则创建该路径目录
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //包括完整的路径名+文件名
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);

            //将targetFile上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //上传完之后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
           log.error("上传文件异常",e);
           return null;
        }
        return targetFile.getName();
    }

}
