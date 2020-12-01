package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.NotFoundException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.MenuMapper;
import com.github.ylfjm.mapper.PermissionMapper;
import com.github.ylfjm.mapper.RoleMenuMapper;
import com.github.ylfjm.mapper.RolePermissionMapper;
import com.github.ylfjm.pojo.po.Menu;
import com.github.ylfjm.pojo.po.Permission;
import com.github.ylfjm.pojo.po.RoleMenu;
import com.github.ylfjm.pojo.po.RolePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 菜单业务类
 *
 * @author YLFJM
 * @date 2018/12/21
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;
    private final PermissionMapper permissionMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RolePermissionMapper rolePermissionMapper;

    /**
     * 添加菜单
     *
     * @param menu 菜单信息
     */
    public void add(Menu menu) {
        if (!StringUtils.hasText(menu.getName())) {
            throw new BadRequestException("操作失败，菜单名不能为空");
        }
        if (!StringUtils.hasText(menu.getUrl())) {
            throw new BadRequestException("操作失败，菜单地址不能为空");
        }
        if (menu.getPid() == null || menu.getPid() == 0) {
            menu.setPid(0);
            menu.setLevel(1);//一级菜单
        } else {
            Menu parent = menuMapper.selectByPrimaryKey(menu.getPid());
            if (Objects.isNull(parent)) {
                throw new BadRequestException("操作失败，父级菜单不存在或已被删除");
            }
            if (parent.getLevel() > 1) {
                throw new BadRequestException("操作失败，只支持创建一级或二级菜单");
            }
            // 设置菜单层级
            menu.setLevel(parent.getLevel() + 1);
        }
        Menu query = new Menu();
        query.setName(menu.getName());
        // 同一个父级菜单下的子菜单名称不能相同，不同父级菜单下的子菜单可以同名
        query.setPid(menu.getPid());
        int count = menuMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("操作失败，同一个父级菜单下子菜单名不能重复");
        }
        query.setName(null);
        query.setPid(null);
        query.setUrl(menu.getUrl());
        count = menuMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("操作失败，菜单URL已存在");
        }
        menu.setCreator(UserCache.getCurrentAdminRealName());
        menu.setCreateTime(new Date());
        menu.setId(null);
        int result = menuMapper.insert(menu);
        if (result < 1) {
            throw new YlfjmException("操作失败，添加菜单发生错误");
        }
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        Menu menu = menuMapper.selectByPrimaryKey(id);
        if (menu == null) {
            throw new NotFoundException("操作失败，菜单不存在或已被删除");
        }
        int result = menuMapper.deleteByPrimaryKey(id);
        if (result < 1) {
            throw new YlfjmException("删除菜单失败");
        }
        // 删除菜单角色关联数据
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setMenuId(id);
        roleMenuMapper.delete(roleMenu);
        // 删除菜单对应的权限，以及权限角色关联数据
        List<Integer> idList = permissionMapper.selectIdByMenuId(id);
        if (!CollectionUtils.isEmpty(idList)) {
            Example example = new Example(Permission.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", idList);
            permissionMapper.deleteByExample(example);

            Example example2 = new Example(RolePermission.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andIn("permissionId", idList);
            rolePermissionMapper.deleteByExample(example2);
        }
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     */
    public void update(Menu menu) {
        if (Objects.isNull(menu.getId())) {
            throw new BadRequestException("操作失败，请选择菜单");
        }
        if (!StringUtils.hasText(menu.getName())) {
            throw new BadRequestException("操作失败，菜单名不能为空");
        }
        if (!StringUtils.hasText(menu.getUrl())) {
            throw new BadRequestException("操作失败，菜单地址不能为空");
        }
        Menu record = menuMapper.selectByPrimaryKey(menu.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，菜单不存在或已被删除");
        }
        if (menu.getPid() == null || menu.getPid() == 0) {
            menu.setPid(0);
            menu.setLevel(1);//一级菜单
        } else {
            Menu parent = menuMapper.selectByPrimaryKey(menu.getPid());
            if (Objects.isNull(parent)) {
                throw new YlfjmException("操作失败，父级菜单不存在或已被删除");
            }
            if (parent.getLevel() > 1) {
                throw new BadRequestException("操作失败，只支持创建一级或二级菜单");
            }
            // 设置菜单层级
            menu.setLevel(parent.getLevel() + 1);
        }
        // 校验菜单名称是否已经存在
        Example example = new Example(Menu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", menu.getId());
        // 同一个父级菜单下的子菜单名称不能相同，不同父级菜单下的子菜单可以同名
        criteria.andEqualTo("name", menu.getName());
        criteria.andEqualTo("pid", menu.getPid());
        int count = menuMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("操作失败，同一个父级菜单下子菜单名不能重复");
        }
        // 校验菜单URL是否已经存在
        Example example2 = new Example(Menu.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andNotEqualTo("id", menu.getId());
        criteria2.andEqualTo("url", menu.getUrl());
        count = menuMapper.selectCountByExample(example2);
        if (count > 0) {
            throw new BadRequestException("操作失败，菜单URL已存在");
        }
        menu.setUpdater(UserCache.getCurrentAdminRealName());
        menu.setUpdateTime(new Date());
        menuMapper.updateByPrimaryKeySelective(menu);
    }

    /**
     * 分页查询菜单信息
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public PageVO<Menu> page(Integer pageNum, Integer pageSize, Integer pid, String name, Integer level) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Menu> page = menuMapper.page(pid, name, level);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 获取级联菜单结构
     */
    public List<Menu> listMenuTree(Integer roleId) {
        List<Menu> allMenus = menuMapper.selectAllMenus();
        if (roleId != null) {
            // 当角色ID不为空时，设置该角色是否拥有该菜单访问权限
            Set<Integer> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
            if (!CollectionUtils.isEmpty(menuIds)) {
                for (Menu menu : allMenus) {
                    menu.setHave(menuIds.contains(menu.getId()));
                }
            }
        }
        // 获取该角色下所有菜单
        List<Menu> firstLevelMenus = this.getFirstLevelMenuList(allMenus);
        // 为一级菜单设置子菜单准备递归
        for (Menu menu : firstLevelMenus) {
            // 获取父菜单下所有子菜单调用getChildList
            List<Menu> childList = this.getChildList(menu.getId(), allMenus);
            menu.setSubMenus(childList);
        }
        return firstLevelMenus;
    }

    /**
     * 递归组合子菜单逻辑
     *
     * @param allMenus 所有菜单集合
     */
    private List<Menu> getFirstLevelMenuList(List<Menu> allMenus) {
        // 构建子菜单
        List<Menu> firstLevelMenus = new ArrayList<>();
        // 遍历传入的list
        ListIterator<Menu> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            Menu menu = listIterator.next();
            // 所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (menu.getLevel() == 1 && (menu.getPid() == 0 || menu.getPid() == null)) {
                firstLevelMenus.add(menu);
                listIterator.remove();
            }
        }
        return firstLevelMenus;
    }

    /**
     * 递归组合子菜单逻辑
     *
     * @param pid      父级菜单ID
     * @param allMenus 所有菜单集合
     */
    public List<Menu> getChildList(Integer pid, List<Menu> allMenus) {
        // 构建子菜单
        List<Menu> subMenus = new ArrayList<>();
        // 遍历传入的list
        ListIterator<Menu> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            Menu menu = listIterator.next();
            // 所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (Objects.equals(menu.getPid(), pid)) {
                subMenus.add(menu);
                listIterator.remove();
            }
        }
        // 递归
        for (Menu menu : subMenus) {
            menu.setSubMenus(this.getChildList(menu.getId(), allMenus));
        }
        if (subMenus.size() == 0) {
            return null;
        }
        return subMenus;
    }

    /**
     * 获取所有子菜单ID（包含自己）
     *
     * @param menuId 父级菜单ID
     */
    public List<Integer> getChildMenuIds(Integer menuId) {
        List<Menu> allMenus = menuMapper.selectAllMenus();
        // 构建子菜单
        List<Integer> subMenus = new ArrayList<>();
        subMenus.add(menuId);
        this.getChildList(menuId, allMenus, subMenus);
        return subMenus;
    }

    /**
     * 递归获取子菜单逻辑
     *
     * @param pid      父菜单ID
     * @param allMenus 所有菜单
     * @param subMenus 子菜单集合
     */
    private void getChildList(Integer pid, List<Menu> allMenus, List<Integer> subMenus) {
        List<Integer> tempList = new ArrayList<>();
        // 遍历传入的list
        ListIterator<Menu> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            Menu menu = listIterator.next();
            // 所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (Objects.equals(menu.getPid(), pid)) {
                tempList.add(menu.getId());
                listIterator.remove();
            }
        }
        // 递归
        for (Integer id : tempList) {
            this.getChildList(id, allMenus, subMenus);
        }
        subMenus.addAll(tempList);
    }

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @param roleId 角色ID
     */
    public List<Menu> listMenuAndPermission(Integer roleId) {
        // 获取所有菜单列表（每个菜单中携带对应的权限列表）
        List<Menu> menus = menuMapper.selectMenuWithPermission();
        // 获取一共有几级菜单
        Integer maxLevel = menuMapper.selectMaxMenuLevel();

        // 获取该角色对应的所有权限
        Set<Integer> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);

        if (!CollectionUtils.isEmpty(menus)) {
            Map<Integer, List<Menu>> map = new HashMap<>();
            // 1-将菜单分级存放
            for (int i = 1; i <= maxLevel; i++) {
                map.put(i, new ArrayList<>());
                for (Menu menu : menus) {
                    menu.setHave(false);
                    if (menu.getLevel() == i) {
                        map.get(i).add(menu);
                    }
                    // 菜单的权限列表不为空 && 角色拥有的权限列表不为空
                    if (!CollectionUtils.isEmpty(menu.getPermissions()) && !CollectionUtils.isEmpty(permissionIds)) {
                        for (Permission permission : menu.getPermissions()) {
                            permission.setHave(false);
                            // 判断菜单的权限是否被包含在角色拥有的权限列表中，如果被包含，则设置个标识（前端展示用到）
                            if (permissionIds.contains(permission.getId())) {
                                // 表示该角色拥有该权限
                                permission.setHave(true);
                                // 表示该角色拥有该菜单
                                menu.setHave(true);
                            }
                        }
                    }
                }
            }
            // 2-将子菜单设置到对应的上级菜单下
            for (int i = maxLevel; i > 1; i--) {
                List<Menu> outers = map.get(i - 1);
                List<Menu> inners = map.get(i);
                for (Menu outer : outers) {
                    List<Menu> subMenus = new ArrayList<>();
                    for (Menu inner : inners) {
                        if (Objects.equals(inner.getPid(), outer.getId())) {
                            subMenus.add(inner);
                        }
                        // 如果某个子菜单被该角色拥有，则上级菜单也应该被角色拥有
                        if (inner.getHave() != null && inner.getHave()) {
                            outer.setHave(inner.getHave());
                        }
                    }
                    outer.setSubMenus(subMenus);
                }
            }
            return map.get(1);
        }
        return null;
    }

}
