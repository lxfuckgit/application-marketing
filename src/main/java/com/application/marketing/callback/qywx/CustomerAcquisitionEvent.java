package com.application.marketing.callback.qywx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.marketing.campaign.service.CampaignService;
import com.javapai.framework.enums.StatusEnum;

/**
 * 获客助手事件通知
 */
@Component
public class CustomerAcquisitionEvent {
	/**/
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CampaignService campaignService;

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
	public void eventFriendRequest(String linkId) {
		logger.info("--->处理回调事件：eventFriendRequest参数：{}", linkId);
	}

}
