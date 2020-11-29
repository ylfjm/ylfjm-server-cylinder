package com.github.ylfjm.pojo.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 部门
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@Table(name = "system_department")
@Getter
@Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//部门名称
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    @Transient
    private Integer accountCount;//所属该部门的管理员账号数量

}
