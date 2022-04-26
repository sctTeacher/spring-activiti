package com.shan.controller;

import com.alibaba.fastjson.JSON;
import com.shan.entity.activiti.Evection;
import com.shan.entity.activiti.VO.Approval;
import com.shan.entity.activiti.VO.ApprovalAudit;
import com.shan.entity.activiti.VO.ApprovalUpdate;
import com.shan.service.TestActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 此处为activiti 常用功能demo  有些参数应为前端传递 此处为方便 故写为固定值
 * 此工作流方式 对应前端 录入完后 只有提交按钮  没有保存按钮  故 提交时启动流程
 */
@RestController
@RequestMapping("approval")
@Slf4j
public class TestActivitiController {


    @Autowired
    private TestActivitiService testActivitiService;

    /**
     * 模拟提交申请
     * 提交人   张三 1001
     * 审批人   李四 1002
     */
    @GetMapping("/submitApproval")
    public void submit() {
        Evection evection = Evection.builder().evectionName("请假申请单").num(1.0).submitUserId("1001").build();
        Map<String, Object> map = new HashMap<>();
        //第一审批人
        map.put("firstApproval", "1002");
        map.put("listTitle", "创建请假单");
        map.put("content", JSON.toJSONString(evection));
        testActivitiService.submit(evection, map);
    }


    /**
     * 我的申请列表
     *
     * @return
     */
    @GetMapping("/applyList")
    public List<Approval> applyList() {
        String submitUserName = "zhangsan(张三)";
        String submitUserId = "1001";
        log.info("我的申请列表");
        List<Approval> list = testActivitiService.applyList(submitUserName, submitUserId);
        System.out.println(list.toString());
        return list;

    }

    /**
     * 编辑数据回显
     * @return
     */
    @GetMapping("/echoData")
    public HashMap echoData() {
        log.info("编辑数据回显");
        //实例id
        String processInstanceId = "5";
        HashMap result = testActivitiService.echoData(processInstanceId);
        System.out.println(result.toString());
        return result;
    }


    /**
     * 编辑审批内容
     * @param approvalUpdate
     * @return
     */
    @RequestMapping(value = "/updateApprovalInfo", method = RequestMethod.POST)
    public HashMap updateApprovalInfo(@RequestBody ApprovalUpdate approvalUpdate) {
        log.info("编辑审批内容");
        HashMap result = testActivitiService.updateApprovalInfo(approvalUpdate);
        System.out.println(result.toString());
        return result;
    }

    /**
     * 撤回
     *
     * @return
     */
    @GetMapping("/revocationApply")
    public HashMap revocationApply() {
        log.info("撤回");
        String processInstanceId = "5";
        HashMap result = testActivitiService.revocationApply(processInstanceId);
        System.out.println(result.toString());
        return result;
    }


    /**
     * 我的待办列表
     * 提交人   张三 1001
     * 审批人   李四 1002
     * @return
     */
    @GetMapping("/waitApprovalList")
    public List<Approval> waitApprovalList() {
        log.info("我的待办列表");
        String submitUserId = "1002";
        List<Approval> list = testActivitiService.waitApprovalList(submitUserId);
        System.out.println(list.toString());
        return list;
    }

    /**
     * 审核同意或拒绝
     * @param approvalAudit
     * @return
     */
    @RequestMapping(value = "/approvalAudit", method = RequestMethod.POST)
    public String approvalAudit(@RequestBody ApprovalAudit approvalAudit) {
        log.info("审核同意或拒绝");
        testActivitiService.approvalAudit(approvalAudit);
        return "ok";
    }


    /**
     * 我的已办列表
     * 提交人   张三 1001
     * 审批人   李四 1002
     * @return
     */
    @GetMapping("/doneList")
    public List<Approval> doneList() {
        log.info("我的已办列表");
        String submitUserId = "1002";
        List<Approval> list = testActivitiService.doneList(submitUserId);
        System.out.println(list.toString());
        return list;
    }



}
