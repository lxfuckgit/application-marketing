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
	
	public String getUserIdByMobile(String appId, String mobile) {
		String token = getAccessToken(appId);
		String result = userInfoClient.getUserIdByMobile(token, mobile);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			// 保存本地数据
			WxUserInfo wxUser = getWxUserInfo(token, json.get("userid").asText());
			if (null != wxUser) {
				persistentQywxUser(appId, wxUser.getUserid(), wxUser.getName(), mobile);
			}
			// 返回查询结果
			return json.get("userid").asText();
		} else {
			logger.warn("--->getUserIdByMobile返回错误：{}", json.get("errmsg"));
			return null;
		}
	}

	/**
	 * 数据同步-同步部门关联成员。<br>
	 * 
	 * @param appId
	 */
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
	
	/**
	 * 数据同步-同步成员详情信息。<br>
	 * 
	 * @param appId
	 */
	public void syncUserInfo(String appId, String userId) {
		String token = getAccessToken(appId);
		WxUserInfo wxUserInfo = getWxUserInfo(token, userId);
		if (null != wxUserInfo) {
			QywxUser entity = qywxUserDao.findByUserId(userId);
			if (null == entity) {
				entity = new QywxUser();
			}
			entity.setAppId(appId);
//			entity.setDeptId(wxUserInfo.get);
			entity.setUserId(wxUserInfo.getUserid());
			entity.setUserName(wxUserInfo.getName());
			entity.setNickName(wxUserInfo.getName());
			qywxUserDao.save(entity);
		} else {
			logger.warn("--->接口（syncUserInfo）通信异常！");
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
	
	private QywxUser persistentQywxUser(String appId, String userId, String userName, String userMobile) {
		QywxUser entity = qywxUserDao.findByUserId(userId);
		if (null == entity) {
			entity = new QywxUser();
		}
		entity.setAppId(appId);
		entity.setDeptId("0");
		entity.setUserId(userId);
		entity.setUserName(userName);
		entity.setNickName(userName);
		entity.setUserMobile(userMobile);
		qywxUserDao.save(entity);
		return entity;
	}

}
