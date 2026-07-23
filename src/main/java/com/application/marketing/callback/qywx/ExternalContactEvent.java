package com.application.marketing.callback.qywx;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.domain.MarketingTag;
import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.campaign.repository.MarketingTagDao;
import com.application.marketing.campaign.service.MarketingClueService;
import com.application.marketing.campaign.service.QyWeixinService;
import com.application.marketing.common.domain.QywxFriend;
import com.application.marketing.common.repository.QywxFriendDao;
import com.application.marketing.common.service.QywxCropTagService;
import com.application.marketing.component.DataNotice;

/**
 * 外部联系事件通知
 */
@Component
public class ExternalContactEvent {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MarketingDao marketingRepository;
	
	@Autowired
	MarketingTagDao marketingTagDao;
	
	@Autowired
	QyWeixinService qywxService;
	
	@Autowired
	QywxFriendDao qywxFriendDao;
	
	@Autowired
	QywxCropTagService cropTagService;
	
	@Autowired
	MarketingClueService marketingClueService;
	
	@Autowired
	DataNotice dataNotice;
	
	/**
	 * 外部联系人免验证添加成员事件。<br>
	 * <strong>官方解释：</strong>外部联系人添加了配置了【客户联系】功能且开启了免验证的成员时（此时成员尚未确认添加对方为好友），回调该事件。<br>
	 * 
	 * <strong>重点提示：</strong>此事件产生于[非免验证的添加]场景，此事件并非获客链接的专属事件，也有企业员工主动扫码添加客户、客户通过员工的名片添加、个人二维码添加等。<br>
	 * 
	 * @param linkId
	 * @param userId
	 * @param extUserId
	 */
	public void eventAddHalfExternalContact(String linkId, String userId, String extUserId) {
		logger.info("--->处理回调事件：eventAddHalfExternalContact参数：{}-{}-{}", linkId, userId, extUserId);

	}

	/**
	 * 添加企业客户事件。<br>
	 * <strong>官方解释：</strong>配置了客户联系功能的成员添加外部联系人时，回调该事件。<br>
	 * 
	 * <strong>重点提示：</strong>此事件的触发时机为[客户与成员成为双向好友]。但添加方式存在多样性（不限于获客链接），无论是企业员工主动扫码添加客户，还是客户通过员工的名片、个人二维码等添加，只要双方成为双向好友，都会触发此事件。<br>
	 * 
	 * @param appId
	 * @param userId
	 * @param extUserId
	 * @param state
	 */
	public void eventAddExternalContact(String appId, String userId, String extUserId, String state) {
		logger.info("--->处理回调事件：eventAddExternalContact参数：{}-{}-{}", appId, userId, extUserId);
		
		/* 本地数据检查 */
//		Long marketingId = tryToFindMarketingId();
		QywxFriend friend = qywxFriendDao.findByState(state);
		if (null == friend) {
			// 非获客链接形式的好友添加，直接跳过。
			return;
		}
		
		/* 本地数据检查 */
		Marketing marketing = marketingRepository.findByExtid(friend.getLinkId());
		if (null == marketing) {
			logger.warn("--->异常：非法数据标识（{}）！", friend.getLinkId());
			return;
		}

		/* 此事件绑定业务1：关联[外部用户]到线索数据 */
		MarketingClue clue = new MarketingClue();
		clue.setMarketingId(marketing.getId());
		clue.setUserId(userId);
		clue.setExtUserId(extUserId);
		clue.setExposureId(state);
		Long r = marketingClueService.createMarketingClue(clue);
		logger.info("--->提示：当前线索归档结果：{}", r);

		/* 此事件绑定业务2：给[外部用户]打标签 */
		List<MarketingTag> tagList = marketingTagDao.findByMarketingId(marketing.getId());
		if (null == tagList) {
			logger.warn("--->异常：当前数据（{}）无关联标签！", appId);
			return;
		} else {
			cropTagService.addCustTags(appId, userId, extUserId, tagList.stream().map(MarketingTag::getTagValue).collect(Collectors.toList()));
			logger.info("--->提示：外部用户标签完成！");
		}
		
		/* 此事件绑定业务3：给系统数据通知 */
		if (null != state) {
			dataNotice.doDateNotice(state, 1);
		}
	}

}
