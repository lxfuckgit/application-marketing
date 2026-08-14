package com.application.marketing.common.controller;

import java.time.LocalDate;
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
import com.application.marketing.common.service.QywxPaymentService;
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
	
	@Autowired
	QywxPaymentService qywxPaymentService;
	
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
	
	/**
	 * 数据同步。<br>
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/syncQywxFundFlow")
	public RstResult<String> syncQywxFundFlow(@RequestBody Map<String, String> dto) {
		if (StringUtils.isBlank(dto.get("appId"))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (StringUtils.isBlank(dto.get("billDate"))) {
			// 默认取T-1日账单
			dto.put("billDate", LocalDate.now().minusDays(1).toString());
		}
		qywxPaymentService.getFundFlow(dto.get("appId"), dto.get("billDate"));
		return ResultBuilder.normalResult();
	}
	
	/**
	 * 数据同步。<br>
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/syncQywxFundFlowHistory")
	public RstResult<String> syncQywxFundFlowHistory(@RequestBody Map<String, String> dto) {
		if (StringUtils.isBlank(dto.get("appId"))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if (StringUtils.isBlank(dto.get("days"))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		if (StringUtils.isBlank(dto.get("billDateFrom"))) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_EMPTY);
		}
		
		Integer days = Integer.valueOf(dto.get("days"));
		LocalDate dateFrom = LocalDate.parse(dto.get("billDateFrom"));
		for (int i = 0; i < days; i++) {
			qywxPaymentService.getFundFlow(dto.get("appId"), dateFrom.plusDays(i).toString());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return ResultBuilder.normalResult();
	}
}
