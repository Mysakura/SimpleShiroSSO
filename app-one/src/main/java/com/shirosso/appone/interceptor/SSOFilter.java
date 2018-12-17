package com.shirosso.appone.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.example.app.common.Constant;
import com.example.app.common.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


public class SSOFilter extends AccessControlFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = null;
        User user = null;
        for(Cookie c : request.getCookies()){
            if (Constant.SSO_TOKEN.equals(c.getName())){
                token = c.getValue();
                break;
            }
        }
        Subject subject = SecurityUtils.getSubject();
        boolean authenticated = subject.isAuthenticated();// 是否通过身份验证
        if (token != null){
            // 如果是登出操作，需要清除公共token信息
            if(request.getRequestURI().equals("/logout")){
                stringRedisTemplate.delete(Constant.TOKEN_PRE + token);
                subject.logout();
                return true;
            }
            String s = stringRedisTemplate.boundValueOps(Constant.TOKEN_PRE + token).get();
            user = JSONObject.parseObject(s, User.class);// 根据token获取用户信息
            if (user != null){
                // 有用户信息并且没有身份认证
                if(!authenticated){
                    // 手动通过，因为在其它系统已经登录
                    subject.login(new UsernamePasswordToken(user.getUsername(), user.getPassword()));
                }
            }else{
                // 没有用户信息，说明已经超时或者退出登录，需要清除当前的认证信息
                if (authenticated){
                    subject.logout();
                }
            }
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return false;
    }
}
