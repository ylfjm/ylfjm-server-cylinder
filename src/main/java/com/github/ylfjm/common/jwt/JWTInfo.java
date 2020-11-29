package com.github.ylfjm.common.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class JWTInfo implements Serializable {

    private static final long serialVersionUID = -5277712364271453933L;
    private Integer id;
    private String account;
    private String realName;
    private Integer type;

    public JWTInfo() {
    }

}
