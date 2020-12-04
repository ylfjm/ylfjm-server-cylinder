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
 * 管理员
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@Table(name = "admin")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//管理员ID
    private String userName;//用户名
    private String password;//密码
    private String realName;//姓名
    private Integer deptId;//部门ID
    private String postCode;//职位CODE
    private String remark;//备注
    private Boolean forbidden;//是否禁用：1-是，0-否

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间

    @Transient
    private String deptName;//部门名称
    @Transient
    private Boolean have;//标识是否是某角色
    @Transient
    private Set<Integer> roleIds;//角色ID集合
    @Transient
    public List<Menu> menus;//管理员拥有的菜单列表

}
