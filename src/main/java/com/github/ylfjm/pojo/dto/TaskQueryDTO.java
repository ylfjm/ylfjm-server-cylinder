package com.github.ylfjm.pojo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：任务查询参数封装实体类
 *
 * @Author Zhang Bo
 * @Date 2020/12/15
 */
@Getter
@Setter
public class TaskQueryDTO {

    private String searchType;
    private Integer projectId;

    private String idSortBy;
    private String priSortBy;
    private String statusSortBy;
    private String createBySortBy;
    private String createDateSortBy;
    private String deadlineSortBy;
    private String pdDesignerSortBy;
    private String pdEstimateDateSortBy;
    private String uiDesignerSortBy;
    private String uiEstimateDateSortBy;
    private String webDeveloperSortBy;
    private String webEstimateDateSortBy;
    private String androidDeveloperSortBy;
    private String androidEstimateDateSortBy;
    private String iosDeveloperSortBy;
    private String iosEstimateDateSortBy;
    private String serverDeveloperSortBy;
    private String serverEstimateDateSortBy;
    private String testerSortBy;
    private String testEstimateDateSortBy;

}
