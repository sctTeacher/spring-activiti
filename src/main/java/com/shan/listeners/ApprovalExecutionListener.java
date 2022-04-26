package com.shan.listeners;

import com.shan.service.EvectionService;
import com.shan.utils.ConstantUtils;
import com.shan.utils.SpringContextUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.Map;

public class ApprovalExecutionListener implements ExecutionListener {

    private static final long serialVersionUID = -283012834369688647L;

    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("审批通过调用notify："+execution.getEventName());
        if ("end".equals(execution.getEventName())) {
            System.out.println("审批通过调用："+execution.getEventName());
            RuntimeService runtimeService = SpringContextUtils.getBean("runtimeServiceBean", RuntimeService.class);
            String processInstanceId = execution.getProcessInstanceId();
            // 维护状态值
            Map<String, Object> map = new HashMap<>();
            map.put("state", ConstantUtils.APPROVAL_STATE[4][0]);
            map.put("stateValue", ConstantUtils.APPROVAL_STATE[4][1]);
            runtimeService.setVariablesLocal(processInstanceId, map);


            String approvalType = runtimeService.getVariable(processInstanceId, "approvalType", String.class);
            System.out.println("审批通过调用"+processInstanceId);
            System.out.println(approvalType);
            //保存业务数据EvectionService
            EvectionService evectionService = SpringContextUtils.getBean("evectionServiceImpl", EvectionService.class);
            evectionService.saveEvection(processInstanceId);
        }
    }





}
