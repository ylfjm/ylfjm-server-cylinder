package com.github.ylfjm.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：日报
 *
 * @Author Zhang Bo
 * @Date 2020/12/19
 */
@Table(name = "daily")
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Daily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer projectId;
    private String projectName;
    private String postCode;
    private String postName;

    private String content;

    private String createBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd")//入参格式化
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")//出参格式化
    private Date createDate;

}
