package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Menu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YLFJM
 * @date 2018/11/2
 */
public interface MenuMapper extends Mapper<Menu> {

    Page<Menu> page(@Param("pid") Integer pid, @Param("name") String name, @Param("level") Integer level);

    /**
     * 获取所有菜单列表
     */
    List<Menu> selectAllMenus();

    /**
     * 获取菜单列表（带权限信息）
     */
    List<Menu> selectMenuWithPermission();

    /**
     * 获取一共有几级菜单
     */
    Integer selectMaxMenuLevel();

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
