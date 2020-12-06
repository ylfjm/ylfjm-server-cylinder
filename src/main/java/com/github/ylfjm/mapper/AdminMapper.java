package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Admin;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AdminMapper extends Mapper<Admin> {

    /**
     * 获取管理员列表，携带角色信息
     *
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param postCode  职位代码
     * @param realName  姓名
     * @param forbidden 禁用状态
     */
    Page<Admin> selectWithRole(@Param("roleId") Integer roleId,
                               @Param("deptId") Integer deptId,
                               @Param("postCode") String postCode,
                               @Param("realName") String realName,
                               @Param("forbidden") Boolean forbidden);

}
