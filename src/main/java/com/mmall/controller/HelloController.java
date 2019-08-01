package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Classname HelloController
 * @Description hello
 * @Date 2019/8/1 17:56
 * @Created by oyj
 */
@Controller
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        return "上线成功";
    }
}