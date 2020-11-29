package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.po.Menu;
import com.github.ylfjm.service.MenuService;
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

import java.util.List;

/**
 * 菜单
 *
 * @author YLFJM
 * @date 2018/11/3
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 添加菜单
     *
     * @param menu 菜单信息
     */
    @PostMapping(value = "/menu")
    public void add(@RequestBody Menu menu) {
        menuService.add(menu);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    @DeleteMapping(value = "/menu/{id}")
    public void delete(@PathVariable Integer id) {
        menuService.delete(id);
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     */
    @PutMapping(value = "/menu")
    public void update(@RequestBody Menu menu) {
        menuService.update(menu);
    }

    /**
     * 查询菜单列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param pid      父菜单ID
     * @param name     菜单名称
     * @param level    菜单层级
     */
    @GetMapping(value = "/menu/{pageNum}/{pageSize}")
    public PageVO<Menu> page(@PathVariable Integer pageNum,
                             @PathVariable Integer pageSize,
                             @RequestParam(required = false) Integer pid,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) Integer level) {
        return menuService.page(pageNum, pageSize, pid, name, level);
    }

    /**
     * 获取级联菜单结构
     */
    @GetMapping(value = "/menu/tree")
    public List<Menu> listMenuTree(@RequestParam(required = false) Integer roleId) {
        return menuService.listMenuTree(roleId);
    }

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @param roleId 角色ID
     */
    @GetMapping(value = "/menu/permission")
    public List<Menu> listMenuAndPermission(@RequestParam(required = false) Integer roleId) {
        return menuService.listMenuAndPermission(roleId);
    }

}
