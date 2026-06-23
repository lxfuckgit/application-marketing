package com.application.marketing.common.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.common.controller.dto.ListQywxDeptDTO;
import com.application.marketing.common.controller.dto.ListQywxUserDTO;
import com.application.marketing.common.service.QywxDeptService;
import com.application.marketing.common.service.QywxUserService;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;
import com.thirdparty.params.WxDeptInfo;

@RestController
public class QywxController {
	@Autowired
	QywxDeptService qywxPartyService;

	@Autowired
	QywxUserService qywxUserService;

	/**
	 * 数据查询（企业微信-通讯录-部门列表）。 <br>
	 * 
	 * @param dto
	 * @return
	 */
	public List<WxDeptInfo> listQywxDepartyment(@RequestBody ListQywxDeptDTO dto) {
		if (StringUtils.isBlank(dto.getAppId())) {
			return null;
		}
		return qywxPartyService.listDepartyment(dto.getAppId());
	}

	/**
	 * 数据同步。<br>
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/syncQywxDepartyment")
	public RstResult<String> syncQywxDepartyment(@RequestBody ListQywxDeptDTO dto) {
		if (StringUtils.isBlank(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		} else {
			qywxPartyService.syncDepartyment(dto.getAppId());
		}
		return ResultBuilder.normalResult();
	}

	/**
	 * 数据同步。<br>
	 * 
	 * @param dto
	 * @return
	 */
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

}
