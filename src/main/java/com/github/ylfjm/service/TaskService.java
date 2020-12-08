package com.github.ylfjm.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.ylfjm.common.BadRequestException;
import com.github.ylfjm.common.NotFoundException;
import com.github.ylfjm.common.YlfjmException;
import com.github.ylfjm.common.cache.UserCache;
import com.github.ylfjm.common.pojo.vo.PageVO;
import com.github.ylfjm.common.utils.DateUtil;
import com.github.ylfjm.mapper.ProjectMapper;
import com.github.ylfjm.mapper.TaskMapper;
import com.github.ylfjm.mapper.TaskRemarkMapper;
import com.github.ylfjm.pojo.po.Project;
import com.github.ylfjm.pojo.po.Task;
import com.github.ylfjm.pojo.po.TaskActionType;
import com.github.ylfjm.pojo.po.TaskRemark;
import com.github.ylfjm.pojo.po.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：任务
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
    private final ProjectMapper projectMapper;

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
        task.setStatus(TaskStatus.doing.name());
        task.setDeleted(false);
        task.setOpenedBy(UserCache.getCurrentUserName());
        task.setOpenedDate(now);
        int result = taskMapper.insertSelective(task);
        if (result < 1) {
            throw new YlfjmException("操作失败，创建任务发生错误");
        }
        this.addTaskRemark(task.getId(), "创建", null, now);
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
        this.addTaskRemark(id, "删除", null, now);
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
        String richText = this.buildTextForUpdate(task, record, new StringBuffer());
        this.copyBasic(task, record);
        this.copyDeveloper(task, record);
        this.setRequired(record);
        this.setLastEdited(record, now);
        int result = taskMapper.updateByPrimaryKey(record);
        if (result < 1) {
            throw new YlfjmException("操作失败，修改任务发生错误");
        }
        String text = "编辑";
        if (StringUtils.hasText(richText)) {
            this.addTaskRemark(task.getId(), text, richText, now);
        }
    }

    /**
     * 任务详情操作更新任务状态
     *
     * @param actionType 操作类型 {@link TaskActionType}
     * @param task    任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String actionType, Task task) {
        Date now = new Date();
        String text = null;
        String richText = null;
        Task record = taskMapper.selectByPrimaryKey(task.getId());
        if (record == null) {
            throw new NotFoundException("操作失败，任务不存在或已被删除");
        }
        if (Objects.equals(actionType, TaskActionType.assign.name())) {
            text = "指派";
            richText = this.buildTextForAssign(task, record, new StringBuffer());
            this.copyDeveloper(task, record);
            this.setRequired(record);
        } else if (Objects.equals(actionType, TaskActionType.estimate.name())) {
            text = "排期";
            task.setFinishedDate(null);
            this.doEstimateOrComplete(task, record);
        } else if (Objects.equals(actionType, TaskActionType.complete.name())) {
            text = "完成";
            task.setEstimateDate(null);
            this.doEstimateOrComplete(task, record);
            this.allCompleteHandler(record);
        } else if (Objects.equals(actionType, TaskActionType.activate.name())) {
            text = "激活";
            record.setStatus(TaskStatus.doing.name());
        } else if (Objects.equals(actionType, TaskActionType.cancel.name())) {
            text = "取消";
            record.setStatus(TaskStatus.cancel.name());
            record.setCanceledBy(UserCache.getCurrentUserName());
            record.setCanceledDate(now);
        } else if (Objects.equals(actionType, TaskActionType.close.name())) {
            text = "关闭";
            record.setStatus(TaskStatus.closed.name());
            record.setClosedBy(UserCache.getCurrentUserName());
            record.setClosedDate(now);
        } else {
            throw new NotFoundException("操作失败，未定义的操作");
        }
        this.setLastEdited(record, now);
        int result = taskMapper.updateByPrimaryKey(record);
        if (result < 1) {
            throw new YlfjmException("操作失败，更新任务状态发生错误");
        }
        if (StringUtils.hasText(task.getRemark())) {
            richText = (StringUtils.hasText(richText) ? richText : "") + task.getRemark();
        }
        if (StringUtils.hasText(richText)) {
            this.addTaskRemark(task.getId(), text, richText, now);
        }
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
     * @param status   任务状态：doing-进行中、done-已完成、cancel-已取消、closed-已关闭
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
     * 复制基本信息
     *
     * @param fromTask 源
     * @param toTask   目标
     */
    private void copyBasic(Task fromTask, Task toTask) {
        toTask.setProjectId(fromTask.getProjectId());
        toTask.setType(fromTask.getType());
        toTask.setPri(fromTask.getPri());
        toTask.setDeadline(fromTask.getDeadline());
        toTask.setName(fromTask.getName());
        toTask.setRichText(fromTask.getRichText());
    }

    /**
     * 复制开发者信息
     *
     * @param fromTask 源
     * @param toTask   目标
     */
    private void copyDeveloper(Task fromTask, Task toTask) {
        toTask.setPdDesigner(fromTask.getPdDesigner());
        toTask.setUiDesigner(fromTask.getUiDesigner());
        toTask.setWebDeveloper(fromTask.getWebDeveloper());
        toTask.setAndroidDeveloper(fromTask.getAndroidDeveloper());
        toTask.setIosDeveloper(fromTask.getIosDeveloper());
        toTask.setServerDeveloper(fromTask.getServerDeveloper());
        toTask.setTester(fromTask.getTester());
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
        task.setLastEditedBy(UserCache.getCurrentUserName());
        task.setLastEditedDate(now);
    }

    /**
     * 组装任务操作日志/备注内容
     *
     * @param fromTask 源
     * @param toTask   目标
     */
    private String buildTextForUpdate(Task fromTask, Task toTask, StringBuffer sb) {
        if (!Objects.equals(fromTask.getName(), toTask.getName())) {
            sb.append("修改了 <strong><em>任务名称</em></strong>，旧值为\"").append(fromTask.getName())
                    .append("\"，新值为\"").append(toTask.getName()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getProjectId(), toTask.getProjectId())) {
            Project fromProject = projectMapper.selectByPrimaryKey(fromTask.getProjectId());
            Project toProject = projectMapper.selectByPrimaryKey(toTask.getProjectId());
            if (fromProject != null && toProject != null) {
                sb.append("修改了 <strong><em>所属项目</em></strong>，旧值为\"").append(fromProject.getName())
                        .append("\"，新值为\"").append(toProject.getName()).append("\"。<br/>");
            }
        }
        if (!Objects.equals(fromTask.getPri(), toTask.getPri())) {
            sb.append("修改了 <strong><em>任务类型</em></strong>，旧值为\"").append(fromTask.getPri())
                    .append("\"，新值为\"").append(toTask.getPri()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getDeadline(), toTask.getDeadline())) {
            sb.append("修改了 <strong><em>截止日期</em></strong>，旧值为\"").append(DateUtil.dateToString2(fromTask.getDeadline()))
                    .append("\"，新值为\"").append(DateUtil.dateToString2(toTask.getDeadline())).append("\"。<br/>");
        }
        sb.append(this.buildTextForAssign(fromTask, toTask, new StringBuffer()));
        return sb.toString();
    }

    /**
     * 组装任务操作日志/备注内容
     *
     * @param fromTask 源
     * @param toTask   目标
     */
    private String buildTextForAssign(Task fromTask, Task toTask, StringBuffer sb) {
        if (!Objects.equals(fromTask.getPdDesigner(), toTask.getPdDesigner())) {
            sb.append("修改了 <strong><em>产品设计</em></strong>，旧值为\"").append(fromTask.getPdDesigner())
                    .append("\"，新值为\"").append(toTask.getPdDesigner()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getUiDesigner(), toTask.getUiDesigner())) {
            sb.append("修改了 <strong><em>UI设计</em></strong>，旧值为\"").append(fromTask.getUiDesigner())
                    .append("\"，新值为\"").append(toTask.getUiDesigner()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getAndroidDeveloper(), toTask.getAndroidDeveloper())) {
            sb.append("修改了 <strong><em>安卓开发</em></strong>，旧值为\"").append(fromTask.getAndroidDeveloper())
                    .append("\"，新值为\"").append(toTask.getAndroidDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getIosDeveloper(), toTask.getIosDeveloper())) {
            sb.append("修改了 <strong><em>苹果开发</em></strong>，旧值为\"").append(fromTask.getIosDeveloper())
                    .append("\"，新值为\"").append(toTask.getIosDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getWebDeveloper(), toTask.getWebDeveloper())) {
            sb.append("修改了 <strong><em>前端开发</em></strong>，旧值为\"").append(fromTask.getWebDeveloper())
                    .append("\"，新值为\"").append(toTask.getWebDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getServerDeveloper(), toTask.getServerDeveloper())) {
            sb.append("修改了 <strong><em>后端开发</em></strong>，旧值为\"").append(fromTask.getServerDeveloper())
                    .append("\"，新值为\"").append(toTask.getServerDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(fromTask.getTester(), toTask.getTester())) {
            sb.append("修改了 <strong><em>测试人员</em></strong>，旧值为\"").append(fromTask.getTester())
                    .append("\"，新值为\"").append(toTask.getTester()).append("\"。<br/>");
        }
        return sb.toString();
    }

    /**
     * 排期/完成
     *
     * @param task   前端传参
     * @param record 数据库的
     */
    private void doEstimateOrComplete(Task task, Task record) {
        String currentPostCode = UserCache.getCurrentPostCode();
        String errorMessage = "操作失败，该任务没有指派给你";
        if (Objects.equals(currentPostCode, "po")) {
            if (record.getPdRequired() && this.checkContains(record.getPdDesigner())) {
                if (task.getEstimateDate() != null) {
                    record.setPdEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setPdFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (Objects.equals(currentPostCode, "ui")) {
            if (record.getUiRequired() && this.checkContains(record.getUiDesigner())) {
                if (task.getEstimateDate() != null) {
                    record.setUiEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setUiFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (Objects.equals(currentPostCode, "android")) {
            if (record.getAndroidRequired() && this.checkContains(record.getAndroidDeveloper())) {
                if (task.getEstimateDate() != null) {
                    record.setAndroidEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setAndroidFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (Objects.equals(currentPostCode, "ios")) {
            if (record.getIosRequired() && this.checkContains(record.getIosDeveloper())) {
                if (task.getEstimateDate() != null) {
                    record.setIosEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setIosFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (Objects.equals(currentPostCode, "web")) {
            if (record.getWebRequired() && this.checkContains(record.getWebDeveloper())) {
                if (task.getEstimateDate() != null) {
                    record.setWebEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setWebFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (Objects.equals(currentPostCode, "dev")) {
            if (record.getServerRequired() && this.checkContains(record.getServerDeveloper())) {
                if (task.getEstimateDate() != null) {
                    record.setServerEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setServerFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        } else if (Objects.equals(currentPostCode, "test")) {
            if (record.getTestRequired() && this.checkContains(record.getTester())) {
                if (task.getEstimateDate() != null) {
                    record.setTestEstimateDate(task.getEstimateDate());
                }
                if (task.getFinishedDate() != null) {
                    record.setTestFinishedDate(task.getFinishedDate());
                }
            } else {
                throw new BadRequestException(errorMessage);
            }
        }
    }

    /**
     * 所有开发都完成与否
     *
     * @param record 数据库的
     */
    private void allCompleteHandler(Task record) {
        boolean allComplete = true;
        if (record.getPdRequired() && record.getPdFinishedDate() == null) {
            allComplete = false;
        }
        if (record.getUiRequired() && record.getUiFinishedDate() == null) {
            allComplete = false;
        }
        if (record.getAndroidRequired() && record.getAndroidFinishedDate() == null) {
            allComplete = false;
        }
        if (record.getIosRequired() && record.getIosFinishedDate() == null) {
            allComplete = false;
        }
        if (record.getWebRequired() && record.getWebFinishedDate() == null) {
            allComplete = false;
        }
        if (record.getServerRequired() && record.getServerFinishedDate() == null) {
            allComplete = false;
        }
        if (record.getTestRequired() && record.getTestFinishedDate() == null) {
            allComplete = false;
        }
        if (allComplete) {
            record.setStatus(TaskStatus.done.name());
        }
    }

    /**
     * 校验当前用户是否是该任务的开发者
     *
     * @param developer 任务指派的开发人员
     */
    private boolean checkContains(String developer) {
        String currentUserName = UserCache.getCurrentUserName();
        return Arrays.stream(developer.split(",")).anyMatch(d -> Objects.equals(d, currentUserName));
    }

    /**
     * 添加任务备注
     */
    private void addTaskRemark(Integer taskId, String text, String RichText, Date createdate) {
        TaskRemark taskRemark = new TaskRemark();
        taskRemark.setTaskId(taskId);
        taskRemark.setText(text);
        taskRemark.setRichText(RichText);
        taskRemark.setCreateBy(UserCache.getCurrentRealName());
        taskRemark.setCreateDate(createdate);
        taskRemarkMapper.insert(taskRemark);
    }

}
