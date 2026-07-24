package com.application.marketing.common.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.marketing.common.domain.QywxDept;
import com.application.marketing.common.repository.QywxDeptDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.DeptInfoClient;
import com.thirdparty.params.WxDeptInfo;

/**
 * 企业微信-通讯录管理-部门管理。<br>
 */
@Service
public class QywxDeptService extends QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	QywxDeptDao qywxDeptDao;

	DeptInfoClient deptInfoClient = new DeptInfoClient();

	/**
	 * 数据同步-同步企业关联部门。<br>
	 * 
	 * @param appId
	 */
	public void syncDepartyment(String appId) {
		String token = getAccessToken(appId);
		String result = deptInfoClient.listDept(token, null);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			json.get("department_id").forEach(dept -> {
				WxDeptInfo info = getWxDeptInfo(token, dept.get("id").asText());
				if (null == info) {
					logger.warn("--->接口（getDept）通信异常!");
				}
				// 同步处理本地数据
				QywxDept entity = qywxDeptDao.findByAppIdAndExtDeptId(appId, dept.get("id").asText());
				if (null == entity) {
					entity = new QywxDept();
					entity.setExtDeptId(dept.get("id").asText());
				}
				entity.setAppId(appId);
				if (null != info.getParentid()) {
					entity.setParentId(Long.valueOf(info.getParentid()));
				}
				entity.setDeptName(info.getName());
				qywxDeptDao.save(entity);
			});
		} else {
			logger.warn("--->接口（listDept）通信异常，返回信息：{}", json.get("errmsg").asText());
		}
	}

	/**
	 * 
	 * @param appId
	 * @return
	 */
	public List<WxDeptInfo> listDepartyment(String appId) {
		String token = getAccessToken(appId);
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

	private WxDeptInfo getWxDeptInfo(String token, String wxDeptId) {
		String result = deptInfoClient.getDept(token, wxDeptId);
		JsonNode json = UtilJson.json2Object(result);
		if (0 == json.get("errcode").intValue()) {
			WxDeptInfo info = new WxDeptInfo();
			info.setId(json.get("department").get("id").asInt());
			info.setName(json.get("department").get("name").asText());
			info.setParentid(json.get("department").get("parentid").asInt());
			info.setOrder(json.get("department").get("name").asInt());
			return info;
		} else {
			logger.warn("--->接口（getDept）通信异常！");
			return null;
		}
	}

}
