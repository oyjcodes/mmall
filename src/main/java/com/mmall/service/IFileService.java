package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname IFileService
 * @Description 处理文件
 * @Date 2019/3/18 14:57
 * @Created by oyj
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
