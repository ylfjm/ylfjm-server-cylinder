package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.po.Admin;
import com.github.ylfjm.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 创建管理员
     *
     * @param admin 管理员信息
     */
    @PostMapping(value = "/admin")
    public void add(@RequestBody Admin admin) {
        adminService.add(admin, admin.getRoleIds());
    }

    /**
     * 删除管理员账号
     *
     * @param id 管理员ID
     */
    @DeleteMapping(value = "/admin/{id}")
    public void delete(@PathVariable Integer id) {
        adminService.delete(id);
    }

    /**
     * 禁用/启用管理员账号
     *
     * @param id 管理员ID
     */
    @PatchMapping(value = "/admin/{id}/forbidden")
    public void forbidden(@PathVariable Integer id) {
        adminService.forbidden(id);
    }

    /**
     * 更新管理员
     *
     * @param admin 管理员信息
     */
    @PutMapping(value = "/admin")
    public void update(@RequestBody Admin admin) {
        adminService.update(admin, admin.getRoleIds());
    }

    /**
     * 查询管理员列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  管理员姓名
     * @param forbidden 禁用状态
     */
    @GetMapping(value = "/admin/{pageNum}/{pageSize}")
    public PageVO<Admin> page(@PathVariable Integer pageNum,
                              @PathVariable Integer pageSize,
                              @RequestParam(required = false) Integer roleId,
                              @RequestParam(required = false) Integer deptId,
                              @RequestParam(required = false) String realName,
                              @RequestParam(required = false) Boolean forbidden) {
        return adminService.page(pageNum, pageSize, roleId, deptId, realName, forbidden);
    }

    // /**
    //  * 获取管理员所拥有的菜单
    //  *
    //  * @param id 管理员ID
    //  */
    // @GetMapping(value = "/admin/{id}/menus")
    // public List<Menu> listMenus(@PathVariable Integer id) {
    //     return adminService.listMenusByAdminId(id);
    // }
    //
    // /**
    //  * 获取管理员权限列表
    //  *
    //  * @param id 管理员ID
    //  */
    // @GetMapping(value = "/admin/{id}/permissions")
    // public Set<PermissionCache> listPermissions(@PathVariable Integer id) {
    //     return adminService.listPermissionsByAdminId(id);
    // }

}
