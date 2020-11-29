package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.po.Department;
import com.github.ylfjm.service.DepartmentService;
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
 * 部门
 *
 * @author YLFJM
 * @date 2018/11/7
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 新增部门
     *
     * @param department 部门信息
     */
    @PostMapping(value = "/department")
    public void add(@RequestBody Department department) {
        departmentService.add(department);
    }

    /**
     * 删除某个部门
     *
     * @param id 部门ID
     */
    @DeleteMapping(value = "/department/{id}")
    public void remove(@PathVariable("id") Integer id) {
        departmentService.delete(id);
    }

    /**
     * 更新某个部门信息
     *
     * @param department 部门信息
     */
    @PutMapping(value = "/department")
    public void update(@RequestBody Department department) {
        departmentService.update(department);
    }

    /**
     * 查询部门列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param name     部门名称
     */
    @GetMapping(value = "/department/{pageNum}/{pageSize}")
    public PageVO<Department> page(@PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize,
                                   @RequestParam(value = "name", required = false) String name) {
        return departmentService.page(pageNum, pageSize, name);
    }

}
