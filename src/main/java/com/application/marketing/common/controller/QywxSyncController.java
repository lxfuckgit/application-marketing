package com.application.marketing.common.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.common.controller.dto.ListQywxDeptDTO;
import com.application.marketing.common.controller.dto.ListQywxUserDTO;
import com.application.marketing.common.controller.dto.SyncQywxUserDTO;
import com.application.marketing.common.service.QywxAcquisitionService;
import com.application.marketing.common.service.QywxDeptService;
import com.application.marketing.common.service.QywxUserService;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;

@RestController
public class QywxSyncController {
	@Autowired
	QywxDeptService qywxPartyService;

	@Autowired
	QywxUserService qywxUserService;
	
	@Autowired
	QywxAcquisitionService qywxAcquisitionService;
	
	@RequestMapping("/syncQywxDepartyment")
	public RstResult<String> syncQywxDepartyment(@RequestBody ListQywxDeptDTO dto) {
		if (StringUtils.isBlank(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		} else {
			qywxPartyService.syncDepartyment(dto.getAppId());
		}
		return ResultBuilder.normalResult();
	}

	@RequestMapping("/syncQywxDepartymentUser")
	public RstResult<String> syncQywxDepartymentUser(@RequestBody ListQywxUserDTO dto) {
		if (StringUtils.isBlank(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		} else if (StringUtils.isBlank(dto.getDeptId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		} else {
			qywxUserService.syncDepartymentUser(dto.getAppId(), dto.getDeptId());
		}
		return ResultBuilder.normalResult();
	}
	
	/**
	 * 数据同步。<br>
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/syncQywxUserInfo")
	public RstResult<String> syncQywxUserInfo(@RequestBody SyncQywxUserDTO dto) {
		if (StringUtils.isBlank(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		} else {
			qywxUserService.syncUserInfo(dto.getAppId(), dto.getUserId());
		}
		return ResultBuilder.normalResult();
	}
	
	/**
	 * 数据同步。<br>
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/syncQywxChatInfo")
	public RstResult<String> syncQywxChatInfo(@RequestBody Map<String, String> dto) {
		if (StringUtils.isBlank(dto.get("appId"))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		} else if (StringUtils.isBlank(dto.get("chatKey"))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		} else {
			qywxAcquisitionService.getChatInfo(dto.get("appId"), dto.get("chatKey"));
		}
		return ResultBuilder.normalResult();
	}
}
