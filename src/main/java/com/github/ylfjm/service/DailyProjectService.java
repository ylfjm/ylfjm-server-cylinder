package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.DailyProjectMapper;
import com.github.ylfjm.pojo.po.DailyProject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Objects;

/**
 * 描述：日报项目
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DailyProjectService {

    private final DailyProjectMapper dailyProjectMapper;

    /**
     * 创建日报项目
     *
     * @param dailyProject 日报项目
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(DailyProject dailyProject) {
        if (!StringUtils.hasText(dailyProject.getName())) {
            throw new BadRequestException("操作失败，名称不能为空");
        }
        if (dailyProject.getSorts() == null) {
            throw new BadRequestException("操作失败，排序不能为空");
        }
        dailyProject.setId(null);
        dailyProject.setDeleted(false);
        int result = dailyProjectMapper.insert(dailyProject);
        if (result < 1) {
            throw new YlfjmException("操作失败，新增日报项目发生错误");
        }
    }

    /**
     * 删除某个日报项目
     *
     * @param id 日报项目ID
     */
    public void delete(Integer id) {
        DailyProject dailyProject = dailyProjectMapper.selectByPrimaryKey(id);
        if (Objects.isNull(dailyProject)) {
            throw new BadRequestException("操作失败，日报项目不存在或已被删除");
        }
        dailyProject.setDeleted(true);
        dailyProjectMapper.updateByPrimaryKey(dailyProject);
    }

    /**
     * 更新日报项目
     *
     * @param dailyProject 日报项目
     */
    public void update(DailyProject dailyProject) {
        if (!StringUtils.hasText(dailyProject.getName())) {
            throw new BadRequestException("操作失败，名称不能为空");
        }
        if (Objects.isNull(dailyProject.getId())) {
            throw new BadRequestException("操作失败，请选择日报项目");
        }
        DailyProject record = dailyProjectMapper.selectByPrimaryKey(dailyProject.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，日报项目不存在或已被删除");
        }
        Example example = new Example(DailyProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", dailyProject.getId());
        criteria.andEqualTo("name", dailyProject.getName());
        int count = dailyProjectMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("操作失败，该日报项目已存在");
        }
        dailyProjectMapper.updateByPrimaryKeySelective(dailyProject);
    }

    /**
     * 查询日报项目列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public PageVO<DailyProject> page(int pageNum, int pageSize) {
        Example example = new Example(DailyProject.class);
        example.orderBy("sorts").desc();
        PageHelper.startPage(pageNum, pageSize);
        Page<DailyProject> page = (Page<DailyProject>) dailyProjectMapper.selectByExample(example);
        return new PageVO<>(pageNum, page);
    }

}
