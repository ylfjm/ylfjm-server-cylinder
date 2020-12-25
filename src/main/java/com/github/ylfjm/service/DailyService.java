package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.DailyMapper;
import com.github.ylfjm.mapper.DailyProjectMapper;
import com.github.ylfjm.pojo.dto.DailyDTO;
import com.github.ylfjm.pojo.po.Daily;
import com.github.ylfjm.pojo.po.DailyProject;
import com.github.ylfjm.pojo.vo.DailyContentVO;
import com.github.ylfjm.pojo.vo.DailyDateVO;
import com.github.ylfjm.pojo.vo.DailyProjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
    private final DailyProjectMapper dailyProjectMapper;

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
            DailyProject dp = dailyProjectMapper.selectByPrimaryKey(daily.getProjectId());
            if (dp == null) {
                throw new BadRequestException("操作失败，项目不存在或已被删除");
            }
            daily.setProjectName(dp.getName());
            daily.setPostCode(currentPostCode);
            daily.setCreateBy(UserCache.getCurrentUserName());
            daily.setCreateDate(now);
        }
        int result = dailyMapper.batchInsert(dailyList);
        if (result < 1) {
            throw new YlfjmException("操作失败，创建日报发生错误");
        }
    }

    /**
     * 删除日报
     *
     * @param id 日报ID
     */
    public void delete(Integer id) {
        Daily daily = dailyMapper.selectByPrimaryKey(id);
        if (daily == null) {
            throw new BadRequestException("操作失败，日报不存在或已被删除");
        }
        if (!Objects.equals(daily.getCreateBy(), UserCache.getCurrentUserName())) {
            throw new BadRequestException("操作失败，你不能删除别人的日报");
        }
        dailyMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询日报列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public PageVO<DailyDateVO> pageShow(int pageNum, int pageSize) {
        PageVO<DailyDateVO> pageVO = new PageVO<>(pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        Page<DailyDateVO> datePage = dailyMapper.selectCreateDatePage();
        List<DailyDateVO> result = datePage.getResult();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        pageVO.setPages(datePage.getPages());
        pageVO.setTotal(datePage.getTotal());
        pageVO.setResult(result);
        List<DailyDTO> projectList = dailyMapper.selectDailyProject(result);
        List<DailyDTO> contentList = dailyMapper.selectDailyContent(result);
        for (DailyDateVO dVO : result) {
            String date = dVO.getDate();
            //组装以日期对应的项目列表
            Iterator<DailyDTO> pIterator = projectList.iterator();
            List<DailyProjectVO> dpList = new ArrayList<>();
            while (pIterator.hasNext()) {
                DailyDTO next = pIterator.next();
                if (Objects.equals(next.getCreateDate(), date)) {
                    DailyProjectVO dpVO = new DailyProjectVO();
                    dpVO.setId(next.getProjectId());
                    dpVO.setName(next.getProjectName());
                    dpList.add(dpVO);
                    pIterator.remove();
                }
            }
            dVO.setProjectList(dpList);
            //组装以日期+项目对应的日报列表
            for (DailyProjectVO dpVO : dpList) {
                Integer projectId = dpVO.getId();
                Iterator<DailyDTO> cIterator = contentList.iterator();
                List<DailyContentVO> dcList = new ArrayList<>();
                while (cIterator.hasNext()) {
                    DailyDTO next = cIterator.next();
                    if (Objects.equals(next.getCreateDate(), date) && Objects.equals(next.getProjectId(), projectId)) {
                        DailyContentVO dcVO = new DailyContentVO();
                        dcVO.setId(next.getId());
                        dcVO.setContent(next.getContent());
                        dcVO.setPostCode(next.getPostCode());
                        dcList.add(dcVO);
                        cIterator.remove();
                    }
                }
                dpVO.setContentList(dcList);
            }
        }
        return pageVO;
    }

    /**
     * 查询日报列表
     *
     * @param pageNum    第几页
     * @param pageSize   每页大小
     * @param searchType 搜索分类：all-所有；me-我的
     */
    public PageVO<Daily> page(int pageNum, int pageSize, String searchType) {
        Example example = new Example(Daily.class);
        if (StringUtils.hasText(searchType) && Objects.equals("me", searchType)) {
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("createBy", UserCache.getCurrentUserName());
        }
        example.orderBy("createDate").desc();
        PageHelper.startPage(pageNum, pageSize);
        Page<Daily> page = (Page<Daily>) dailyMapper.selectByExample(example);
        return new PageVO<>(pageNum, page);
    }

}
