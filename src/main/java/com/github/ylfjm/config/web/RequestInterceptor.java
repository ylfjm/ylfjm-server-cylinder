package com.github.ylfjm.config.web;

import com.github.ylfjm.common.JwtTokenException;
import com.github.ylfjm.common.constant.AuthConstant;
import com.github.ylfjm.common.jwt.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2020/11/29
 */
@Slf4j
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private static List<String> ignores = new ArrayList<>();

    static {
        ignores.add("/api/login");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        log.info("RequestInterceptor start...path=" + path);

        //放开被忽略的请求（配置在配置文件中），不做权限验证
        if (!CollectionUtils.isEmpty(ignores)) {
            for (String prefix : ignores) {
                if (path.startsWith(prefix)) {
                    return true;
                }
            }
        }

        // 3.用户权限校验
        String token = this.getToken(request);
        log.info("RequestInterceptor token:" + token);
        try {
            String newToken = JwtHelper.getGoodToken(token);
            if (StringUtils.hasText(newToken)) {
                //将重新颁发的jwt token设置到响应header里
                response.setHeader(AuthConstant.ADMIN_TOKEN, newToken);
            }
            // 将认证token设置到请求头传递到后续服务
            return true;
        } catch (Exception e) {
            if (e instanceof JwtTokenException) {
                log.error("------JwtTokenException------" + e.getMessage());
                return false;
            } else {
                log.error("------其它Exception------" + e.getMessage());
                return false;
            }
        } finally {
            log.info("RequestInterceptor end...");
        }
    }

    /**
     * 获取token
     * 从请求信息中获取用户认证token
     */
    private String getToken(HttpServletRequest request) {
        // 从请求header中获取用户权限认证信息
        String token = request.getHeader(AuthConstant.ADMIN_TOKEN);
        // 从请求参数中获取用户权限认证信息
        if (!StringUtils.hasText(token)) {
            return request.getParameter(AuthConstant.ADMIN_TOKEN);
        }
        return token;
    }

}
