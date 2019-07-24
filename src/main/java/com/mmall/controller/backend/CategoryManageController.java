package com.mmall.controller.backend;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname CategoryManageController
 * @Description 商品的类型管理
 * @Date 2019/2/16 15:32
 * @Created by oyj
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    //添加商品分类
    @RequestMapping("add_category.do")
    public ServerResponse addCategory(HttpServletRequest httpServletRequest, String categoryName, @RequestParam(value = "parentid",defaultValue = "0") int parentId){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userStrObj = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStrObj, User.class);
//        if(user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户还未登陆，需要先登陆");
//        }
//        //用户已登录，则判断是否是管理员
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //是管理员，进行添加商品分类的操作
//          return iCategoryService.addCategory(categoryName, parentId);
//        }
//        return ServerResponse.createByErrorMessage("用户的操作权限不够,需要管理员权限");
        return iCategoryService.addCategory(categoryName, parentId);
    }

    //更新商品类名
    @RequestMapping("set_category_name")
    public ServerResponse setCategoryName(HttpServletRequest httpServletRequest,Integer categoryId,String categotyName){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userStrObj = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStrObj, User.class);
//        if(user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户还未登陆，需要先登陆");
//        }
//        //用户已登录，则判断是否是管理员
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //是管理员
//            return iCategoryService.updateCategoryName(categoryId,categotyName);
//        }
//        return ServerResponse.createByErrorMessage("用户的操作权限不够,需要管理员权限");
        return iCategoryService.updateCategoryName(categoryId,categotyName);
    }
    //根据传入的categoryId,获取该类别下平级的子节点信息，不进行递归
    @RequestMapping("get_category")
    public ServerResponse getChildrenParallelCategory(HttpServletRequest httpServletRequest,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userStrObj = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStrObj, User.class);
//        if(user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户还未登陆，需要先登陆");
//        }
//        //用户已登录，则判断是否是管理员
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //是管理员
//            return iCategoryService.getChildrenCategoryByParentId(categoryId);
//        }
//        return ServerResponse.createByErrorMessage("用户的操作权限不够,需要管理员权限");
        return iCategoryService.getChildrenCategoryByParentId(categoryId);
    }

    //根据传入的categoryId,获取该类别下平级的子节点信息，进行递归
    @RequestMapping("get_deep_category")
    public ServerResponse getChildrenDeepCategory(HttpServletRequest httpServletRequest,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if(StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userStrObj = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userStrObj, User.class);
//        if(user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户还未登陆，需要先登陆");
//        }
//        //用户已登录，则判断是否是管理员
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //进行递归查询
//            return iCategoryService.selectCategoryAndChildById(categoryId);
//        }
//        return ServerResponse.createByErrorMessage("用户的操作权限不够,需要管理员权限");
        return iCategoryService.selectCategoryAndChildById(categoryId);
    }

}
