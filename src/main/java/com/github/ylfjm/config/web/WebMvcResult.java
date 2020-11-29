package com.github.ylfjm.config.web;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：全局统一返回数据格式
 *
 * @author YLFJM
 * @Date 2020/10/21
 */
@Getter
@Setter
public class WebMvcResult<T> {

    private int code;
    private String message;
    private T data;

    public WebMvcResult() {
    }

    public WebMvcResult(T data) {
        this.data = data;
    }

    public WebMvcResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
