package com.application.marketing.callback.qywx;

import com.application.marketing.common.controller.vo.ChatCountVO;
import com.application.marketing.common.domain.QywxFriend;
import com.application.marketing.common.repository.QywxFriendDao;
import com.application.marketing.common.service.QywxAcquisitionService;
import com.application.marketing.component.DataNotice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.repository.MarketingClueDao;
import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.campaign.repository.MarketingTagDao;
import com.application.marketing.campaign.service.CampaignService;
import com.application.marketing.campaign.service.MarketingClueService;
import com.javapai.framework.enums.StatusEnum;

/**
 * 获客助手事件通知
 */
@Component
public class CustomerAcquisitionEvent {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CampaignService campaignService;
	
	@Autowired
	MarketingClueService marketingClueService;

	@Autowired
	MarketingDao marketingDao;
	
	@Autowired
	MarketingTagDao marketingTagDao;
	
	@Autowired
	MarketingClueDao marketingClueDao;
	
	@Autowired
	QywxFriendDao qywxFriendDao;
	
	@Autowired
	DataNotice dataNotice;
	
	@Autowired
	QywxAcquisitionService qywxAcquisitionService;

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
	 * 
	 * <strong>官方解释：</strong>当微信用户通过获客链接点击添加到通讯录，成功发起好友请求，回调此事件到创建该链接的应用。<br>
	 * <strong>重点提示：</strong>当前【获客链接】如果关闭[添加成员时需要验证]选项时，【微信用户】将直接添加【企业用户】成友好友并触发add_external_contact事件。<br>
	 */
	public void eventFriendRequest(String linkId, String state) {
		logger.info("--->处理回调事件：eventFriendRequest参数：{}/{}", linkId, state);
		QywxFriend frend = qywxFriendDao.findByLinkIdAndState(linkId, state);
		if (null == frend) {
			frend = new QywxFriend(linkId, state);
			// 数据同步
			dataNotice.doDateNotice(state, 0);
		} else {
			logger.info("--->FriendRequest二次请求参数：{}/{}", linkId, state);
		}
		qywxFriendDao.save(frend);
	}
	
	/**
	 * 【获客链接】成员首次收外部用户的消息事件。<br>
	 * <strong>官方解释：</strong>授权企业中配置了客户联系功能的[内部成员]通过获客链接添加微信客户后，当成员首次收到客户消息时，回调此事件到创建相应链接的应用。。
	 * 
	 * @param linkId    获客链接标识。
	 * @param userId    内部用户标识。
	 * @param extUserId 外部用户标识。
	 */
	public void eventCustomerStartChat(String linkId, String userId, String extUserId) {
		logger.info("--->处理回调事件：eventCustomerStartChat参数：{}-{}-{}", linkId, userId, extUserId);
		
		// 检查linkId有效性
		Marketing marketing = marketingDao.findByTypeAndExtid(Marketing.TYPE_8, linkId);
		if (null == marketing) {
			logger.warn("--->异常：外部数据标识（{}）异常！", linkId);
			return;
		}
		
		/* 此事件绑定业务1：更新线索数据[相互接收消息统计] */
		MarketingClue clue = marketingClueDao.findByMarketingIdAndUserIdAndExtUserId(marketing.getId(), userId, extUserId);
		if (null == clue) {
			clue = new MarketingClue();
			clue.setMarketingId(marketing.getId());
			clue.setUserId(userId);
			clue.setExtUserId(extUserId);
			clue.setRecvMsgCnt(1);
			Long r = marketingClueService.createMarketingClue(clue);
			logger.info("--->提示：当前线索归档结果：{}", r);
		} else {
			clue.setRecvMsgCnt(1);
			marketingClueDao.save(clue);
			logger.info("--->提示：当前线索首次开口归档！");
		}
		
		/* 此事件绑定业务2：时实同步数据[先不考虑耦合性-后期优化] */
		if (null != clue.getExposureId()) {
			dataNotice.doDateNotice(clue.getExposureId(), 2);
		}
	}
	
	/**
	 * 【获客链接】成员多次收外部用户的消息事件。<br>
	 * 
	 * @param linkId
	 * @param userId
	 * @param extUserId
	 * @param chatKey
	 */
	public void eventMessageFromCustomer(String linkId, String chatKey) {
		logger.info("--->处理回调事件：eventMessageFromCustomer参数：{}", linkId);
		
		// 检查linkId有效性
		Marketing marketing = marketingDao.findByTypeAndExtid(Marketing.TYPE_8, linkId);
		if (null == marketing) {
			logger.warn("--->异常：外部数据标识（{}）异常！", linkId);
			return;
		}
		
		// 同步会话数量并更新会话数量
		ChatCountVO chatCount = qywxAcquisitionService.getChatInfo(marketing.getAppId(), chatKey);
		if (null == chatCount) {
			logger.warn("--->异常：成员消息数据读取异常！");
			return;
		}
		String userId = chatCount.getUserId();
		String extUserId = chatCount.getExtUserId();
		MarketingClue clue = marketingClueDao.findByMarketingIdAndUserIdAndExtUserId(marketing.getId(), userId, extUserId);
		if (null == clue) {
			clue = new MarketingClue();
			clue.setMarketingId(marketing.getId());
			clue.setUserId(userId);
			clue.setExtUserId(extUserId);
			clue.setAdAccount(marketing.getAdAccount());
			clue.setChatKey(chatKey);
			clue.setRecvMsgCnt(chatCount.getRecvMsgCnt());
			marketingClueDao.save(clue);
		} else {
			clue.setChatKey(chatKey);
			clue.setRecvMsgCnt(chatCount.getRecvMsgCnt());
			marketingClueDao.save(clue);
		}
		logger.info("--->事件eventMessageFromCustomer会话（{}-{}-{}）凭据更新完毕！", linkId, userId, extUserId);
		
		// 时实同步数据[先不考虑耦合性-后期优化]
		if (null != clue.getExposureId()) {
			dataNotice.doDateNotice(clue.getExposureId(), chatCount.getRecvMsgCnt());
		}
	}
}
