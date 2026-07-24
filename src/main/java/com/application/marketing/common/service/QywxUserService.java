package com.application.marketing.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.marketing.common.controller.vo.WxUserInfo;
import com.application.marketing.common.domain.QywxUser;
import com.application.marketing.common.repository.QywxUserDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.UserInfoClient;

@Service
public class QywxUserService extends QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	QywxUserDao qywxUserDao;

	UserInfoClient userInfoClient = new UserInfoClient();

	public void syncDepartymentUser(String appId, String deptId) {
		String token = getAccessToken(appId);
		String result = userInfoClient.listUserInfoByDeptId(token, deptId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			json.get("userlist").forEach(user -> {
				WxUserInfo info = getWxUserInfo(token, user.get("userid").asText());
				if (null == info) {
					logger.warn("--->接口（getWxUserInfo）通信异常!");
				}
				// 同步处理本地数据
				QywxUser entity = qywxUserDao.findByUserId(info.getUserid());
				if (null == entity) {
					entity = new QywxUser();
				}
				entity.setAppId(appId);
				entity.setDeptId(deptId);
				entity.setUserId(info.getUserid());
				entity.setUserName(info.getName());
				entity.setNickName(info.getName());
				qywxUserDao.save(entity);
			});
		} else {
			logger.warn("--->接口（syncDepartymentUser）通信异常，返回信息：{}", json.get("errmsg").asText());
		}
	}

	private WxUserInfo getWxUserInfo(String token, String wxUserId) {
		String result = userInfoClient.getUserInfoByUserId(token, wxUserId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			WxUserInfo wxUserInfo = new WxUserInfo();
			wxUserInfo.setUserid(json.get("userid").asText());
			wxUserInfo.setName(json.get("name").asText());
			wxUserInfo.setStatus(json.get("status").asText());
			if (json.has("avatar")) {
				// 需要二次授权才有此信息
				wxUserInfo.setAvatar(json.get("avatar").asText());
			}
			return wxUserInfo;
		} else {
			logger.warn("--->接口（gettoken）通信异常！");
			return null;
		}
	}

}
