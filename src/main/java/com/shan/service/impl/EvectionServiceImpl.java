package com.shan.service.impl;

import com.alibaba.fastjson.JSON;
import com.shan.entity.activiti.Evection;
import com.shan.service.EvectionService;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvectionServiceImpl implements EvectionService {

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void saveEvection(String processInstanceId) {
        String value = runtimeService.getVariable(processInstanceId, "content", String.class);
        Evection evection = JSON.parseObject(String.valueOf(value), Evection.class);
        System.out.println("请假单保存成功");
    }
}
