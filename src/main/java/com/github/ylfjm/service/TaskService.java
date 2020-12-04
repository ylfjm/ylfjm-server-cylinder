package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.NotFoundException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.mapper.TaskRemarkMapper;
import com.github.ylfjm.mapper.TaskMapper;
import com.github.ylfjm.pojo.po.Task;
import com.github.ylfjm.pojo.po.TaskRemark;
import com.github.ylfjm.pojo.po.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：TODO
 *
 * @Author YLFJM
 * @Date 2020/11/6
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRemarkMapper taskRemarkMapper;

    /**
     * 创建任务
     *
     * @param task 任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Task task) {
        Date now = new Date();
        //校验
        this.check(task);
        this.setRequired(task);
        this.setLastEdited(task, now);
        task.setId(null);
        task.setStatus("wait");
        task.setDeleted(false);
        task.setOpenedBy(UserCache.getAccount());
        task.setOpenedDate(now);
        int result = taskMapper.insertSelective(task);
        if (result < 1) {
            throw new YlfjmException("操作失败，创建任务发生错误");
        }
        this.addTaskRemark(task.getId(), 1, "创建", now);
    }

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        Date now = new Date();
        Task task = taskMapper.selectByPrimaryKey(id);
        if (task == null) {
            throw new NotFoundException("操作失败，任务不存在或已被删除");
        }
        Task update = new Task();
        update.setId(id);
        update.setDeleted(true);
        this.setLastEdited(update, now);
        int result = taskMapper.updateByPrimaryKeySelective(update);
        if (result < 1) {
            throw new YlfjmException("操作失败，删除任务发生错误");
        }
        this.addTaskRemark(id, 1, "删除", now);
    }

    /**
     * 修改任务
     *
     * @param task 任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Task task) {
        Date now = new Date();
        if (task.getId() == null) {
            throw new BadRequestException("操作失败，缺少任务ID");
        }
        Task record = taskMapper.selectByPrimaryKey(task.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，任务不存在或已被删除");
        }
        //校验
        this.check(task);
        this.setRequired(task);
        this.setLastEdited(task, now);
        task.setStatus(record.getStatus());
        task.setDeleted(record.getDeleted());
        int result = taskMapper.updateByPrimaryKey(task);
        if (result < 1) {
            throw new YlfjmException("操作失败，修改任务发生错误");
        }
        this.addTaskRemark(task.getId(), 1, "编辑", now);
    }

    /**
     * 更新任务状态
     *
     * @param id        任务ID
     * @param oldStatus 当前状态
     * @param newStatus 更新后状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer id, String oldStatus, String newStatus, String closedReason) {
        Date now = new Date();
        Task task = taskMapper.selectByPrimaryKey(id);
        if (task == null) {
            throw new NotFoundException("操作失败，任务不存在或已被删除");
        }
        if (!Objects.equals(task.getStatus(), oldStatus)) {
            throw new NotFoundException("操作失败，任务状态已更新，请重试");
        }
        if (!TaskStatus.contains(oldStatus) || !TaskStatus.contains(newStatus)) {
            throw new NotFoundException("操作失败，任务状态有误");
        }
        Task update = new Task();
        update.setId(id);
        update.setStatus(newStatus);
        if (Objects.equals(newStatus, TaskStatus.cancel.name())) {
            update.setCanceledBy(UserCache.getAccount());
            update.setCanceledDate(now);
        } else if (Objects.equals(newStatus, TaskStatus.closed.name())) {
            update.setClosedBy(UserCache.getAccount());
            update.setClosedDate(now);
            update.setClosedReason(closedReason);
        }
        this.setLastEdited(update, now);
        int result = taskMapper.updateByPrimaryKeySelective(update);
        if (result < 1) {
            throw new YlfjmException("操作失败，更新任务状态发生错误");
        }
        this.addTaskRemark(id, 2, "更新任务状态【由'" + oldStatus + "'更新为'" + newStatus + "'】", now);
    }

    /**
     * 指派
     *
     * @param task 任务信息
     */
    public void assign(Task task) {
        Date now = new Date();
        if (task.getId() == null) {
            throw new BadRequestException("操作失败，缺少任务ID");
        }
        Task record = taskMapper.selectByPrimaryKey(task.getId());
        if (record == null) {
            throw new BadRequestException("操作失败，任务不存在或已被删除");
        }
        record.setPdDesigner(task.getPdDesigner());
        record.setUiDesigner(task.getUiDesigner());
        record.setWebDeveloper(task.getWebDeveloper());
        record.setAndroidDeveloper(task.getAndroidDeveloper());
        record.setIosDeveloper(task.getIosDeveloper());
        record.setServerDeveloper(task.getServerDeveloper());
        record.setTester(task.getTester());
        this.setRequired(record);
        this.setLastEdited(record, now);
        int result = taskMapper.updateByPrimaryKey(record);
        if (result < 1) {
            throw new YlfjmException("操作失败，指派开发时发生错误");
        }
        this.addTaskRemark(task.getId(), 2, task.getRemark(), now);
    }

    /**
     * 查询单个任务
     *
     * @param id 任务ID
     */
    public Task getById(Integer id) {
        Task task = taskMapper.selectByPrimaryKey(id);
        if (task == null) {
            throw new BadRequestException("查询失败，任务不存在或已被删除");
        }
        return task;
    }

    /**
     * 分页查询任务信息，可带查询条件
     *
     * @param status   任务状态：wait-未开始、doing-进行中、done-已完成、pause-已暂停、cancel-已取消、closed-已关闭
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    public PageVO<Task> page(String status, int pageNum, int pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Task> page = taskMapper.selectPage(status);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询任务备注列表
     *
     * @param taskId 任务ID
     */
    public List<TaskRemark> remarkList(Integer taskId) {
        TaskRemark remark = new TaskRemark();
        remark.setTaskId(taskId);
        return taskRemarkMapper.select(remark);
    }

    /**
     * 校验
     */
    private void check(Task task) {
        if (task.getProjectId() == null) {
            throw new BadRequestException("操作失败，请选择任务所属项目");
        }
        if (!StringUtils.hasText(task.getName())) {
            throw new BadRequestException("操作失败，任务名称不能为空");
        }
        if (task.getPri() == null) {
            throw new BadRequestException("操作失败，请选择任务优先级");
        }
        if (!StringUtils.hasText(task.getType())) {
            throw new BadRequestException("操作失败，请选择任务类型");
        }
        if (task.getDeadline() == null) {
            throw new BadRequestException("操作失败，请选择任务截止日期");
        }
    }

    /**
     * 设置指派对象信息
     */
    private void setRequired(Task task) {
        task.setPdRequired(StringUtils.hasText(task.getPdDesigner()));
        task.setUiRequired(StringUtils.hasText(task.getUiDesigner()));
        task.setWebRequired(StringUtils.hasText(task.getWebDeveloper()));
        task.setAndroidRequired(StringUtils.hasText(task.getAndroidDeveloper()));
        task.setIosRequired(StringUtils.hasText(task.getIosDeveloper()));
        task.setServerRequired(StringUtils.hasText(task.getServerDeveloper()));
        task.setTestRequired(StringUtils.hasText(task.getTester()));
    }

    /**
     * 设置最后修改信息
     */
    private void setLastEdited(Task task, Date now) {
        task.setLastEditedBy(UserCache.getAccount());
        task.setLastEditedDate(now);
    }

    /**
     * 添加任务备注
     */
    private void addTaskRemark(Integer taskId, int textType, String text, Date createdate) {
        TaskRemark taskRemark = new TaskRemark();
        taskRemark.setTaskId(taskId);
        taskRemark.setTextType(textType);
        if (textType == 1) {
            taskRemark.setText(text);
        } else {
            taskRemark.setRichText(text);
        }
        taskRemark.setCreateBy(UserCache.getAccount());
        taskRemark.setCreateDate(createdate);
        taskRemarkMapper.insert(taskRemark);
    }

}
