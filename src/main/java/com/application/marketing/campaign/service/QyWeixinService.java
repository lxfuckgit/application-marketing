package com.application.marketing.campaign.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.controller.dto.HuokeLinkCreate;
import com.application.marketing.controller.dto.WXTokenVO;
import com.application.marketing.controller.dto.WeixinGroupCreate;
import com.application.marketing.controller.dto.WeixinGroupMessage;
import com.application.marketing.controller.dto.WxUserInfo;
import com.application.marketing.domain.MessageGroup;
import com.application.marketing.domain.ThirdAccount;
import com.application.marketing.repository.MessageGroupDao;
import com.application.marketing.repository.ThirdAccountDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.CorpTagClient;
import com.thirdparty.eweixin.CustAcquisitionClient;
import com.thirdparty.eweixin.CustomerClient;
import com.thirdparty.eweixin.DeptInfoClient;
import com.thirdparty.eweixin.GroupChatClient;
import com.thirdparty.eweixin.QiyeWeixinClient;
import com.thirdparty.eweixin.UserInfoClient;
import com.thirdparty.params.EweixinResult;
import com.thirdparty.params.WxCustContact;
import com.thirdparty.params.WxDeptInfo;

@Service
public class QyWeixinService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 接口成功标记
	 */
	private static final Integer RETURN_CODE = 0;

	@Autowired
	private ThirdAccountDao thirdAccountDao;

	@Autowired
	MarketingDao marketingDao;
	
	@Autowired
	MessageGroupDao messageGroupDao;
	
	CustomerClient customerClient = new CustomerClient();
	
	UserInfoClient userInfoClient = new UserInfoClient();
	
	DeptInfoClient deptInfoClient = new DeptInfoClient();
	
	GroupChatClient groupChatClient = new GroupChatClient();
	
	QiyeWeixinClient qiyeWeixinClient = new QiyeWeixinClient();
	
	CustAcquisitionClient custAcquistionClient = new CustAcquisitionClient();
	
	CorpTagClient corpTagClient = new CorpTagClient();
	
	/**
	 * 查询企业微信的“获客链接”。
	 * 
	 * @param dto
	 * @return
	 */
//	public RstResult<JsonNode> listHuokeLink() {
//		String token = getAccessToken("ww5b77b727717ccd72");
//		JsonNode linkList = qiyeWeixinClient.list_link(token);
//		return ResultBuilder.normalResult(linkList);
//	}
//
	/**
	 * 创建企业微信的“获客链接”。
	 * 
	 * @param dto
	 * @return
	 */
	public JsonNode createHuokeLink(HuokeLinkCreate dto) {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = custAcquistionClient.createLink(token, dto.getLinkName(), dto.getStaffList());
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
//
//	/**
//	 * 更新企业微信的“获客链接”。
//	 * 
//	 * @param dto
//	 * @return
//	 */
//	public RstResult<String> updateHuokeLink(HuokeLinkCreate dto) {
//		String token = getAccessToken("ww5b77b727717ccd72");
//		JsonNode link = qiyeWeixinClient.create_link(token, dto.getLinkName(), dto.getStaffList());
//		return ResultBuilder.normalResult();
//	}
	
	public EweixinResult deleteHuokeLink(String linkId) {
		String token = getAccessToken("ww5b77b727717ccd72");
		return custAcquistionClient.deleteLink(token, linkId);
	}
	
//
//	/**
//	 * 同步企业微信的“获客链接”列表。
//	 */
//	public void syncQiyeWeixinLinkList(String appId) {
//		JsonNode linkList = qiyeWeixinClient.list_link(appId);
////		linkList.forEach(link -> {
////			Marketing entity = marketingDao.findByExtid(link.asText());
////			if (null == entity) {
////				JsonNode linkDetail = qiyeWeixinClient.get_link(appId, link.asText());
////				entity = new Marketing();
////				entity.setExtid(link.asText());
////				entity.setName(linkDetail.get("link_name").asText());
////				entity.setLink(linkDetail.get("url").asText());
////				entity.setStatus(StatusEnum.ENABLE.getValue());
////				marketingDao.save(entity);
////				logger.info("--->同步活动({})完成！", entity.getName());
////			} else {
////
////			}
////		});
//	}

	/**
	 * 同步企业微信的“获客链接”关联的线索列表。
	 */
	public List<JsonNode> syncQiyeWeixinLinkClueList(String linkId) {
		List<JsonNode> list = new ArrayList<JsonNode>();
		/* 1、提取线索 */
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = custAcquistionClient.listLinkClue(token, linkId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 != json.get("errcode").intValue()) {
			logger.warn("--->syncQiyeWeixinLinkClueList返回错误：{}", json.get("errmsg"));
			return null;
		}
		/* 2、提取线索 */
		if (json.get("next_cursor") != null) {

		} else {
			list.add(json.get("customer_list"));
			return list;
		}
		return null;
	}
	
	
	public String getUserByMobile(String mobile) {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = userInfoClient.getUserIdByMobile(token, mobile);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			return json.get("userid").asText();
		} else {
			logger.warn("--->getUserIdByMobile返回错误：{}", json.get("errmsg"));
			return null;
		}
	}
	
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
		String result = groupChatClient.sendToGruopChat(token, dto.getGroupId(), dto.getContent(), "text", dto.getMemberList());
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			logger.info("--->messageWeixinGroup消息发送完毕！");
		} else {
			logger.warn("--->messageWeixinGroup返回错误：{}", json.get("errmsg"));
		}
	}

	public String getAccessToken(String appId) {
		ThirdAccount ta = thirdAccountDao.findByAppId(appId);
		if (null == ta) {
			logger.warn("--->getAccessToken返回错误：appId未配置");
			return null;
		} else if (ta.getExpiresTime() > System.currentTimeMillis()) {
			return ta.getAccessToken();
		}
		/* 更新本地token */
		String result = qiyeWeixinClient.getAccessToken(ta.getAppId(), ta.getAppSecret());
		if (StringUtils.isBlank(result)) {
			logger.warn("--->接口（gettoken）通信异常！");
			return null;
		}
		Map<String, String> resultMap = UtilJson.string2Map(result);
		if (null == resultMap || !"0".equals(resultMap.get("errcode"))) {
			logger.warn("--->获取token返回错误：{}", resultMap.get("errmsg"));
			return null;
		}
		WXTokenVO vo = new WXTokenVO(resultMap.get("access_token"), Long.valueOf(resultMap.get("expires_in")));;
		ta.setAccessToken(vo.getAccess_token());
		ta.setExpiresTime(System.currentTimeMillis() + vo.getExpires_in() * 1000);
		thirdAccountDao.save(ta);
		/* 返回最新token */
		return vo.getAccess_token();
	}

	public List<WxUserInfo> listFollowUser() {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = userInfoClient.listFollowUserId(token);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			List<WxUserInfo> list = new ArrayList<WxUserInfo>();
			json.get("follow_user").forEach(userId -> {
				WxUserInfo wxuserInfo = getWxUserInfo(token, userId.asText());
				if (null != wxuserInfo) {
					list.add(wxuserInfo);
				}
			});
			return list;
		} else {
			logger.warn("--->接口（gettoken）通信异常！");
			return null;
		}
	}
	
	public List<WxDeptInfo> listDepartyment() {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = deptInfoClient.listDept(token, null);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			List<WxDeptInfo> list = new ArrayList<WxDeptInfo>();
			json.get("department_id").forEach(dept -> {
				WxDeptInfo info = getWxDeptInfo(token, dept.get("id").asText());
				if (null != info) {
					list.add(info);
				}
			});
			return list;
		} else {
			logger.warn("--->接口（listDept）通信异常！");
			return null;
		}
	}
	
	public List<WxUserInfo> listDeptUser(String deptId) {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = userInfoClient.listUserInfoByDeptId(token, deptId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			List<WxUserInfo> list = new ArrayList<WxUserInfo>();
			json.get("userlist").forEach(user -> {
				WxUserInfo info = getWxUserInfo(token, user.get("userid").asText());
//				WxUserInfo info = new WxUserInfo();
//				info.setUserid(user.get("userid").asText());
//				info.setName(user.get("name").asText());
				list.add(info);
			});
			return list;
		} else {
			logger.warn("--->接口（listDeptUser）通信异常！");
			return null;
		}
	}
	
	public JsonNode listCustTags() {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = corpTagClient.listCorpTag(token);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			return json.get("tag_group");
		} else {
			logger.warn("--->listCorpTag返回错误：{}", json.get("errmsg"));
			return null;
		}
	}
	
	public void addCustTags(String userId, String extUserId, List<String> tagList) {
		String token = getAccessToken("ww5b77b727717ccd72");
		String result = corpTagClient.addCorpTag(token, userId, extUserId, tagList);
		logger.warn("--->addCustTags返回：{}", result);
	}
	
	public WxDeptInfo getWxDeptInfo(String token, String wxDeptId) {
		String result = deptInfoClient.getDept(token, wxDeptId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			WxDeptInfo info = new WxDeptInfo();
			info.setId(json.get("department").get("id").asInt());
			info.setName(json.get("department").get("name").asText());
			return info;
		} else {
			logger.warn("--->接口（getDept）通信异常！");
			return null;
		}
	}

	public WxUserInfo getWxUserInfo(String token, String wxUserId) {
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
	
	public WxCustContact getWxCustomerInfo(String customerId) {
		String token = getAccessToken("ww5b77b727717ccd72");
		return getWxCustomerInfo(token, customerId);
	}
	
	public WxCustContact getWxCustomerInfo(String token, String customerId) {
		String result = customerClient.getCustomer(token, customerId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			return UtilJson.json2Object(json.get("external_contact").toString(), WxCustContact.class);
		} else {
			logger.warn("--->接口（getCustomer）通信异常！");
			return null;
		}
	}

}
