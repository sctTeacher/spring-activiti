package com.shan;

import com.shan.utils.SpringContextUtils;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;


@SpringBootApplication(exclude =  SecurityAutoConfiguration.class)
public class SpringActivitiApplication  {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SpringActivitiApplication.class, args);
        SpringContextUtils.setApplicationContext(run);
        String[] beans = run.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
            System.out.println(bean);
        }
        System.out.println("----------------");
    }





}
