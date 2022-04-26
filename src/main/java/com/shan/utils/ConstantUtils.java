package com.shan.utils;


public final class ConstantUtils {



    /**
     * activiti工作流相关   ------start-------
     */
    /**
     * ACTIVITI流程图id
     */
    public static final String PROCESSES_CHART_ID = "cloudProcess";

    /**
     * 流程图任务节点id-一级审批
     */
    public static final String TASK_FIRST = "usertask1";

    /**
     * 流程图任务节点id-二级审批
     */
    public static final String TASK_SECOND = "usertask2";

    /**
     * 流程图任务节点id-三级审批
     */
    public static final String TASK_THIRD = "usertask3";

    /**
     * 流程图任务节点id-四级审批
     */
    public static final String TASK_FOURTH = "usertask4";

    /**
     * 审批状态 1 审核中 2 已催办 3 已撤回 4 已删除 5 审核通过 6 审核未通过
     */
    public static final String[][] APPROVAL_STATE = {{"1", "审核中"}, {"2", "已催办"},
            {"3", "已撤回"}, {"4", "已删除"}, {"5", "审核通过"},
            {"6", "审核未通过"},};
 /*   public static final String[][] APPROVAL_STATE = {{"1", "EXAMINE_STATE_CHECKING"}, {"2", "EXAMINE_STATE_URGE"},
            {"3", "EXAMINE_STATE_WITHDRAW"}, {"4", "EXAMINE_STATE_DELETE"}, {"5", "EXAMINE_STATE_AUDITPASS"},
            {"6", "EXAMINE_STATE_AUDITFAILED"},};*/


    /**
     * activiti工作流相关   ------end-------
     */
}
