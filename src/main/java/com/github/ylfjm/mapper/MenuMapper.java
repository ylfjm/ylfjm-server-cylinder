package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Menu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author YLFJM
 * @date 2018/11/2
 */
public interface MenuMapper extends Mapper<Menu> {

    Page<Menu> page(@Param("sysType") Integer sysType, @Param("pid") Integer pid, @Param("name") String name, @Param("level") Integer level);

    /**
     * 获取所有菜单列表
     */
    List<Menu> selectAllMenus(@Param("sysType") Integer sysType);

    /**
     * 获取所有菜单ID
     */
    Set<Integer> selectAllMenuId(@Param("sysType") Integer sysType);

    /**
     * 获取一级菜单列表
     */
    List<Menu> selectFirstLevelMenus(@Param("sysType") Integer sysType);

    /**
     * 获取菜单列表（带权限信息）
     */
    List<Menu> selectMenuWithPermission(@Param("sysType") Integer sysType);

    /**
     * 获取一共有几级菜单
     */
    Integer selectMaxMenuLevel(@Param("sysType") Integer sysType);

    /**
     * 根据权限ID集合获取菜单集合
     *
     * @param permissionIds 权限ID集合
     */
    Set<Menu> selectMenuBypermissionIds(@Param("permissionIds") Set<Integer> permissionIds);

    /**
     * 根据id查询pid
     *
     * @param id 菜单ID
     */
    Integer selectPidById(@Param("id") Integer id);

    /**
     * 获取管理员所拥有的菜单
     *
     * @param adminId 管理员ID
     */
    List<Menu> selectMenusByAdminId(@Param("adminId") Integer adminId);
}
