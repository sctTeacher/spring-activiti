package com.shan.service;

import com.shan.entity.activiti.Evection;
import com.shan.entity.activiti.VO.Approval;
import com.shan.entity.activiti.VO.ApprovalAudit;
import com.shan.entity.activiti.VO.ApprovalUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TestActivitiService {
    void submit(Evection evection, Map<String, Object> map);

    List<Approval> applyList(String submitUserName, String submitUserId);

    HashMap echoData(String processInstanceId);

    HashMap updateApprovalInfo(ApprovalUpdate approvalUpdate);

    HashMap revocationApply(String processInstanceId);

    List<Approval> waitApprovalList(String submitUserId);

    void approvalAudit(ApprovalAudit approvalAudit);

    List<Approval> doneList(String submitUserId);
}
