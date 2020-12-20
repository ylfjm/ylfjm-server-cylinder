package com.github.ylfjm.pojo.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：日报项目
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@Table(name = "daily_project")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DailyProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer sorts;
    //已删除(0-否,1-是)
    private Boolean deleted;

}
