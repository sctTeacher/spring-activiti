package com.shan.entity.activiti.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 审批VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Approval {
	
    // 流程实例id
    private String processInstanceId;
    
	// 申请人
	private String submitUserId;
	
	// 申请人姓名
	private String submitUserName;
	
	// 申请人部门
	private String orgId;
	
	// 申请人部门名称
	private String orgName;
	
	// 申请日期
	private String applyDate;
	
	// 申请类型
	private String approvalType;
	
	// 申请类型名称
	private String approvalTypeName;
	
	// 申请内容
	private String content;

	// 申请备注
	private String remark;
	
	// 审批级别
	private Integer approvalLevel;
	
	// 审批进度(每次处理后+1)
	private Integer approvalProgress;
	
	// 状态(1 审核中   2 已催办   3 已撤回   4 已删除   5 审核通过   6 审核未通过)
	private Integer state;
	
	// 状态值
	private String stateValue;
	
	// 任务节点完成时间,逗号拼接
	private String taskEndTime;

	// 最后一级审批意见
	private String lastOpinion;

	//审批记录
	private List<ApprovalRecord> approvalRecords;

}
