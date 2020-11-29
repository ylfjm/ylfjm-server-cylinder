package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.NotFoundException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.enums.SexType;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.AdminLogMapper;
import com.github.ylfjm.mapper.AdminMapper;
import com.github.ylfjm.mapper.AdminRoleMapper;
import com.github.ylfjm.mapper.DepartmentMapper;
import com.github.ylfjm.mapper.MenuMapper;
import com.github.ylfjm.mapper.PermissionMapper;
import com.github.ylfjm.pojo.po.Admin;
import com.github.ylfjm.pojo.po.AdminLog;
import com.github.ylfjm.pojo.po.AdminRole;
import com.github.ylfjm.pojo.po.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 管理员业务类
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final AdminLogMapper adminLogMapper;
    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private final DepartmentMapper departmentMapper;
    private final PermissionMapper permissionMapper;
    private final AdminRoleMapper adminRoleMapper;

    /**
     * 管理员登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param sysType  所属系统
     */
    public Admin login(String userName, String password, Integer sysType, String ip) {
        Admin query = new Admin();
        query.setUserName(userName);
        query.setSysType(sysType);
        Admin admin = adminMapper.selectOne(query);
        if (admin == null) {
            throw new RuntimeException("登录失败，该用户不存在");
        }
        AdminLog adminLog = new AdminLog();
        adminLog.setAdminId(admin.getId());
        adminLog.setUserName(admin.getUserName());
        adminLog.setRealName(admin.getRealName());
        adminLog.setTime(new Date());
        adminLog.setIp(ip);
        // 禁用校验
        if (admin.getForbidden()) {
            adminLog.setSuccess(false);
            adminLog.setCause("账户被锁定");
            adminLogMapper.insert(adminLog);
            // 记录登录日志
            throw new BadRequestException("登录失败，该账号已被禁用");
        }
        // 密码校验
        if (!admin.getPassword().equalsIgnoreCase(password)) {
            adminLog.setSuccess(false);
            adminLog.setCause("密码输入错误");
            adminLogMapper.insert(adminLog);
            // 记录登录日志
            throw new BadRequestException("登录失败，密码错误");
        }
        adminLog.setSuccess(true); //登录成功
        adminLogMapper.insert(adminLog);
        return this.getFullAdminInfo(admin);
    }

    /**
     * 获取管理员信息，并设置相关额外属性
     *
     * @param admin 管理员信息
     */
    private Admin getFullAdminInfo(Admin admin) {
        // 获取管理员所拥有的菜单列表
        List<Menu> menus = this.listMenusByAdminId(admin.getId());
        admin.setMenus(menus);
        return admin;
    }

    /**
     * 创建管理员
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Admin admin, Set<Integer> roleIds) {
        admin.setSysType(UserCache.getJWTInfo().getType());
        // 参数校验
        this.checkParams(admin, roleIds);
        // 校验密码
        if (!StringUtils.hasText(admin.getPassword())) {
            throw new BadRequestException("操作失败，密码不能为空");
        }
        // 校验用户名是否已被使用
        Admin query = new Admin();
        query.setUserName(admin.getUserName());
        query.setSysType(admin.getSysType());
        int count = adminMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("操作失败，用户名已存在");
        }
        // 设置创建人
        admin.setCreator(UserCache.getCurrentAdminRealName());
        // 设置创建时间
        admin.setCreateTime(new Date());
        // 设置禁用状态为不禁用（默认）
        admin.setForbidden(false);
        admin.setId(null);
        int result = adminMapper.insert(admin);
        if (result < 1) {
            throw new YlfjmException("操作失败，新增用户发生错误");
        }
        // 保存管理员-角色映射关系
        this.saveAdminRole(admin.getId(), roleIds);
    }

    /**
     * 删除管理员
     *
     * @param id 管理员ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        Admin admin = adminMapper.selectByPrimaryKey(id);
        if (admin == null) {
            throw new NotFoundException("操作失败，用户不存在或已被删除");
        }
        Integer sysType = UserCache.getJWTInfo().getType();
        if (!Objects.equals(admin.getSysType(), sysType)) {
            throw new BadRequestException("非法操作");
        }
        adminMapper.deleteByPrimaryKey(id);
        AdminRole adminRole = new AdminRole();
        adminRole.setAdminId(id);
        adminRoleMapper.delete(adminRole);
    }

    /**
     * 禁用/启用管理员账号
     *
     * @param id 管理员ID
     */
    public void forbidden(Integer id) {
        Admin admin = adminMapper.selectByPrimaryKey(id);
        if (admin == null) {
            throw new NotFoundException("操作失败，用户不存在或已被删除");
        }
        Integer sysType = UserCache.getJWTInfo().getType();
        if (!Objects.equals(admin.getSysType(), sysType)) {
            throw new BadRequestException("非法操作");
        }
        Admin update = new Admin();
        update.setId(admin.getId());
        // 设置更新人
        update.setUpdater(UserCache.getCurrentAdminRealName());
        // 设置更新时间
        update.setUpdateTime(new Date());
        if (admin.getForbidden()) {
            // 设置禁用状态为false
            update.setForbidden(false);
        } else {
            // 设置禁用状态为true
            update.setForbidden(true);
        }
        adminMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 更新管理员账号
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Admin admin, Set<Integer> roleIds) {
        // 参数校验
        this.checkParams(admin, roleIds);
        if (Objects.isNull(admin.getId())) {
            throw new BadRequestException("操作失败，请选择用户");
        }
        Admin record = adminMapper.selectByPrimaryKey(admin.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，用户不存在或已被删除");
        }
        Integer sysType = UserCache.getJWTInfo().getType();
        if (!Objects.equals(record.getSysType(), sysType)) {
            throw new BadRequestException("非法操作");
        }
        // 校验用户名是否已被使用
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", admin.getId());
        criteria.andEqualTo("userName", admin.getUserName());
        criteria.andEqualTo("sysType", record.getSysType());
        int count = adminMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("操作失败，该用户名已存在");
        }
        if (!StringUtils.hasText(admin.getPassword()) || record.getPassword().equalsIgnoreCase(admin.getPassword())) {
            // 不更新密码
            admin.setPassword(null);
        }
        // 设置更新人
        admin.setUpdater(UserCache.getCurrentAdminRealName());
        // 设置更新时间
        admin.setUpdateTime(new Date());
        // 不更新sysType
        admin.setSysType(null);
        int result = adminMapper.updateByPrimaryKeySelective(admin);
        if (result < 1) {
            throw new YlfjmException("操作失败，修改用户发生错误");
        }
        // 如果前端传递的参数中角色ID集合不为空，则需要变更管理员的角色信息
        // 保存管理员-角色映射关系
        this.saveAdminRole(admin.getId(), roleIds);
    }

    /**
     * 分页查询管理员信息，可带查询条件
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  姓名
     * @param forbidden 禁用状态
     */
    public PageVO<Admin> page(int pageNum, int pageSize, Integer roleId, Integer deptId, String realName, Boolean forbidden) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Admin> page = adminMapper.selectWithRole(UserCache.getJWTInfo().getType(), roleId, deptId, realName, forbidden);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page);
    }

    /**
     * 获取管理员所拥有的菜单
     *
     * @param id 管理员ID
     */
    public List<Menu> listMenusByAdminId(Integer id) {
        // 首先获取该管理员的所有菜单集合
        List<Menu> menus = menuMapper.selectMenusByAdminId(id);

        // 获取该角色下所有菜单
        List<Menu> firstLevelMenus = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getLevel() == 1) {
                firstLevelMenus.add(menu);
            }
        }

        // 为一级菜单设置子菜单准备递归
        for (Menu menu : firstLevelMenus) {
            // 获取父菜单下所有子菜单调用getChildList
            List<Menu> childList = menuService.getChildList(menu.getId(), menus);
            menu.setSubMenus(childList);
        }
        return firstLevelMenus;
    }

    /**
     * 参数校验
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    private void checkParams(Admin admin, Set<Integer> roleIds) {
        // 校验用户名
        if (!StringUtils.hasText(admin.getUserName())) {
            throw new BadRequestException("操作失败，用户名不能为空");
        }
        // 校验姓名
        if (!StringUtils.hasText(admin.getRealName())) {
            throw new BadRequestException("操作失败，姓名不能为空");
        }
        // 校验性别
        if (admin.getSex() == null || SexType.find(admin.getSex()) == null) {
            throw new BadRequestException("操作失败，性别填写错误");
        }
        // 校验部门
        if (admin.getDeptId() == null) {
            throw new BadRequestException("操作失败，请选择部门");
        }
        boolean exists = departmentMapper.existsWithPrimaryKey(admin.getDeptId());
        if (!exists) {
            throw new BadRequestException("操作失败，部门不存在或已被删除");
        }
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BadRequestException("操作失败，请选择角色");
        }
    }

    /**
     * 保存管理员-角色映射关系
     *
     * @param adminId 管理员ID
     * @param roleIds 角色ID集合
     */
    private void saveAdminRole(Integer adminId, Set<Integer> roleIds) {
        // 1-删除旧的管理员-角色映射
        AdminRole adminRole = new AdminRole();
        adminRole.setAdminId(adminId);
        adminRoleMapper.delete(adminRole);

        // 2-保存新的管理员-角色映射关系
        List<AdminRole> list = new ArrayList<>();
        for (Integer roleId : roleIds) {
            adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        }
        // 批量插入菜单权限映射
        adminRoleMapper.batchInsert(list);
    }

}
