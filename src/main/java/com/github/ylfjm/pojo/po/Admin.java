package com.github.ylfjm.pojo.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Table(name = "system_admin")
@Getter
@Setter
@ToString
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//管理员ID
    private String userName;//用户名
    private String password;//密码
    private String realName;//姓名
    private Integer sex;//性别：1-男，2-女
    private String phone;//电话
    private String email;//邮箱
    private Integer deptId;//部门ID
    private String remark;//备注
    private Boolean forbidden;//是否禁用：1-是，0-否
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间

    @Transient
    private String deptName;//部门名称
    @Transient
    private Set<Integer> roleIds;//角色ID集合
    @Transient
    public List<Menu> menus;//管理员拥有的菜单列表

}
