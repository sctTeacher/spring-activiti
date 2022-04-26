package com.shan.entity.activiti.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 审核实体 vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApprovalAudit {
	
    // 流程实例id
    private String processInstanceId;
    
    // 审核意见
    private String auditOpinion;
    
    // 审核结果
    private Boolean auditResults;
    
    // 审核人
    private String auditUserId;
    
    // 审核人姓名
    private String auditUserRealName;

    // 审批类型
    private String approvalType;

}
