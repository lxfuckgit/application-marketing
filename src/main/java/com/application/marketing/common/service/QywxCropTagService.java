package com.application.marketing.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.CorpTagClient;

/**
 * 企业微信-客户联系-企业标签管理。<br>
 */
@Service
public class QywxCropTagService extends QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	CorpTagClient corpTagClient = new CorpTagClient();

	public JsonNode listCorpTags(String appId) {
		String result = corpTagClient.listCorpTag(getAccessToken(appId));
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			return json.get("tag_group");
		} else {
			logger.warn("--->listCorpTag返回错误：{}", json.get("errmsg"));
			return null;
		}
	}
}
