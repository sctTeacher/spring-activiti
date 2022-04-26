package com.shan.entity.activiti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 出差申请中的流程变量对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Evection implements Serializable {

    /**
     * 主键Id
     */
    private Long id;


    /**
     * 出差单的名字
     */
    private String evectionName;
    /**
     * 出差天数
     */
    private Double num;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 出差结束时间
     */
    private Date endDate;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 出差原因
     */
    private String reson;

    /**
     * 提交人
     */
    private String submitUserId;

}
