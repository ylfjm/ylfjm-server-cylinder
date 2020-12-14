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
import com.github.ylfjm.pojo.po.TaskSearchType;
import com.github.ylfjm.pojo.po.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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
        this.emptyStringToNull(task);
        this.setRequired(task);
        this.setLastEdited(task, now);
        task.setId(null);
        task.setStatus(TaskStatus.doing.name());
        task.setDeleted(false);
        task.setCreateBy(UserCache.getCurrentUserName());
        task.setCreateDate(now);
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
        this.emptyStringToNull(task);
        String richText = this.buildTextForUpdate(record, task, new StringBuffer());
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
            if (StringUtils.hasText(task.getRemark())) {
                richText += task.getRemark();
            }
            this.addTaskRemark(task.getId(), text, richText, now);
        } else if (StringUtils.hasText(task.getRemark())) {
            this.addTaskRemark(task.getId(), text, task.getRemark(), now);
        }
    }

    /**
     * 任务详情操作更新任务状态
     *
     * @param actionType 操作类型 {@link TaskActionType}
     * @param task       前端传参
     */
    @Transactional(rollbackFor = Exception.class)
    public void action(String actionType, Task task) {
        Date now = new Date();
        String text;
        String richText;
        Task record = taskMapper.selectByPrimaryKey(task.getId());
        if (record == null) {
            throw new NotFoundException("操作失败，任务不存在或已被删除");
        }
        if (Objects.equals(actionType, TaskActionType.assign.name())) {
            text = "指派";
            this.emptyStringToNull(task);
            richText = this.buildRichTextForAssign(record, task, new StringBuffer());
            this.copyDeveloper(task, record);
            this.setRequired(record);
            if (StringUtils.hasText(richText)) {
                if (StringUtils.hasText(task.getRemark())) {
                    richText += task.getRemark();
                }
                this.addTaskRemark(task.getId(), text, richText, now);
            } else if (StringUtils.hasText(task.getRemark())) {
                this.addTaskRemark(task.getId(), text, task.getRemark(), now);
            }
        } else if (Objects.equals(actionType, TaskActionType.estimate.name())) {
            text = "排期";
            task.setFinishedDate(null);
            this.doEstimateOrComplete(task, record);
            this.addTaskRemark(task.getId(), text, task.getRemark(), now);
        } else if (Objects.equals(actionType, TaskActionType.complete.name())) {
            text = "标记为完成";
            task.setEstimateDate(null);
            if (task.getFinishedDate() == null) {
                //如果前端没传完成时间就取当前时间为完成时间
                task.setFinishedDate(now);
            }
            this.doEstimateOrComplete(task, record);
            this.allCompleteHandler(record);
            this.addTaskRemark(task.getId(), text, task.getRemark(), now);
        } else if (Objects.equals(actionType, TaskActionType.activate.name())) {
            text = "激活";
            record.setStatus(TaskStatus.doing.name());
            this.addTaskRemark(task.getId(), text, task.getRemark(), now);
        } else if (Objects.equals(actionType, TaskActionType.cancel.name())) {
            text = "取消";
            record.setStatus(TaskStatus.cancel.name());
            record.setCanceledBy(UserCache.getCurrentUserName());
            record.setCanceledDate(now);
            this.addTaskRemark(task.getId(), text, task.getRemark(), now);
        } else if (Objects.equals(actionType, TaskActionType.close.name())) {
            text = "关闭";
            record.setStatus(TaskStatus.closed.name());
            record.setClosedBy(UserCache.getCurrentUserName());
            record.setClosedDate(now);
            this.addTaskRemark(task.getId(), text, task.getRemark(), now);
        } else {
            throw new NotFoundException("操作失败，未定义的操作");
        }
        this.setLastEdited(record, now);
        int result = taskMapper.updateByPrimaryKey(record);
        if (result < 1) {
            throw new YlfjmException("操作失败，更新任务状态发生错误");
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
        Project project = projectMapper.selectByPrimaryKey(task.getProjectId());
        if (project != null) {
            task.setProjectName(project.getName());
        }
        return task;
    }

    /**
     * 分页查询任务信息，可带查询条件
     *
     * @param searchType 搜索类型 {@link TaskSearchType}
     * @param pageNum    第几页
     * @param pageSize   每页大小
     */
    public PageVO<Task> page(String searchType, int pageNum, int pageSize) {
        Example example = new Example(Task.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        List<String> statusList = new ArrayList<>();
        String developer = null;
        if (Objects.equals(searchType, TaskSearchType.all.name())) {
            statusList.add(TaskStatus.doing.name());
            statusList.add(TaskStatus.done.name());
            statusList.add(TaskStatus.cancel.name());
            statusList.add(TaskStatus.closed.name());
        } else if (Objects.equals(searchType, TaskSearchType.assignMe.name())) {
            developer = UserCache.getCurrentUserName();
        } else if (Objects.equals(searchType, TaskSearchType.notClosed.name())) {
            statusList.add(TaskStatus.doing.name());
            statusList.add(TaskStatus.done.name());
            statusList.add(TaskStatus.cancel.name());
        } else if (Objects.equals(searchType, TaskSearchType.doing.name())) {
            statusList.add(TaskStatus.doing.name());
        } else if (Objects.equals(searchType, TaskSearchType.done.name())) {
            statusList.add(TaskStatus.done.name());
        } else if (Objects.equals(searchType, TaskSearchType.cancel.name())) {
            statusList.add(TaskStatus.cancel.name());
        } else if (Objects.equals(searchType, TaskSearchType.closed.name())) {
            statusList.add(TaskStatus.closed.name());
        }
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Task> page = taskMapper.selectPage(statusList, developer);
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
        toTask.setCanceledBy(fromTask.getCanceledBy());
        toTask.setCanceledDate(fromTask.getCanceledDate());
        toTask.setClosedBy(fromTask.getClosedBy());
        toTask.setClosedDate(fromTask.getClosedDate());
        toTask.setPdEstimateDate(fromTask.getPdEstimateDate());
        toTask.setPdFinishedDate(fromTask.getPdFinishedDate());
        toTask.setUiEstimateDate(fromTask.getUiEstimateDate());
        toTask.setUiFinishedDate(fromTask.getUiFinishedDate());
        toTask.setWebEstimateDate(fromTask.getWebEstimateDate());
        toTask.setWebFinishedDate(fromTask.getWebFinishedDate());
        toTask.setAndroidEstimateDate(fromTask.getAndroidEstimateDate());
        toTask.setAndroidFinishedDate(fromTask.getAndroidFinishedDate());
        toTask.setIosEstimateDate(fromTask.getIosEstimateDate());
        toTask.setIosFinishedDate(fromTask.getIosFinishedDate());
        toTask.setServerEstimateDate(fromTask.getServerEstimateDate());
        toTask.setServerFinishedDate(fromTask.getServerFinishedDate());
        toTask.setTestEstimateDate(fromTask.getTestEstimateDate());
        toTask.setTestFinishedDate(fromTask.getTestFinishedDate());
    }

    /**
     * 前端传的值是""，把它变成null
     *
     * @param toTask 前端传参
     */
    private void emptyStringToNull(Task toTask) {
        toTask.setPdDesigner(StringUtils.hasText(toTask.getPdDesigner()) ? toTask.getPdDesigner() : null);
        toTask.setUiDesigner(StringUtils.hasText(toTask.getUiDesigner()) ? toTask.getUiDesigner() : null);
        toTask.setWebDeveloper(StringUtils.hasText(toTask.getWebDeveloper()) ? toTask.getWebDeveloper() : null);
        toTask.setAndroidDeveloper(StringUtils.hasText(toTask.getAndroidDeveloper()) ? toTask.getAndroidDeveloper() : null);
        toTask.setIosDeveloper(StringUtils.hasText(toTask.getIosDeveloper()) ? toTask.getIosDeveloper() : null);
        toTask.setServerDeveloper(StringUtils.hasText(toTask.getServerDeveloper()) ? toTask.getServerDeveloper() : null);
        toTask.setTester(StringUtils.hasText(toTask.getTester()) ? toTask.getTester() : null);
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
     * @param dbTask 数据库中的
     * @param task   前端传参
     */
    private String buildTextForUpdate(Task dbTask, Task task, StringBuffer sb) {
        if (!Objects.equals(dbTask.getName(), task.getName())) {
            sb.append("修改了 <strong><em>任务名称</em></strong>，旧值为\"").append(dbTask.getName())
                    .append("\"，新值为\"").append(task.getName()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getProjectId(), task.getProjectId())) {
            Project fromProject = projectMapper.selectByPrimaryKey(dbTask.getProjectId());
            Project toProject = projectMapper.selectByPrimaryKey(task.getProjectId());
            if (fromProject != null && toProject != null) {
                sb.append("修改了 <strong><em>所属项目</em></strong>，旧值为\"").append(fromProject.getName())
                        .append("\"，新值为\"").append(toProject.getName()).append("\"。<br/>");
            }
        }
        if (!Objects.equals(dbTask.getType(), task.getType())) {
            sb.append("修改了 <strong><em>任务类型</em></strong>，旧值为\"").append(dbTask.getType())
                    .append("\"，新值为\"").append(task.getType()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getPri(), task.getPri())) {
            sb.append("修改了 <strong><em>优先级</em></strong>，旧值为\"").append(dbTask.getPri())
                    .append("\"，新值为\"").append(task.getPri()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getDeadline(), task.getDeadline())) {
            sb.append("修改了 <strong><em>截止日期</em></strong>，旧值为\"").append(DateUtil.dateToString2(dbTask.getDeadline()))
                    .append("\"，新值为\"").append(DateUtil.dateToString2(task.getDeadline())).append("\"。<br/>");
        }
        sb.append(this.buildRichTextForAssign(dbTask, task, new StringBuffer()));
        return sb.toString();
    }

    /**
     * 组装任务操作日志/备注内容
     *
     * @param dbTask 数据库中的
     * @param task   前端传参
     */
    private String buildRichTextForAssign(Task dbTask, Task task, StringBuffer sb) {
        if (!Objects.equals(dbTask.getPdDesigner(), task.getPdDesigner())) {
            sb.append("修改了 <strong><em>产品设计</em></strong>，旧值为\"").append(dbTask.getPdDesigner())
                    .append("\"，新值为\"").append(task.getPdDesigner()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getUiDesigner(), task.getUiDesigner())) {
            sb.append("修改了 <strong><em>UI设计</em></strong>，旧值为\"").append(dbTask.getUiDesigner())
                    .append("\"，新值为\"").append(task.getUiDesigner()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getAndroidDeveloper(), task.getAndroidDeveloper())) {
            sb.append("修改了 <strong><em>安卓开发</em></strong>，旧值为\"").append(dbTask.getAndroidDeveloper())
                    .append("\"，新值为\"").append(task.getAndroidDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getIosDeveloper(), task.getIosDeveloper())) {
            sb.append("修改了 <strong><em>苹果开发</em></strong>，旧值为\"").append(dbTask.getIosDeveloper())
                    .append("\"，新值为\"").append(task.getIosDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getWebDeveloper(), task.getWebDeveloper())) {
            sb.append("修改了 <strong><em>前端开发</em></strong>，旧值为\"").append(dbTask.getWebDeveloper())
                    .append("\"，新值为\"").append(task.getWebDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getServerDeveloper(), task.getServerDeveloper())) {
            sb.append("修改了 <strong><em>后端开发</em></strong>，旧值为\"").append(dbTask.getServerDeveloper())
                    .append("\"，新值为\"").append(task.getServerDeveloper()).append("\"。<br/>");
        }
        if (!Objects.equals(dbTask.getTester(), task.getTester())) {
            sb.append("修改了 <strong><em>测试人员</em></strong>，旧值为\"").append(dbTask.getTester())
                    .append("\"，新值为\"").append(task.getTester()).append("\"。<br/>");
        }
        return sb.toString();
    }

    /**
     * 排期/完成
     *
     * @param task   前端传参
     * @param record 数据库中的
     */
    private void doEstimateOrComplete(Task task, Task record) {
        String currentPostCode = task.getCurrentPostCode();
        String currentUserName = UserCache.getCurrentUserName();
        String errorMessage = "操作失败，该任务没有指派给你";
        String errorMessage2 = "操作失败，预计完成时间不支持二次修改";
        if (Objects.equals(currentPostCode, "po")) {
            //任务没有指派给某职位或当前用户不在指派列表
            if (!record.getPdRequired() || !Objects.equals(record.getPdDesigner(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            //设置预计完成时间
            if (task.getEstimateDate() != null) {
                //如果已经设置过预计完成时间
                if (record.getPdEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setPdEstimateDate(task.getEstimateDate());
            }
            //设置实际完成时间
            if (task.getFinishedDate() != null) {
                record.setPdFinishedDate(task.getFinishedDate());
            }
        } else if (Objects.equals(currentPostCode, "ui")) {
            if (!record.getUiRequired() || !Objects.equals(record.getUiDesigner(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            if (task.getEstimateDate() != null) {
                if (record.getUiEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setUiEstimateDate(task.getEstimateDate());
            }
            if (task.getFinishedDate() != null) {
                record.setUiFinishedDate(task.getFinishedDate());
            }
        } else if (Objects.equals(currentPostCode, "android")) {
            if (!record.getAndroidRequired() || !Objects.equals(record.getAndroidDeveloper(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            if (task.getEstimateDate() != null) {
                if (record.getAndroidEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setAndroidEstimateDate(task.getEstimateDate());
            }
            if (task.getFinishedDate() != null) {
                record.setAndroidFinishedDate(task.getFinishedDate());
            }
        } else if (Objects.equals(currentPostCode, "ios")) {
            if (!record.getIosRequired() || !Objects.equals(record.getIosDeveloper(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            if (task.getEstimateDate() != null) {
                if (record.getIosEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setIosEstimateDate(task.getEstimateDate());
            }
            if (task.getFinishedDate() != null) {
                record.setIosFinishedDate(task.getFinishedDate());
            }
        } else if (Objects.equals(currentPostCode, "web")) {
            if (!record.getWebRequired() || !Objects.equals(record.getWebDeveloper(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            if (task.getEstimateDate() != null) {
                if (record.getWebEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setWebEstimateDate(task.getEstimateDate());
            }
            if (task.getFinishedDate() != null) {
                record.setWebFinishedDate(task.getFinishedDate());
            }
        } else if (Objects.equals(currentPostCode, "dev")) {
            if (!record.getServerRequired() || !Objects.equals(record.getServerDeveloper(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            if (task.getEstimateDate() != null) {
                if (record.getServerEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setServerEstimateDate(task.getEstimateDate());
            }
            if (task.getFinishedDate() != null) {
                record.setServerFinishedDate(task.getFinishedDate());
            }
        } else if (Objects.equals(currentPostCode, "test")) {
            if (!record.getTestRequired() || !Objects.equals(record.getTester(), currentUserName)) {
                throw new BadRequestException(errorMessage);
            }
            if (task.getEstimateDate() != null) {
                if (record.getTestEstimateDate() != null) {
                    throw new BadRequestException(errorMessage2);
                }
                record.setTestEstimateDate(task.getEstimateDate());
            }
            if (task.getFinishedDate() != null) {
                record.setTestFinishedDate(task.getFinishedDate());
            }
        }
    }

    /**
     * 所有开发都完成与否
     *
     * @param record 数据库中的
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
        return Objects.equals(developer, currentUserName);
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
