package com.github.ylfjm.pojo.po;

import java.util.Arrays;
import java.util.Objects;

public enum PostCode {

    po("产品经理", "po"),
    ui("UI设计", "ui"),
    android("安卓开发", "android"),
    ios("苹果开发", "ios"),
    web("前端开发", "web"),
    dev("后端开发", "dev"),
    test("测试", "test"),
    pm("项目经理", "pm"),
    td("研发经理", "td"),
    others("其他", "others");

    private final String name;
    private final String code;

    PostCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static PostCode findByCode(String code) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.code, code)).findAny().orElse(null);
    }

}
