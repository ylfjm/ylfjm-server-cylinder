package com.github.ylfjm.config.web;

import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.enums.SystemType;
import com.github.ylfjm.common.jwt.JWTInfo;
import com.github.ylfjm.helper.PermissionCacheHelper;
import com.github.ylfjm.pojo.dto.PermissionCacheDTO;
import com.github.ylfjm.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Set;

/**
 * 业务管理系统权限拦截器
 *
 * @author YLFJM
 * @date 2018/8/8.
 */
@Slf4j
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1-获取所有权限列表
        Set<PermissionCacheDTO> permissionList = PermissionCacheHelper.getPList(SystemType.SYSTEM.getValue());
        if (CollectionUtils.isEmpty(permissionList)) {
            permissionList = permissionService.getPermissionList(SystemType.SYSTEM.getValue(), null);
            if (CollectionUtils.isEmpty(permissionList)) {
                return true;
            }
            PermissionCacheHelper.setPList(SystemType.SYSTEM.getValue(), permissionList);
        }
        //2-获取当前接口的信息
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestMapping annotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (annotation == null) {
            return true;
        }
        String method = annotation.method()[0].name();
        String code = annotation.value()[0];

        //3-校验接口是否添加了权限控制，如果添加了权限说明该接口需要访问权限，否则不需要直接允许访问
        if (!this.permissionRequired(permissionList, method, code)) {
            return true;
        }
        //4-如果不是从后台请求的要放行
        try {
            JWTInfo jwtInfo = UserCache.getJWTInfo();
            if (jwtInfo != null && !Objects.equals(jwtInfo.getType(), SystemType.SYSTEM.getValue())) {
                return true;
            }
        } catch (Exception e) {
            log.error("系统权限校验，获取JWTInfo发生异常。");
            return true;
        }
        //5-权限校验
        Integer adminId = UserCache.getId();
        // 获取用户的权限列表
        Set<PermissionCacheDTO> permissions = permissionService.getPermissionList(SystemType.SYSTEM.getValue(), adminId);
        if (CollectionUtils.isEmpty(permissions)) {
            throw new BadRequestException("操作失败，您没有此操作权限");
        }
        for (PermissionCacheDTO permission : permissions) {
            // 如果用户权限列表存在该接口的权限，则校验通过，允许访问
            if (permission.getMethod().equalsIgnoreCase(method) && permission.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        //校验失败，拒绝访问
        throw new BadRequestException("操作失败，您没有此操作权限");
    }

    /**
     * 判断用户当前访问的接口是否拥有权限才可以访问
     *
     * @param permissionList 已经添加的所有权限列表
     * @param method         用户当前访问的接口方法类型
     * @param code           用户当前访问的接口地址
     */
    private boolean permissionRequired(Set<PermissionCacheDTO> permissionList, String method, String code) {
        return permissionList.stream().anyMatch(dto -> dto.getMethod().equalsIgnoreCase(method) && dto.getCode().equalsIgnoreCase(code));
    }

}
