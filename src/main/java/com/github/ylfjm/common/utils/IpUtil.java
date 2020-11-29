package com.github.ylfjm.common.utils;

import javax.servlet.http.HttpServletRequest;

public final class IpUtil {

    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        //如果是多级代理
        if (ip != null && ip.contains(",")) {
            return ip.substring(ip.lastIndexOf(",") + 1).trim();
        }
        return ip;
    }

}
