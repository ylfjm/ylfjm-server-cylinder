package com.github.ylfjm.common;

public class NoLoginException extends YlfjmException {

    public NoLoginException() {
        super("请先登录");
    }

}
