package com.application.marketing.common.service;

import java.util.ArrayList;
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
	
	/**
	 * 获客链接的修改。<br>
	 * 
	 * @param appId     应用标识<br>
	 * @param linkId    获客链接标识<br>
	 * @param linkName  链接名称<br>
	 * @param staffList
	 * @return
	 */
	public EweixinResult updateHuokeLink(String appId, String linkId, String linkName, List<String> staffList) {
		String token = getAccessToken(appId);
		return custAcquistionClient.updateLink(token,linkId, linkName, staffList);
	}
	
	/**
	 * 获客链接的读取。<br>
	 * 
	 * @param appId  应用标识<br>
	 * @param linkId 获客链接标识<br>
	 * @return
	 */
//	public EweixinResult getHuokeLink(String appId, String linkId) {
//		return custAcquistionClient.getLink(getAccessToken(appId), linkId);
//	}
	
	/**
	 * 同步企业微信的“获客链接”关联的线索列表。
	 */
	public List<JsonNode> syncHuokeLinkClueList(String appId, String linkId) {
		List<JsonNode> list = new ArrayList<JsonNode>();
		
		/* 1、换取令牌 */
		String token = getAccessToken(appId);
		/* 2、提取线索 */
		String result = custAcquistionClient.listLinkClue(token, linkId);
		JsonNode json = UtilJson.json2Object(result);
		// 不成功则直接退出
		if (0 != json.get("errcode").intValue()) {
			logger.warn("--->syncQiyeWeixinLinkClueList返回错误：{}", json.get("errmsg"));
			return list;
		}
		// 成功则直接处理数据
		int page = 1;
		while (true) {
			logger.info("正在获取第{}页数据！", page++);
			list.add(json.get("customer_list"));
			// 如果存在下一页就继续遍历
			if (json.get("next_cursor") != null) {
				result = custAcquistionClient.listLinkClue(token, linkId, json.get("next_cursor").asText());
				json = UtilJson.json2Object(result);
			} else {
				logger.info("所有数据已获取完毕，共获取{}页客户数据", list.size());
				break;
			}
		}
		return list;
	}

}
