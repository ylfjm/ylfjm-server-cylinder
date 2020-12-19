package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.mapper.DailyMapper;
import com.github.ylfjm.mapper.ProjectMapper;
import com.github.ylfjm.pojo.dto.DailyDTO;
import com.github.ylfjm.pojo.po.Daily;
import com.github.ylfjm.pojo.po.PostCode;
import com.github.ylfjm.pojo.po.Project;
import com.github.ylfjm.pojo.vo.DailyDateVO;
import com.github.ylfjm.pojo.vo.DailyProjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：日报
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DailyService {

    private final DailyMapper dailyMapper;
    private final ProjectMapper projectMapper;

    /**
     * 创建日报
     *
     * @param dailyDTO 日报数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(DailyDTO dailyDTO) {
        Date now = new Date();
        List<Daily> dailyList = dailyDTO.getDailyList();
        String currentPostCode = dailyDTO.getCurrentPostCode();
        for (Daily daily : dailyList) {
            if (!StringUtils.hasText(daily.getContent())) {
                throw new BadRequestException("操作失败，日报内容不能为空");
            }
            Project project = projectMapper.selectByPrimaryKey(daily.getProjectId());
            if (project == null) {
                throw new BadRequestException("操作失败，项目不存在或已被删除");
            }
            daily.setProjectName(project.getName());
            daily.setPostCode(currentPostCode);
            daily.setPostName(PostCode.findByCode(currentPostCode).getName());
            daily.setCreateBy(UserCache.getCurrentUserName());
            daily.setCreateDate(now);
        }
        int result = dailyMapper.batchInsert(dailyList);
        if (result < 1) {
            throw new YlfjmException("操作失败，创建日报发生错误");
        }
    }

    /**
     * 查询日报列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public List<DailyDateVO> page(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<DailyDateVO> datePage = dailyMapper.selectCreateDatePage();
        List<DailyDateVO> result = datePage.getResult();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        List<DailyDTO> dailyList = dailyMapper.selectDaily(result);
        for (DailyDateVO vo : result) {
            String date = vo.getDate();
            List<DailyProjectVO> projectList = new ArrayList<>();
            for (DailyDTO dto : dailyList) {
                if (Objects.equals(dto.getCreateDate(), date)) {
                    DailyProjectVO projectVO = new DailyProjectVO();
                    projectVO.setId(dto.getProjectId());
                    projectVO.setName(dto.getProjectName());
                }
            }
        }


        return result;
    }

}
