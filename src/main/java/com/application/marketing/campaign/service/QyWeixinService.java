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
import com.application.marketing.common.controller.vo.WXTokenVO;
import com.application.marketing.common.controller.vo.WxUserInfo;
import com.application.marketing.common.domain.ThirdAccount;
import com.application.marketing.common.repository.ThirdAccountDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.CorpTagClient;
import com.thirdparty.eweixin.CustAcquisitionClient;
import com.thirdparty.eweixin.CustomerClient;
import com.thirdparty.eweixin.DeptInfoClient;
import com.thirdparty.eweixin.QiyeWeixinClient;
import com.thirdparty.eweixin.UserInfoClient;
import com.thirdparty.params.WxCustContact;
import com.thirdparty.params.WxDeptInfo;

@Service
public class QyWeixinService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ThirdAccountDao thirdAccountDao;

	@Autowired
	MarketingDao marketingDao;
	
	CustomerClient customerClient = new CustomerClient();
	
	UserInfoClient userInfoClient = new UserInfoClient();
	
	DeptInfoClient deptInfoClient = new DeptInfoClient();
	
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
