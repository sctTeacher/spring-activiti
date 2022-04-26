package com.shan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shan.entity.activiti.Evection;
import com.shan.entity.activiti.VO.Approval;
import com.shan.entity.activiti.VO.ApprovalAudit;
import com.shan.entity.activiti.VO.ApprovalUpdate;
import com.shan.mapper.ResourcesApprovalMapper;
import com.shan.service.TestActivitiService;
import com.shan.utils.ConstantUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TestActivitiServiceImpl implements TestActivitiService {
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ResourcesApprovalMapper resourcesApprovalMapper;

    @Override
    public void submit(Evection evection, Map<String, Object> map) {
        String instanceId = submitApply(evection, "1", map);
        System.out.println("流程启动实例id：：" + instanceId);
    }

    /**
     * @param evection      申请单实体 可以抽取为公共
     * @param approvalLevel 审批层级
     * @param parameters    存入流程相关参数
     * @return firstApproval   第一级审批人
     * listTitle  标题
     * content   内容 申请实体
     * submitUserName 提交人姓名
     * submitUserId 提交人id
     * processInstanceId 实例id
     * approvalLevel 审批级别
     * state  状态编码
     * stateValue  状态中文名
     * approvalProgress 审批进度 (每次处理后+1)
     * taskEndTime 结束时间
     */
    private String submitApply(Evection evection, String approvalLevel, Map<String, Object> parameters) {
        //启动流程实例
        identityService.setAuthenticatedUserId(evection.getSubmitUserId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ConstantUtils.PROCESSES_CHART_ID);
        System.out.println("请假流程启动成功实例id：：" + processInstance.getId());
        // 所有类型的公共参数
        parameters.put("submitUserName", String.format("%s%s", "zhangsan", "(张三)"));
        parameters.put("submitUserId", evection.getSubmitUserId());
        parameters.put("processInstanceId", processInstance.getId());
        parameters.put("approvalLevel", approvalLevel);
        parameters.put("state", ConstantUtils.APPROVAL_STATE[0][0]);
        parameters.put("stateValue", ConstantUtils.APPROVAL_STATE[0][1]);
        parameters.put("approvalProgress", 1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        parameters.put("taskEndTime", dtf.format(now));
        //提交流程任务
        Task currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(currentTask.getId(), parameters);
        System.out.println("执行【员工申请】环节，流程推动到【上级审核】环节");
        return processInstance.getId();
    }

    @Override
    public List<Approval> applyList(String submitUserName, String submitUserId) {
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .variableValueLikeIgnoreCase("submitUserName", "%" + submitUserName + "%")
                .startedBy(submitUserId)
                .orderByProcessInstanceStartTime().desc();
        //不分页
        List<HistoricProcessInstance> list = historicProcessInstanceQuery.list();
        //分页 需构建分页参数 前端传并处理
        //List<HistoricProcessInstance> listPage = historicProcessInstanceQuery.listPage(0,10);
        //封装为实体
        List<Approval> runTimeProcessVariable = getHistoryProcessVariable(list, null);
        // 处理 为未处理存库
        HashMap param = new HashMap();
        param.put("userId", submitUserId);
        param.put("whetherProcessing", 0);
        List<String> waitAuthorizationUntreated = resourcesApprovalMapper.getWaitAuthorizationByUserIdAndWhetherProcessing(param);
        for (Approval approval : runTimeProcessVariable) {
            if (waitAuthorizationUntreated.contains(approval.getProcessInstanceId())) {
                approval.setState(Integer.valueOf(ConstantUtils.APPROVAL_STATE[0][0]));
                approval.setStateValue(ConstantUtils.APPROVAL_STATE[0][1]);
            }
        }
        return runTimeProcessVariable;
    }


    /**
     * 历史变量查询
     *
     * @param listPage               需要查询的历史流程对象
     * @param excludeProcessInstance 需要忽略的流程对象
     * @return List<Approval>
     */
    private List<Approval> getHistoryProcessVariable(List<HistoricProcessInstance> listPage,
                                                     List<String> excludeProcessInstance) {
        List<Approval> result = new ArrayList<>();
        for (HistoricProcessInstance HistoricProcessInstance : listPage) {
            if (CollectionUtils.isEmpty(excludeProcessInstance)
                    || !excludeProcessInstance.contains(HistoricProcessInstance.getId())) {
                result.add(getHistoryProcessVariable(HistoricProcessInstance));
            }
        }
        return result;
    }

    private Approval getHistoryProcessVariable(HistoricProcessInstance historicProcessInstance) {
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId()).list();
        Map<String, Object> variables = new HashMap<>();
        for (HistoricVariableInstance historicVariableInstance : list) {
            variables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
        }
        return getApproval(historicProcessInstance.getId(), historicProcessInstance.getStartUserId(), historicProcessInstance.getStartTime(), variables);
    }

    private Approval getApproval(String processId, String startUserId, Date startTime, Map<String, Object> variables) {
        Approval approval = new Approval();
        if (variables == null || variables.isEmpty()) {
            return approval;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        approval.setProcessInstanceId(processId);
        approval.setSubmitUserId(startUserId);
        approval.setSubmitUserName(String.valueOf(variables.get("submitUserName")));
        approval.setApplyDate(formatter.format(startTime));

        //提交时存的
        approval.setApprovalLevel(Integer.valueOf(String.valueOf(variables.get("approvalLevel"))));
        approval.setApprovalProgress(Integer.valueOf(String.valueOf(variables.get("approvalProgress"))));
        String content = String.valueOf(variables.get("listTitle"));
        approval.setContent(content);
        approval.setState(Integer.valueOf(String.valueOf(variables.get("state"))));
        approval.setStateValue(String.valueOf(variables.get("stateValue")));

        // 获取各个任务节点完成时间
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processId).orderByHistoricTaskInstanceEndTime().asc().list();
        StringJoiner taskEndTime = new StringJoiner(",");
        for (int i = 0, size = list.size(); i < size; i++) {
            HistoricTaskInstance historicTaskInstance = list.get(i);
            Date endTime = historicTaskInstance.getEndTime();
            if (endTime != null) {
                taskEndTime.add(formatter.format(endTime));
            }
            if (i == size - 1) {
                //每一级审批意见 key  审批意见value  auditOpinion    例如  usertask1AuditInfo 一级审批意见   usertask2AuditInfo 二级审批意见
                Object auditInfoObject = variables.get(historicTaskInstance.getTaskDefinitionKey() + "AuditInfo");
                if (auditInfoObject != null) {
                    JSONObject auditInfo = JSONObject.parseObject(String.valueOf(auditInfoObject));
                    approval.setLastOpinion(auditInfo.getString("auditOpinion"));
                }
            }
        }
        approval.setTaskEndTime(taskEndTime.toString());
        return approval;
    }

    @Override
    public HashMap echoData(String processInstanceId) {
        HashMap pd = new HashMap();
        // 获取流程实例对应的所有变量
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();
        String[] approvalOpinion = new String[4];
        for (HistoricVariableInstance historicVariableInstance : list) {
            String variableName = historicVariableInstance.getVariableName();
            Object value = historicVariableInstance.getValue();
            if ("submitUserName".equals(variableName)) {
                pd.put("submitUserName", value);
            }
            // 获取每一级的审批意见
            if ("usertask1AuditInfo".equals(variableName)) {
                JSONObject auditInfo = JSONObject.parseObject(String.valueOf(value));
                approvalOpinion[0] = "一级审批人" + "(" + auditInfo.getString("auditUserRealName") + ")" + "："
                        + auditInfo.getString("auditOpinion");
            }
            if ("usertask2AuditInfo".equals(variableName)) {
                JSONObject auditInfo = JSONObject.parseObject(String.valueOf(value));
                approvalOpinion[1] = "二级审批人" + "(" + auditInfo.getString("auditUserRealName") + ")" + "："
                        + auditInfo.getString("auditOpinion");
            }
            if ("usertask3AuditInfo".equals(variableName)) {
                JSONObject auditInfo = JSONObject.parseObject(String.valueOf(value));
                approvalOpinion[2] = "三级审批人" + "(" + auditInfo.getString("auditUserRealName") + ")" + "："
                        + auditInfo.getString("auditOpinion");
            }
            if ("usertask4AuditInfo".equals(variableName)) {
                JSONObject auditInfo = JSONObject.parseObject(String.valueOf(value));
                approvalOpinion[3] = "四级审批人" + "(" + auditInfo.getString("auditUserRealName") + ")" + "："
                        + auditInfo.getString("auditOpinion");
            }
            if ("content".equals(variableName)) {
                Object content =JSONObject.parseObject(String.valueOf(value),Evection.class);
                pd.put("content", content);
            }
        }
        pd.put("approvalOpinion", approvalOpinion);
        return pd;

    }

    @Override
    public HashMap updateApprovalInfo(ApprovalUpdate approvalUpdate) {
        HashMap result = new HashMap();
        Integer approvalProgress = runtimeService.getVariable(approvalUpdate.getProcessInstanceId(), "approvalProgress",
                Integer.class);
        if (approvalProgress != 1) {
            result.put("error", "已经开始审核，无法编辑");
            return result;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("remark", approvalUpdate.getRemark());
        Evection evection = approvalUpdate.getEvection();
        parameters.put("content", JSON.toJSONString(evection));
        runtimeService.setVariables(approvalUpdate.getProcessInstanceId(), parameters);
        return result;
    }

    @Override
    public HashMap revocationApply(String processInstanceId) {
        HashMap result = new HashMap();
        Integer approvalProgress = runtimeService.getVariable(processInstanceId, "approvalProgress",
                Integer.class);
        if (approvalProgress != 1) {
            result.put("error", "已经开始审核，无法撤销");
            return result;
        }
        Map<String, Object> map = new HashMap<>();
        String state = ConstantUtils.APPROVAL_STATE[2][0];
        String stateValue = ConstantUtils.APPROVAL_STATE[2][1];
        map.put("state", state);
        map.put("stateValue", stateValue);
        runtimeService.setVariables(processInstanceId, map);
        runtimeService.deleteProcessInstance(processInstanceId, stateValue);
        return null;
    }

    @Override
    public List<Approval> waitApprovalList(String submitUserId) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateUser(submitUserId).orderByTaskCreateTime().desc();
        //分页 前端传
        // List<Task> tasks = taskQuery.listPage(0, 10);
        List<Task> list = taskQuery.list();
        List<Approval> result = getTaskProcessVariable(list);
        return result;
    }


    private List<Approval> getTaskProcessVariable(List<Task> list) {
        List<Approval> result = new ArrayList<>();
        for (Task task : list) {
            ProcessInstance singleResult = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();
            result.add(getRunTimeProcessVariable(singleResult));
        }
        return result;
    }

    /**
     * @param processInstance 流程实例id
     * @return firstApproval   第一级审批人
     * listTitle  标题
     * content   内容 申请实体
     * submitUserName 提交人姓名
     * submitUserId 提交人id
     * processInstanceId 实例id
     * approvalLevel 审批级别
     * state  状态编码
     * stateValue  状态中文名
     * approvalProgress 审批进度 (每次处理后+1)
     * taskEndTime 结束时间
     */
    private Approval getRunTimeProcessVariable(ProcessInstance processInstance) {
        List<String> keyList = new ArrayList<>();
        keyList.add("submitUserName");
        keyList.add("processInstanceId");
        keyList.add("approvalLevel");
        keyList.add("state");
        keyList.add("stateValue");
        keyList.add("approvalProgress");
        keyList.add("content");
        keyList.add("listTitle");
        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId(), keyList);
        return getApproval(processInstance.getId(), processInstance.getStartUserId(), processInstance.getStartTime(), variables);
    }


    @Override
    public void approvalAudit(ApprovalAudit approvalAudit) {
        Task task = taskService.createTaskQuery().processInstanceId(approvalAudit.getProcessInstanceId())
                .singleResult();
        if (task == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("auditOpinion", approvalAudit.getAuditOpinion());
        jsonObject.put("auditUserId", approvalAudit.getAuditUserId());
        jsonObject.put("auditUserRealName", approvalAudit.getAuditUserRealName());

        Map<String, Object> map = new HashMap<>();
        map.put(task.getTaskDefinitionKey() + "AuditInfo", jsonObject.toJSONString());
        // 通过
        if (approvalAudit.getAuditResults()) {
            runtimeService.setVariables(approvalAudit.getProcessInstanceId(), map);
            taskService.complete(task.getId());
        } else {
            // 拒绝
            map.put("state", ConstantUtils.APPROVAL_STATE[5][0]);
            map.put("stateValue", ConstantUtils.APPROVAL_STATE[5][1]);
            runtimeService.setVariables(approvalAudit.getProcessInstanceId(), map);
            runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "拒绝");
        }
    }

    @Override
    public List<Approval> doneList(String submitUserId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee(submitUserId).finished().list();
        for(HistoricTaskInstance task :list){
            System.out.println(task);
            Map<String, Object> processVariables = task.getProcessVariables();
            System.out.println(processVariables);
        }
        return null;
    }


}
