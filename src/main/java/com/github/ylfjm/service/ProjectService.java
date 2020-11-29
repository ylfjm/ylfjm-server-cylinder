package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.ProjectMapper;
import com.github.ylfjm.pojo.po.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 描述：TODO
 *
 * @Author YLFJM
 * @Date 2020/11/6
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;

    /**
     * 新增项目
     *
     * @param project 项目
     */
    public void add(Project project) {
        if (!StringUtils.hasText(project.getName())) {
            throw new BadRequestException("操作失败，项目名称不能为空");
        }
        if (!StringUtils.hasText(project.getCode())) {
            throw new BadRequestException("操作失败，项目代号不能为空");
        }
        if (project.getBegin() == null) {
            throw new BadRequestException("操作失败，项目项目开始日期不能为空");
        }
        if (project.getEnd() == null) {
            throw new BadRequestException("操作失败，项目项目截止日期不能为空");
        }
        project.setId(null);
        project.setStatus("wait");
        project.setDeleted(false);
        int result = projectMapper.insert(project);
        if (result < 1) {
            throw new YlfjmException("操作失败，新增项目发生错误");
        }
    }

    /**
     * 删除某项目
     *
     * @param id 项目ID
     */
    public void delete(Integer id) {
        Project project = projectMapper.selectByPrimaryKey(id);
        if (project == null) {
            throw new BadRequestException("操作失败，项目不存在或已被删除");
        }
        projectMapper.deleteByPrimaryKey(id);
    }

    /**
     * 分页查询项目信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public PageVO<Project> page(String status, int pageNum, int pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Project> page = projectMapper.selectPage(status);
        return new PageVO<>(pageNum, page);
    }

}
