package com.github.ylfjm.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@Getter
@Setter
public class DailyDateVO {

    private String date;//日报的创建时间YYYY-MM-DD
    private List<DailyProjectVO> projectList;
}
