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
public class DailyProjectVO {

    private Integer id;//项目ID
    private String name;//项目名称
    private List<DailyContentVO> contentList;

}
