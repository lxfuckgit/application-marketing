package com.application.marketing.callback.qywx;

import com.application.marketing.common.service.QywxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.application.marketing.campaign.service.CampaignService;
import com.javapai.framework.enums.StatusEnum;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 获客助手事件通知
 */
@Component
public class CustomerAcquisitionEvent {
	/**/
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CampaignService campaignService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	QywxService qywxService;

	String url = "https://dj.lemanman.cn/admin-api/lpg/qiwei/create";


	/**
	 * 删除获客链接事件
	 */
	public void eventDeleteLink(String linkId) {
		logger.info("--->处理回调事件：eventDeleteLink参数：{}", linkId);
		boolean r = campaignService.updateStatusByExtId(linkId, StatusEnum.DISABLE.getValue());
		logger.info("--->处理回调事件：eventDeleteLink结果：{}", r);
	}

	/**
	 * 通过获客链接发起好友请求事件。<br>
	 * <strong>官方解释：</strong>当微信用户通过获客链接点击添加到通讯录，成功发起好友请求，回调此事件到创建该链接的应用。
	 */
	public void eventFriendRequest(String linkId, String state) {
		logger.info("--->处理回调事件：eventFriendRequest参数：{}", linkId);
		Map<String, Object> map = new HashMap<>();
		map.put("state", state);
		map.put("type", 0);
		extracted(map);
	}

	void extracted(Map<String, Object> map) {
		restTemplate.exchange(
				url,
				HttpMethod.POST,
				new HttpEntity<>(map),
				String.class
				);
	}

	public void eventMessageFromCustomer(String chatKey, String corpId) {
		String token = qywxService.getAccessToken(corpId);
		String url = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_customer_acquisition_message?access_token=" + token;

		String jsonBody = String.format("{\"chat_key\": \"%s\"}", chatKey);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

		String response = restTemplate.postForObject(url, request, String.class);
		logger.info("--->处理回调事件：eventMessageFromCustomer结果：{}", response);

		if (response != null) {
			JsonNode json = UtilJson.json2Object(response);
			if (json != null) {
				int errcode = json.has("errcode") ? json.get("errcode").asInt() : -1;
				String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : null;
				String userid = json.has("userid") ? json.get("userid").asText() : null;
				String externalUserId = json.has("external_userid") ? json.get("external_userid").asText() : null;
				JsonNode chatInfo = json.has("chat_info") ? json.get("chat_info") : null;
				String recvMsgCnt = null;
				String state = null;
				if (chatInfo != null) {
					 recvMsgCnt = chatInfo.has("recv_msg_cnt") ? chatInfo.get("recv_msg_cnt").asText() : null;
					 state = chatInfo.has("state") ? chatInfo.get("state").asText() : null;
					logger.info("--->解析结果：recvMsgCnt={}, state={}", recvMsgCnt, state);
				}

				logger.info("--->解析结果：errcode={}, errmsg={}, userid={},externalUserId={}, recvMsgCnt={}, state={}", errcode, errmsg, userid,externalUserId, recvMsgCnt, state);
				Map<String, Object> params = new HashMap<>();
				params.put("userid", userid);
				params.put("externalUserId", externalUserId);
				params.put("recvMsgCnt", recvMsgCnt);
				params.put("state", state);
				params.put("type",2);

				extracted(params);
				if (0 == errcode) {
					logger.info("--->获取获客消息成功，成员userid：{}", userid);
				} else {
					logger.warn("--->获取获客消息失败：{}", errmsg);
				}
			} else {
				logger.warn("--->解析JSON响应失败，响应内容：{}", response);
			}
		}
	}

	/**
	 * 【获客链接】成员首次收外部用户的消息事件。<br>
	 * <strong>官方解释：</strong>当微信用户通过获客链接点击添加到通讯录，成功发起好友请求，回调此事件到创建该链接的应用。
	 * 
	 * @param linkId
	 * @param userId
	 * @param externalUserID
	 */
	public void customerStartChat(String linkId, String userId, String externalUserID) {
		logger.info("--->成员首次收消息事件回调：firstTimeAcceptingMessage参数：{}-{}", userId, externalUserID);
		Map<String, Object> params = new HashMap<>();
		params.put("userid", userId);
		params.put("externalUserId", externalUserID);
		params.put("type", 3);
		extracted(params);
	}
}
