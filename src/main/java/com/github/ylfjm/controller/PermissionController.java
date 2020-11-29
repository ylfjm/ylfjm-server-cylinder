package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.dto.PermissionCacheDTO;
import com.github.ylfjm.pojo.po.Permission;
import com.github.ylfjm.service.PermissionService;
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

import java.util.Set;

/**
 * 权限
 *
 * @author YLFJM
 * @date 2018/11/3
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 创建权限
     *
     * @param permission 权限信息
     */
    @PostMapping(value = "/permission")
    public void add(@RequestBody Permission permission) {
        permissionService.add(permission);
    }

    /**
     * 删除权限账号
     *
     * @param id 权限ID
     */
    @DeleteMapping(value = "/permission/{id}")
    public void delete(@PathVariable Integer id) {
        permissionService.delete(id);
    }

    /**
     * 更新权限
     *
     * @param permission 权限信息
     */
    @PutMapping(value = "/permission")
    public void update(@RequestBody Permission permission) {
        permissionService.update(permission);
    }

    /**
     * 查询权限列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param menuId   菜单ID
     * @param name     权限名称
     * @param code     权限CODE
     */
    @GetMapping(value = "/permission/{pageNum}/{pageSize}")
    public PageVO<Permission> page(@PathVariable Integer pageNum,
                                   @PathVariable Integer pageSize,
                                   @RequestParam(required = false) Integer menuId,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String code) {
        return permissionService.page(pageNum, pageSize, menuId, name, code);
    }

    /**
     * 获取权限信息
     *
     * @param sysType 所属系统
     * @param adminId 管理员ID
     */
    @GetMapping(value = "/permissionList")
    public Set<PermissionCacheDTO> getPermissionList(@RequestParam Integer sysType,
                                                     @RequestParam(required = false) Integer adminId) {
        return permissionService.getPermissionList(sysType, adminId);
    }

}
