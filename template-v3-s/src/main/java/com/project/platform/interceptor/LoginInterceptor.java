package com.project.platform.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson2.JSON;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.exception.CustomException;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);
        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }
        String path = request.getRequestURL().toString();
        log.info("接口登录拦截：，path：{}", path);
        //获取header的token参数
        String token = request.getHeader("token");
        log.info("登录校验开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            log.info("token为空，请求被拦截");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Claims claims = JwtUtils.verifyJwt(token);
        //获取用户ID
        if (claims == null) {
            log.warn("token无效，请求被拦截");
            throw new CustomException(HttpStatus.UNAUTHORIZED,"token无效，请求被拦截");
        } else {
            CurrentUserDTO currentUserDTO = JSON.parseObject(claims.get("currentUser").toString(), CurrentUserDTO.class);
            CurrentUserThreadLocal.set(currentUserDTO);
            return true;
        }
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        log.info("------------- LoginInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        CurrentUserThreadLocal.clear();
        log.info("LogInterceptor 结束");
    }
}