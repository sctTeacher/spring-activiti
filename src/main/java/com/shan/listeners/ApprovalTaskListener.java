package com.shan.listeners;

import com.shan.utils.ConstantUtils;
import com.shan.utils.SpringContextUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.util.Arrays;
import java.util.List;

public class ApprovalTaskListener implements TaskListener {

    private static final long serialVersionUID = 1993617150122957396L;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("ApprovalTaskListener--notify调用:"+delegateTask.getEventName());
        if (EVENTNAME_CREATE.equals(delegateTask.getEventName())) {
            // 获取当前任务节点的审批人
            List<String> list = findTaskCandidateUsers(delegateTask);
            delegateTask.addCandidateUsers(list);
        }
        if (EVENTNAME_COMPLETE.equals(delegateTask.getEventName())) {
            // 维护审批进度   正常是runtimeService实例  不知道为啥spring容器加载的名称是runtimeServiceBean
            RuntimeService runtimeService = SpringContextUtils.getBean("runtimeServiceBean", RuntimeService.class);
            Integer approvalProgress = delegateTask.getVariable("approvalProgress", Integer.class);
            runtimeService.setVariable(delegateTask.getProcessInstanceId(), "approvalProgress", approvalProgress + 1);
        }
    }

    private List<String> findTaskCandidateUsers(DelegateTask delegateTask) {
        System.out.println("ApprovalTaskListener--findTaskCandidateUsers调用");
        // 流程图任务节点id
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();

        // 获取当前任务节点的审批人
        String currentTaskApprover;
        if (ConstantUtils.TASK_FIRST.equals(taskDefinitionKey)) {
            currentTaskApprover = delegateTask.getVariable("firstApproval", String.class);
        } else if (ConstantUtils.TASK_SECOND.equals(taskDefinitionKey)) {
            currentTaskApprover = delegateTask.getVariable("secondApproval", String.class);
        } else if (ConstantUtils.TASK_THIRD.equals(taskDefinitionKey)) {
            currentTaskApprover = delegateTask.getVariable("thirdApproval", String.class);
        } else if (ConstantUtils.TASK_FOURTH.equals(taskDefinitionKey)) {
            currentTaskApprover = delegateTask.getVariable("fourthApproval", String.class);
        } else {
            throw new RuntimeException("ERROR");
        }
        return Arrays.asList(currentTaskApprover.split(","));
    }

}
