package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName:UserController
 * @Description 前台用户接口
 * @Author oyj
 * @Date 2018/11/23 16:08
 * @Version 1.0
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户登陆方法
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    //登陆接口
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        ServerResponse<User> response = iUserService.login(username, password);
        //登陆成功，将用户信息存储在session中
        if (response.isSuccess()) {
           // session.setAttribute(Const.CURRENT_USER, response.getData());
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            //sessionId:  8FF6EDA78AB9858725B1983298610F8E
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }


    //注销接口
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        // session.removeAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        //删除浏览器中的cookie
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        //删除redis中的cookie
        RedisShardedPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess("退出登陆成功！");
    }



    //注册接口
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    //校验接口，根据type传入的类型进行不同的校验
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }
    //获取用户的接口
    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
//        if(user!=null){
//            return ServerResponse.createBySuccess(user);
//        }else{
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);

        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    //找回密保问题接口
    @RequestMapping(value="forget_get_question.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }



    //验证用户输入的问题答案是否正确
    @RequestMapping(value="forget_check_answer.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }



    //未登录情况下，忘记了密码。依据密保问题获取的token，重置密码
    @RequestMapping(value="forget_reset_password.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwoedNew,String forgetToken){
       return iUserService.forgetRestPassword(username,passwoedNew,forgetToken);
    }

    //已经登陆系统情况下，重置密码
    @RequestMapping(value="reset_password.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String passwordOld,String passwordNew){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
       if(user == null){
           return ServerResponse.createByErrorMessage("用户未登录");
       }
       return iUserService.resetPassword(passwordOld,passwordNew,user);
    }


    //登陆状态，更新个人用户信息接口
    @RequestMapping(value="update_information.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest httpServletRequest,User user){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisShardedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userStrObj, User.class);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //只有在登陆状态下才能更新该用户信息
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            //session.setAttribute(Const.CURRENT_USER,response.getData());
            RedisShardedPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }
        return response;
    }

    //查询个人用户信息
    @RequestMapping(value="get_information.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStrObj = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStrObj, User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
        }
        return iUserService.getInformation(user.getId());
    }

}
