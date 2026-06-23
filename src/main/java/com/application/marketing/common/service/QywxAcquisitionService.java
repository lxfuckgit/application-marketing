package com.application.marketing.common.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.CustAcquisitionClient;
import com.thirdparty.params.EweixinResult;

/**
 * 企业微信-获客助手-获客链接。<br>
 */
@Service
public class QywxAcquisitionService extends QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	CustAcquisitionClient custAcquistionClient = new CustAcquisitionClient();

	/**
	 * 获客链接的创建。<br>
	 * 
	 * @param appId      应用标识<br>
	 * @param linkName   链接名称<br>
	 * @param staffList  链接关联客服人员（当客资挂沟到指定人的时候适用）<br>
	 * @param skipVerify 是否无需验证（默认无需验证）<br>
	 * @return
	 */
//	public JsonNode createHuokeLink(com.application.marketing.common.controller.dto.HuokeLinkCreate dto) {
	public JsonNode createHuokeLink(String appId, String linkName, List<String> staffList, boolean skipVerify) {
		String token = getAccessToken(appId);
		String result = custAcquistionClient.createLink(token, linkName, staffList);
		if (StringUtils.isBlank(result)) {
			logger.warn("--->接口（list_link）通信异常！");
			return null;
		}
		JsonNode json = UtilJson.json2Object(result);
		if (RETURN_CODE == json.get("errcode").intValue()) {
			return json.get("link");
		} else {
			logger.warn("--->获取list_link返回错误：{}", json.get("errmsg"));
			return null;
		}
	}
	
	/**
	 * 获客链接的删除。<br>
	 * 
	 * @param appId  应用标识<br>
	 * @param linkId 获客链接标识<br>
	 * @return
	 */
	public EweixinResult deleteHuokeLink(String appId, String linkId) {
		return custAcquistionClient.deleteLink(getAccessToken(appId), linkId);
	}

}
