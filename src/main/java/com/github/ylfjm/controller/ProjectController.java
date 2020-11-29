package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.po.Project;
import com.github.ylfjm.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：项目
 *
 * @Author YLFJM
 * @Date 2020/11/6
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 新增项目
     *
     * @param project 项目
     */
    @PostMapping(value = "/project")
    public void add(@RequestBody Project project) {
        projectService.add(project);
    }

    /**
     * 删除项目
     *
     * @param id 项目ID
     */
    @DeleteMapping(value = "/project/{id}")
    public void remove(@PathVariable("id") Integer id) {
        projectService.delete(id);
    }

    /**
     * 查询项目列表
     *
     * @param status   项目状态：wait-未开始、doing-进行中、suspended-已挂起、closed-已关闭
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/project/{pageNum}/{pageSize}")
    public PageVO<Project> page(@RequestParam(required = false) String status,
                                @PathVariable Integer pageNum,
                                @PathVariable Integer pageSize) {
        return projectService.page(status, pageNum, pageSize);
    }
}
