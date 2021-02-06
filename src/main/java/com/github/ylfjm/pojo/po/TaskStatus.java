package com.github.ylfjm.pojo.po;

import java.util.Objects;

/**
 * 描述：任务状态
 *
 * @Author Zhang Bo
 * @Date 2020/11/15
 */
public enum TaskStatus {

    doing, devDone, done, cancel, closed;

    public static boolean contains(String status) {
        boolean result = false;
        TaskStatus[] values = TaskStatus.values();
        for (TaskStatus ts : values) {
            if (Objects.equals(ts.name(), status)) {
                result = true;
            }
        }
        return result;
    }

}
