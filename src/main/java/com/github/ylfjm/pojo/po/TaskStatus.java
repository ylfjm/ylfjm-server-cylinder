package com.github.ylfjm.pojo.po;

import java.util.Objects;

/**
 * 描述：开发任务实体类
 *
 * @Author Zhang Bo
 * @Date 2020/11/15
 */
public enum TaskStatus {

    wait, doing, done, pause, cancel, closed;

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
