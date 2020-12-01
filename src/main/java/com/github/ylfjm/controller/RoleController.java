package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.po.Role;
import com.github.ylfjm.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色
 *
 * @author YLFJM
 * @date 2018/11/3
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 创建角色
     *
     * @param role 角色信息
     */
    @PostMapping(value = "/role")
    public void add(@RequestBody Role role) {
        roleService.add(role);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @DeleteMapping(value = "/role/{id}")
    public void delete(@PathVariable Integer id) {
        roleService.delete(id);
    }

    /**
     * 更新角色
     *
     * @param role 角色信息
     */
    @PutMapping(value = "/role")
    public void update(@RequestBody Role role) {
        roleService.update(role);
    }

    /**
     * 更新角色用户
     *
     * @param role 角色信息
     */
    @PutMapping(value = "/roleUser")
    public void updateRoleUser(@RequestBody Role role) {
        roleService.updateRoleUser(role, role.getUserIds());
    }

    /**
     * 更新角色菜单
     *
     * @param role 角色信息
     */
    @PutMapping(value = "/roleMenu")
    public void updateRoleMenu(@RequestBody Role role) {
        roleService.updateRoleMenu(role, role.getMenuIds());
    }

    /**
     * 更新角色权限
     *
     * @param role 角色信息
     */
    @PutMapping(value = "/rolePermission")
    public void updateRolePermission(@RequestBody Role role) {
        roleService.updateRolePermission(role, role.getPermissionIds());
    }

    /**
     * 查询角色列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param name     角色名称
     */
    @GetMapping(value = "/role/{pageNum}/{pageSize}")
    public PageVO<Role> page(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize,
                             @RequestParam(required = false) String name) {
        return roleService.page(pageNum, pageSize, name);
    }

}
