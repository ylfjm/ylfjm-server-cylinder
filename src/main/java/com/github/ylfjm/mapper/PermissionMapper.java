package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.dto.PermissionCacheDTO;
import com.github.ylfjm.pojo.po.Permission;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author YLFJM
 * @date 2018/11/2
 */
public interface PermissionMapper extends Mapper<Permission> {

    /**
     * @param sysType    所属系统
     * @param menuIdList 菜单ID集合
     * @param name       权限名称
     * @param code       权限CODE
     */
    Page<Permission> page(@Param("sysType") Integer sysType,
                          @Param("menuIdList") List<Integer> menuIdList,
                          @Param("name") String name,
                          @Param("code") String code);

    /**
     * 根据系统类型获取权限列表
     *
     * @param sysType 所属系统
     */
    Set<PermissionCacheDTO> selectBySysType(@Param("sysType") Integer sysType);

    /**
     * 根据角色ID获取角色拥有的权限列表
     *
     * @param roleId 角色ID
     */
    Set<PermissionCacheDTO> selectCodeAndMethodByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据菜单ID获取权限ID集合
     *
     * @param menuId 菜单ID
     */
    List<Integer> selectIdByMenuId(@Param("menuId") Integer menuId);

    // /**
    //  * 根据菜单ID获取权限列表
    //  *
    //  * @param menuId 菜单ID
    //  */

    // Page<Permission> selectPermissionByMenuId(@Param("menuId") Integer menuId);

}
