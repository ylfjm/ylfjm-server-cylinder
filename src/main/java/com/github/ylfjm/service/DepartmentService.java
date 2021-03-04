package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.AdminMapper;
import com.github.ylfjm.mapper.AdminRoleMapper;
import com.github.ylfjm.mapper.DepartmentMapper;
import com.github.ylfjm.pojo.po.Admin;
import com.github.ylfjm.pojo.po.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 部门业务类
 *
 * @author YLFJM
 * @date 2018/11/13
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final AdminMapper adminMapper;
    private final AdminRoleMapper adminRoleMapper;

    /**
     * 新增部门
     *
     * @param department 部门信息
     */
    public void add(Department department) {
        if (!StringUtils.hasText(department.getName())) {
            throw new BadRequestException("操作失败，部门名称不能为空");
        }
        department.setId(null);
        department.setCreator(UserCache.getCurrentRealName());
        department.setCreateTime(new Date());
        int result = departmentMapper.insert(department);
        if (result < 1) {
            throw new YlfjmException("操作失败，新增部门发生错误");
        }
    }

    /**
     * 删除某个部门
     *
     * @param id 部门ID
     */
    public void delete(Integer id) {
        Department department = departmentMapper.selectByPrimaryKey(id);
        if (Objects.isNull(department)) {
            throw new BadRequestException("操作失败，部门不存在或已被删除");
        }
        Admin query = new Admin();
        query.setDeptId(id);
        int count = adminMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("操作失败，该部门下尚有用户存在");
        }
        departmentMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新某个部门信息
     *
     * @param department 部门信息
     */
    public void update(Department department) {
        if (!StringUtils.hasText(department.getName())) {
            throw new BadRequestException("操作失败，部门名称不能为空");
        }
        if (Objects.isNull(department.getId())) {
            throw new BadRequestException("操作失败，请选择部门");
        }
        Department record = departmentMapper.selectByPrimaryKey(department.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，部门不存在或已被删除");
        }
        // 校验用户名是否已被使用
        Example example = new Example(Department.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", department.getId());
        criteria.andEqualTo("name", department.getName());
        int count = departmentMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("操作失败，该部门已存在");
        }
        department.setUpdater(UserCache.getCurrentRealName());
        department.setUpdateTime(new Date());
        departmentMapper.updateByPrimaryKeySelective(department);
    }

    /**
     * 分页查询部门信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param name     部门名称
     */
    public PageVO<Department> page(Integer pageNum, Integer pageSize, String name) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Department> page = departmentMapper.page(name);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询部门列表，部门携带用户列表
     *
     * @param roleId 角色ID
     */
    public List<Department> listDepartment(Integer roleId) {
        List<Department> list = departmentMapper.list();
        if (roleId != null) {
            Set<Integer> adminIdList = adminRoleMapper.selectAdminIdsByRoleId(roleId);
            for (Department dept : list) {
                List<Admin> adminList = dept.getAdminList();
                if (!CollectionUtils.isEmpty(adminList)) {
                    for (Admin admin : adminList) {
                        admin.setHave(adminIdList.contains(admin.getId()));
                    }
                }
            }
        }
        return list;
    }
}
