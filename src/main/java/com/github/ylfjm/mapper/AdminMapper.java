package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Admin;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AdminMapper extends Mapper<Admin> {

    /**
     * 获取管理员列表，携带角色信息
     *
     * @param sysType   所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  姓名
     * @param forbidden 禁用状态
     */
    Page<Admin> selectWithRole(@Param("sysType") Integer sysType,
                               @Param("roleId") Integer roleId,
                               @Param("deptId") Integer deptId,
                               @Param("realName") String realName,
                               @Param("forbidden") Boolean forbidden);

}
