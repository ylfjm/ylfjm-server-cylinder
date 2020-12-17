package com.github.ylfjm.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：项目
 *
 * @Author YLFJM
 * @Date 2020/11/6
 */
@Table(name = "project")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //项目类型：short-短期项目、long-长期项目
    private String type;
    //项目名称
    private String name;
    //项目代号
    private String code;
    @DateTimeFormat(pattern = "yyyy-MM-dd")//入参格式化
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")//出参格式化
    //开始日期
    private Date begin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")//入参格式化
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")//出参格式化
    //截止日期
    private Date end;
    //可用工作日
    private Integer days;
    //项目状态：wait-未开始、doing-进行中、suspended-已挂起、closed-已关闭
    private String status;
    //被谁开启
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")//出参格式化
    //开启时间
    private Date createDate;
    //被谁关闭
    private String closedBy;
    //关闭时间
    private Date closedDate;
    //被谁取消
    private String canceledBy;
    //取消时间
    private Date canceledDate;
    //访问控制：open-默认、private-私有、custom-自定义
    private String acl;
    //分组白名单
    private String whitelist;
    //已删除(0-否,1-是)
    private Boolean deleted;

}
