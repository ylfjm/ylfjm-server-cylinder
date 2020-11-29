package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Project;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ProjectMapper extends Mapper<Project> {
    Page<Project> selectPage(@Param("status") String status);
}
