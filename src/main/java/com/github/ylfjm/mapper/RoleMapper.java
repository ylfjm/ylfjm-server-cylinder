package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Role;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author YLFJM
 * @date 2018/11/2
 */
public interface RoleMapper extends Mapper<Role> {

    Page<Role> page(@Param("name") String name);

}
