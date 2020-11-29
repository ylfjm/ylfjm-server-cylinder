package com.github.ylfjm.mapper;

import com.github.ylfjm.pojo.po.RoleMenu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author YLFJM
 * @date 2018/11/2
 */
public interface RoleMenuMapper extends Mapper<RoleMenu> {

    /**
     * 批量插入
     *
     * @param list 角色-菜单关联数据集合
     */
    void batchInsert(@Param("list") List<RoleMenu> list);

    /**
     * 根据角色ID查询菜单ID集合
     *
     * @param roleId 角色ID
     */
    Set<Integer> selectMenuIdsByRoleId(@Param("roleId") Integer roleId);
}
