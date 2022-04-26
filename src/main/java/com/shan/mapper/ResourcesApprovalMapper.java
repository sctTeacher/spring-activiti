package com.shan.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ResourcesApprovalMapper {

	/**
	 * 获取用户待授权的流程信息
	 * @param pd
	 * @return
	 */
	List<String> getWaitAuthorizationByUserIdAndWhetherProcessing(HashMap<String, String> pd);
	
}
