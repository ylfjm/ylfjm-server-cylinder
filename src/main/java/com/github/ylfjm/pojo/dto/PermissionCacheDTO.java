package com.github.ylfjm.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 权限缓存
 *
 * @author YLFJM
 * @date 2018/11/2
 */
@Getter
@Setter
public class PermissionCacheDTO implements Serializable {

    private static final long serialVersionUID = -6299160203474077260L;

    private String code;
    private String method;

}
