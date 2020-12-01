package com.github.ylfjm.mapper;

import com.github.pagehelper.Page;
import com.github.ylfjm.pojo.po.Department;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YLFJM
 * @date 2018/10/30
 */
public interface DepartmentMapper extends Mapper<Department> {

    Page<Department> page(@Param("name") String name);

    List<Department> list();

}
