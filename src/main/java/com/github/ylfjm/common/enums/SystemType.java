package com.github.ylfjm.common.enums;

public enum SystemType {

    SYSTEM("业务管理系统", 2),
    H5("H5", 5);

    private final String name;
    private final int value;

    SystemType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
