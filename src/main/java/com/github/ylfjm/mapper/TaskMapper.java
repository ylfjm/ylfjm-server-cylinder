package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.dto.TaskQueryDTO;
import com.github.ylfjm.pojo.po.Task;
import tk.mybatis.mapper.common.Mapper;

public interface TaskMapper extends Mapper<Task> {
    Page<Task> selectPage(TaskQueryDTO taskQueryDTO);
}
