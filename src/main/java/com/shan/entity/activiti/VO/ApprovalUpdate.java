package com.shan.entity.activiti.VO;

import com.shan.entity.activiti.Evection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 编辑审批实体vo
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ApprovalUpdate {

	// 流程实例ID
	private String processInstanceId;
	
	// 审批类型
	private String approvalType;
	
	// 申请人
	private String submitUserId;
	
	// 申请-备注|描述
	private String remark;

	// 请假单
	private Evection evection;



}
