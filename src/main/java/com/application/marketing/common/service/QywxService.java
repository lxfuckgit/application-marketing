package com.application.marketing.common.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.marketing.common.controller.vo.WXTokenVO;
import com.application.marketing.common.domain.ThirdAccount;
import com.application.marketing.common.repository.ThirdAccountDao;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.QiyeWeixinClient;

@Service
public class QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ThirdAccountDao thirdAccountDao;
	
	QiyeWeixinClient qiyeWeixinClient = new QiyeWeixinClient();
	
	public String getAccessToken(String appId) {
		ThirdAccount ta = thirdAccountDao.findByAppId(appId);
		if (null == ta) {
			logger.warn("--->getAccessToken返回错误：appId未配置");
			return null;
		} else if (ta.getExpiresTime() > System.currentTimeMillis()) {
			return ta.getAccessToken();
		}
		/* 查询本地token */
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
		WXTokenVO vo = new WXTokenVO(resultMap.get("access_token"), Long.valueOf(resultMap.get("expires_in")));
		/* 更新地本token信息 */
		ta.setAccessToken(vo.getAccess_token());
		ta.setExpiresTime(System.currentTimeMillis() + vo.getExpires_in() * 1000);
		thirdAccountDao.save(ta);
		/* 返回最新token */
		return vo.getAccess_token();
	}
}
