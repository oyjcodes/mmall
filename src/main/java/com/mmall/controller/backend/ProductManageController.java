package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Classname ProductManageController
 * @Description 产品后台管理
 * @Date 2019/2/26 14:12
 * @Created by oyj
 */
@RestController
//商品模块的管理
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    //保存商品
    @RequestMapping("save_product.do")
    public ServerResponse saveProduct(HttpServletRequest httpServletRequest,Product product){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户还未登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，进行保存商品操作
           return iProductService.saveOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("操作权限不够");
    }

    //设置商品的状态
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest,Integer productId,Integer status){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户还未登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，进行保存商品操作
            return iProductService.setSaleStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("操作权限不够");
    }

    //获取商品的信息
    @RequestMapping(value = "detail.do")
    public ServerResponse getDetail(HttpServletRequest httpServletRequest,Integer productId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户还未登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
             //填充业务代码
            return iProductService.manageProductDetail(productId);

        }
        return ServerResponse.createByErrorMessage("操作权限不够");
    }

    //获取商品的列表
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    public ServerResponse getList(HttpServletRequest httpServletRequest, @RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10")int pageSize){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户还未登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务代码
            return iProductService.getProductList(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("操作权限不够");
    }
    //查询商品
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    public ServerResponse productSearch(HttpServletRequest httpServletRequest,String productName,Integer productId,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10")int pageSize){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户还未登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务代码
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("操作权限不够");
    }

    //文件上传
    @RequestMapping("upload.do")
    public ServerResponse upload(HttpServletRequest httpServletRequest,@RequestParam(value="upload_file",required = false) MultipartFile file, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户还未登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap = Maps.newHashMap();
            //URI是统一资源标识符，而URL是统一资源定位符
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("操作权限不够");
    }
    //富文本上传
    @RequestMapping("richtext_img_upload.do")
    public Map RichtextImgUpload(HttpServletRequest httpServletRequest, @RequestParam(value="upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            resultMap.put("success",false);
            resultMap.put("msg","请登陆管理员");
            return resultMap;
        }
        String userStrObj = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","请登陆管理员");
            return resultMap;
        }
        //富文本对于返回值有自己的要求，我们使用的是simditor，所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//            "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","用户权限不够");
            return resultMap;
        }
    }
}
