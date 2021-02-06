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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

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
        this.check(project);
        project.setId(null);
        project.setStatus("doing");
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
        Project update = new Project();
        update.setId(id);
        update.setDeleted(true);
        projectMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 修改项目
     *
     * @param project 项目
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Project project) {
        if (project.getId() == null) {
            throw new BadRequestException("操作失败，缺少项目ID");
        }
        Project record = projectMapper.selectByPrimaryKey(project.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，项目不存在或已被删除");
        }
        //校验
        this.check(project);
        Example example = new Example(Project.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", project.getId());
        criteria.andEqualTo("name", project.getName());
        criteria.andEqualTo("deleted", 0);
        int count = projectMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("操作失败，该项目名称已存在");
        }
        record.setName(project.getName());
        record.setBegin(project.getBegin());
        record.setEnd(project.getEnd());
        record.setDays(project.getDays());
        record.setType(project.getType());
        int result = projectMapper.updateByPrimaryKey(record);
        if (result < 1) {
            throw new YlfjmException("操作失败，修改项目发生错误");
        }
    }

    /**
     * 校验
     */
    private void check(Project project) {
        if (!StringUtils.hasText(project.getName())) {
            throw new BadRequestException("操作失败，项目名称不能为空");
        }
        if (project.getBegin() == null) {
            throw new BadRequestException("操作失败，项目项目开始日期不能为空");
        }
        if (project.getEnd() == null) {
            throw new BadRequestException("操作失败，项目项目截止日期不能为空");
        }
    }

    /**
     * 分页查询项目信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public PageVO<Project> page(String status, int pageNum, int pageSize) {
        Example example = new Example(Project.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        if (StringUtils.hasText(status)) {
            criteria.andEqualTo("status", status);
        }
        example.orderBy("createDate").desc();
        PageHelper.startPage(pageNum, pageSize);
        Page<Project> page = (Page<Project>) projectMapper.selectByExample(example);
        return new PageVO<>(pageNum, page);
    }

}
