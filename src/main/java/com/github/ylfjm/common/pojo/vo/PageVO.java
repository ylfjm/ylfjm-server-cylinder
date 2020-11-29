package com.github.ylfjm.common.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 分页类
 *
 * @author YLFJM
 * @date 2018/7/22
 */
@Slf4j
@Getter
@Setter
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = 4276234710689081681L;

    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<T> result;

    public PageVO() {
    }

    public PageVO(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public PageVO(Integer pageNum, com.github.pagehelper.Page<T> page) {
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.result = page.getResult();
        // 当前页码大于总页数的情况，针对mybatis配置了页码大于总页数返回最后一页数据的情况
        if (pageNum != null && pageNum > this.pages) {
            this.pageNum = pageNum;
            this.result = null;
        }
    }

}
