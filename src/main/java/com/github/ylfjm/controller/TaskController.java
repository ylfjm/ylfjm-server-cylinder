package com.github.ylfjm.controller;

import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.pojo.dto.TaskQueryDTO;
import com.github.ylfjm.pojo.po.Task;
import com.github.ylfjm.pojo.po.TaskActionType;
import com.github.ylfjm.pojo.po.TaskRemark;
import com.github.ylfjm.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：任务
 *
 * @Author YLFJM
 * @Date 2020/11/6
 */
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 创建任务
     *
     * @param task 任务
     */
    @PostMapping(value = "/task")
    public void add(@RequestBody Task task) {
        taskService.add(task);
    }

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    @DeleteMapping(value = "/task/{id}")
    public void delete(@PathVariable Integer id) {
        taskService.delete(id);
    }

    /**
     * 更新任务
     *
     * @param task 任务信息
     */
    @PutMapping(value = "/task")
    public void update(@RequestBody Task task) {
        taskService.update(task);
    }

    /**
     * 任务详情操作任务状态
     *
     * @param actionType 操作类型 {@link TaskActionType}
     * @param task       任务
     */
    @PutMapping(value = "/task/{actionType}/action")
    public void action(@PathVariable String actionType, @RequestBody Task task) {
        taskService.action(actionType, task);
    }

    /**
     * 查询任务
     *
     * @param id 任务ID
     */
    @GetMapping(value = "/task/{id}")
    public Task getById(@PathVariable Integer id) {
        return taskService.getById(id);
    }

    /**
     * 查询任务列表
     *
     * @param taskQueryDTO {@link TaskQueryDTO}
     * @param pageNum      第几页
     * @param pageSize     每页大小
     */
    @GetMapping(value = "/task/{pageNum}/{pageSize}")
    public PageVO<Task> page(TaskQueryDTO taskQueryDTO,
                             @PathVariable Integer pageNum,
                             @PathVariable Integer pageSize) {
        return taskService.page(taskQueryDTO, pageNum, pageSize);
    }

    /**
     * 查询任务备注列表
     *
     * @param taskId 任务ID
     */
    @GetMapping(value = "/task/{taskId}/remark")
    public List<TaskRemark> remarkList(@PathVariable Integer taskId) {
        return taskService.remarkList(taskId);
    }
}
