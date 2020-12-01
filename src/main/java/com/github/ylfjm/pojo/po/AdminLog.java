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
 * 管理员登录日志
 *
 * @author YLFJM
 * @date 2018/10/30
 */
@Table(name = "system_admin_log")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer adminId;
    private String userName;
    private String realName;
    private String ip;//登录ip
    private Boolean success;//0-登录失败；1-登录成功
    private String cause;//登录失败原因
    private Date time;

}
