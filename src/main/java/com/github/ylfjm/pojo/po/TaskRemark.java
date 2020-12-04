package com.github.ylfjm.pojo.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：任务日志
 *
 * @Author Zhang Bo
 * @Date 2020/11/15
 */
@Table(name = "task_remark")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TaskRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //任务ID
    private Integer taskId;
    //文本类型：1-普通文本；2-富文本
    private Integer textType;
    //普通文本
    private String text;
    //富文本
    private String richText;
    //由谁创建
    private String createBy;
    //创建日期
    private Date createDate;

}
