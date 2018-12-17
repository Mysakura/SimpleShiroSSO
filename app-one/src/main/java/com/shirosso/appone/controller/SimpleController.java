package com.shirosso.appone.controller;

import com.example.app.common.Constant;
import com.example.app.common.PasswordUtils;
import com.example.app.common.entity.User;
import com.example.app.common.response.BaseResponse;
import com.shirosso.appone.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@RestController
public class SimpleController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public ModelAndView index(){
        Subject subject = SecurityUtils.getSubject();
        System.out.println(subject.getSession().getId());
        return new ModelAndView("index");
    }

    @RequestMapping("/login")
    public BaseResponse<String> login(@RequestBody User user, HttpServletResponse httpServletResponse){
        BaseResponse<String> response = new BaseResponse<>(0,"登陆成功");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                user.getUsername(), user.getPassword());
        subject.login(usernamePasswordToken);
        response.setData("/home");
        // 登陆成功之后，将token放入cookie
        String token = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(Constant.SSO_TOKEN, token);
        cookie.setPath("/");
        cookie.setMaxAge(60*30);
        httpServletResponse.addCookie(cookie);
        // 放入redis
        userService.addTokenInfo(token, new User(user.getUsername(), user.getPassword()));
        return response;
    }

    @RequestMapping("/register")
    public BaseResponse register(@RequestBody User user){
        userService.addUser(user);
        return new BaseResponse(0,"注册成功");
    }

    @RequestMapping("/home")
    public ModelAndView home(){
        ModelAndView mv = new ModelAndView("home");
        mv.addObject("users", userService.getUsers());
        return mv;
    }
}
