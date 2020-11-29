package com.github.ylfjm.pojo.po;

import lombok.Getter;
import lombok.Setter;

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
    //开始日期
    private Date begin;
    //截止日期
    private Date end;
    //可用工作日
    private Integer days;
    //项目状态：wait-未开始、doing-进行中、suspended-已挂起、closed-已关闭
    private String status;
    //被谁开启
    private String openedBy;
    //开启时间
    private Date openedDate;
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
