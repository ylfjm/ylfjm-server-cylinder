package com.github.ylfjm.service;

import com.github.ylfjm.mapper.AdminLogMapper;
import com.github.ylfjm.pojo.po.AdminLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员登录日志业务类
 *
 * @author YLFJM
 * @date 2018/11/13
 */
@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogMapper adminLogMapper;

    /**
     * 创建管理员登录日志
     *
     * @param adminLog 管理员登录日志
     */
    public AdminLog save(AdminLog adminLog) {
        adminLogMapper.insert(adminLog);
        return adminLog;
    }

    /**
     * 查询管理员登录日志
     *
     * @param query 查询条件
     */
    public List<AdminLog> list(AdminLog query) {
        return null;
    }

}
