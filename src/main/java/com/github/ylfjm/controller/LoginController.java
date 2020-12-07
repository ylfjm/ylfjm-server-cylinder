package com.github.ylfjm.controller;

import com.github.ylfjm.common.constant.AuthConstant;
import com.github.ylfjm.common.jwt.JWTInfo;
import com.github.ylfjm.common.jwt.JwtHelper;
import com.github.ylfjm.common.utils.IpUtil;
import com.github.ylfjm.pojo.po.Admin;
import com.github.ylfjm.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述：各端登录逻辑控制器
 *
 * @author YLFJM
 * @Date 2019/3/26
 */
@RestController
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final AdminService adminService;

    /**
     * 系统管理员登录
     *
     * @param userName 用户名
     * @param password 密码
     */
    @PostMapping(value = "/login")
    public Admin login(@RequestParam("userName") String userName,
                       @RequestParam("password") String password,
                       HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtil.getIp(request);
        Admin admin = adminService.login(userName, password, ip);
        JWTInfo jwtInfo = new JWTInfo();
        jwtInfo.setId(admin.getId());
        jwtInfo.setAccount(admin.getUserName());
        jwtInfo.setRealName(admin.getRealName());
        jwtInfo.setPostCode(admin.getPostCode());
        String jwtToken = JwtHelper.createJWTToken(jwtInfo);
        response.setHeader(AuthConstant.ADMIN_TOKEN, jwtToken);
        return admin;
    }

}
