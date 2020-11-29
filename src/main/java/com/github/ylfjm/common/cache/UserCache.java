package com.github.ylfjm.common.cache;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ylfjm.common.NoLoginException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.constant.AuthConstant;
import com.github.ylfjm.common.jwt.JWTInfo;
import com.github.ylfjm.common.jwt.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 用户相关信息缓存类
 *
 * @author YLFJM
 * @date 2018/7/10
 */
@Slf4j
public class UserCache {

    /**
     * 从请求中获取JWTInfo
     */
    private static String getJWTInfoStr() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        // 从请求header中获取用户权限认证信息
        // String jwtInfoStr = request.getHeader(AuthConstant.JWTINFO);
        String adminToken = request.getHeader(AuthConstant.ADMIN_TOKEN);
        if (!StringUtils.hasText(adminToken)) {
            throw new NoLoginException();
        }
        return JwtHelper.getJWTInfoFromToken(adminToken);
    }

    /**
     * 从请求中获取JWTInfo
     */
    public static JWTInfo getJWTInfo() {
        try {
            // String jwtInfoStr = URLDecoder.decode(getJWTInfoStr(), "UTF-8");
            String jwtInfoStr = getJWTInfoStr();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jwtInfoStr, JWTInfo.class);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new YlfjmException("操作失败");
        } catch (JsonParseException e) {
            log.error(e.getMessage(), e);
            throw new YlfjmException("操作失败");
        } catch (JsonMappingException e) {
            log.error(e.getMessage(), e);
            throw new YlfjmException("操作失败");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new YlfjmException("操作失败");
        }
    }

    /**
     * 获取当前登录的管理员的姓名
     */
    public static String getCurrentAdminRealName() {
        JWTInfo jwtInfo = getJWTInfo();
        if (jwtInfo != null) {
            return jwtInfo.getRealName();
        }
        throw new NoLoginException();
    }

    /**
     * 获取当前登录的用户ID
     */
    public static Integer getId() {
        JWTInfo jwtInfo = getJWTInfo();
        if (jwtInfo != null) {
            return jwtInfo.getId();
        }
        throw new NoLoginException();
    }

    /**
     * 获取当前登录的用户的账号
     */
    public static String getAccount() {
        JWTInfo jwtInfo = getJWTInfo();
        if (jwtInfo != null) {
            return jwtInfo.getAccount();
        }
        throw new NoLoginException();
    }

}