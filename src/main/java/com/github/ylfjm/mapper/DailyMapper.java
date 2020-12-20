package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.dto.DailyDTO;
import com.github.ylfjm.pojo.po.Daily;
import com.github.ylfjm.pojo.vo.DailyDateVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DailyMapper extends Mapper<Daily> {

    int batchInsert(@Param("list") List<Daily> list);

    Page<DailyDateVO> selectCreateDatePage();

    List<DailyDTO> selectDailyProject(@Param("list") List<DailyDateVO> list);

    List<DailyDTO> selectDailyContent(@Param("list") List<DailyDateVO> list);
}
