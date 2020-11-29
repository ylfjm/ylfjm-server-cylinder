package com.github.ylfjm.pojo.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 管理员-角色关联
 *
 * @author YLFJM
 * @date 2018/11/2
 */
@Table(name = "system_admin__role")
@Getter
@Setter
public class AdminRole {

    @Id
    private Integer adminId;
    @Id
    private Integer roleId;

}
