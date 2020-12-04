package com.github.ylfjm.pojo.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：职位
 *
 * @Author Zhang Bo
 * @Date 2020/12/4
 */
@Table(name = "position")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String code;

}
