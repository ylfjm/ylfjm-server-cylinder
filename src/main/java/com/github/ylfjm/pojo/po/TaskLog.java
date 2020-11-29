package com.github.ylfjm.pojo.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：开发任务日志
 *
 * @Author Zhang Bo
 * @Date 2020/11/15
 */
@Table(name = "task_log")
@Getter
@Setter
public class TaskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //任务描述
    private String content;
    //由谁创建
    private String createBy;
    //创建日期
    private Date createDate;

}
