package com.github.ylfjm.pojo.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 部门
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@Table(name = "department")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//部门名称

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    @Transient
    private List<Admin> adminList;

}
