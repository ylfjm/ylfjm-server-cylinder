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
import java.util.Set;

/**
 * 角色
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@Table(name = "role")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;//角色名称

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    @Transient
    private Set<Integer> adminIds;//用户ID集合
    @Transient
    private Set<Integer> menuIds;//菜单ID集合
    @Transient
    private Set<Integer> permissionIds;//权限ID集合
    @Transient
    private List<Admin> adminList;

}
