package com.application.marketing.common.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.common.controller.dto.ListQywxDeptDTO;
import com.application.marketing.common.controller.dto.ListQywxTagDTO;
import com.application.marketing.common.controller.dto.ListQywxUserDTO;
import com.application.marketing.common.controller.dto.WeixinGroupCreate;
import com.application.marketing.common.controller.dto.WeixinGroupMessage;
import com.application.marketing.common.domain.QywxUser;
import com.application.marketing.common.repository.QywxUserV2Dao;
import com.application.marketing.common.service.QywxAcquisitionService;
import com.application.marketing.common.service.QywxCropTagService;
import com.application.marketing.common.service.QywxDeptService;
import com.application.marketing.common.service.QywxMessageService;
import com.application.marketing.common.service.QywxUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.action.PageResult;
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
	
	@Autowired
	QywxCropTagService qywxCropTagService;
	
	@Autowired
	QywxMessageService qywxMessageService;
	
	@Autowired
	QywxUserV2Dao qywxUserV2Dao;
	
	@Autowired
	QywxAcquisitionService qywxAcquisitionService;

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
	 * 数据查询（企业微信-通讯录-成员列表）。 <br>
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/pageQywxUser")
	public PageResult<QywxUser> pageQywxUser(@RequestBody ListQywxUserDTO dto) {
//		if (StringUtils.isBlank(dto.getAppId())) {
//			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
//		}
		return qywxUserV2Dao.listUser(dto);
	}
	
//	@RequestMapping("/addCustTags")
//	public RstResult<JsonNode> addCustTags() {
//		String userId="TianGuoFa";
//		String extUserId="wmqbSyHgAAM54fXDnYaDSj6g4pNDQxvg";
//		List<String> tagList = List.of("etqbSyHgAA7nxQClAeHmvgu_HBWfbR8g","etqbSyHgAAPusU9SuwJf8FAmAemdoZOg");
//		qyWeixinService.addCustTags(userId, extUserId, tagList);
//		return ResultBuilder.normalResult();
//	}
	
	@RequestMapping("/listCustTags")
	public RstResult<JsonNode> listCustTags(@RequestBody ListQywxTagDTO dto) {
		if (StringUtils.isBlank(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		return ResultBuilder.normalResult(qywxCropTagService.listCorpTags(dto.getAppId()));
	}
	
	@RequestMapping("/creteWeixinGroup")
	public RstResult<String> creteWeixinGroup(@RequestBody WeixinGroupCreate dto) {
		String wxUserId = qywxMessageService.creteWeixinGroup(dto);
		return ResultBuilder.normalResult(wxUserId);
	}
	
	@RequestMapping("/messageWeixinGroup")
	public RstResult<String> messageWeixinGroup(@RequestBody WeixinGroupMessage dto) {
		qywxMessageService.messageWeixinGroup(dto);
		return ResultBuilder.normalResult();
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
