package com.github.ylfjm.pojo.dto;

import com.github.ylfjm.pojo.po.Daily;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 描述：日报
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@Getter
@Setter
public class DailyDTO {

    //创建日报时前端传参
    private List<Daily> dailyList;
    private String currentPostCode;

    //返回给前端展示用的数据
    private Integer id;
    private Integer projectId;
    private String projectName;
    private String postCode;
    private String createDate;
    private String content;

}
