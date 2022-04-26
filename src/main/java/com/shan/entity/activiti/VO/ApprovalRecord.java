package com.shan.entity.activiti.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 审批记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApprovalRecord {

    //审批记录名
    private  String name;
    //审批人id
    private String approvalUserId;
    //审批人姓名
    private String approvalUserName;
    //审批开始时间
    private String approvalStartTime;
    //审批结束时间
    private String approvalEndTime;
    //审批意见
    private String approvalOpinion;

}
