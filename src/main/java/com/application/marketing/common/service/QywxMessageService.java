package com.application.marketing.common.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.marketing.common.controller.dto.WeixinGroupCreate;
import com.application.marketing.common.controller.dto.WeixinGroupMessage;
import com.application.marketing.common.domain.MessageGroup;
import com.application.marketing.common.repository.MessageGroupDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.GroupChatClient;

@Service
public class QywxMessageService extends QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MessageGroupDao messageGroupDao;

	GroupChatClient groupChatClient = new GroupChatClient();

	public String creteWeixinGroup(WeixinGroupCreate dto) {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = groupChatClient.createGroupChat(token, null, dto.getGroupName(), null, dto.getMemberList());
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			MessageGroup group = new MessageGroup();
			group.setGroupName(dto.getGroupName());
			group.setGroupChannel("qyweixin");
			group.setExtId(json.get("chatid").asText());
			messageGroupDao.save(group);
			return json.get("chatid").asText();
		} else {
			logger.warn("--->creteWeixinGroup返回错误：{}", json.get("errmsg"));
			return null;
		}
	}

	public void messageWeixinGroup(WeixinGroupMessage dto) {
		if (StringUtils.isBlank(dto.getGroupId()) || StringUtils.isBlank(dto.getContent())) {
			logger.warn("--->messageWeixinGroup消息内容为空！");
		}
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = groupChatClient.sendToGruopChat(token, dto.getGroupId(), dto.getContent(), "text",
				dto.getMemberList());
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			logger.info("--->messageWeixinGroup消息发送完毕！");
		} else {
			logger.warn("--->messageWeixinGroup返回错误：{}", json.get("errmsg"));
		}
	}

}
