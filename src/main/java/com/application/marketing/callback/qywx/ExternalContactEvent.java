package com.application.marketing.callback.qywx;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingTag;
import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.campaign.repository.MarketingTagDao;
import com.application.marketing.campaign.service.MarketingClueService;
import com.application.marketing.campaign.service.QyWeixinService;

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
	MarketingClueService marketingClueService;

	/**
	 * 外部联系人免验证添加成员事件。<br>
	 * <strong>官方解释：</strong>外部联系人添加了配置了【客户联系】功能且开启了免验证的成员时（此时成员尚未确认添加对方为好友），回调该事件。<br>
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
	 * @param linkId
	 * @param userId
	 * @param extUserId
	 */
	public void eventAddExternalContact(String linkId, String userId, String extUserId) {
		logger.info("--->处理回调事件：eventAddExternalContact参数：{}-{}-{}", linkId, userId, extUserId);
		/* 本地数据检查 */
		Marketing marketing = marketingRepository.findByExtid(linkId);
		if (null == marketing) {
			logger.warn("--->异常：非法数据标识（{}）！", linkId);
			return;
		}

		/* 此事件绑定业务1：关联[外部用户]到线索数据 */
		Long r = marketingClueService.createMarketingClue(marketing.getId(), userId, extUserId);
		logger.info("--->提示：当前线索归档结果：{}", r);

		/* 此事件绑定业务2：给[外部用户]打标签 */
		List<MarketingTag> tagList = marketingTagDao.findByMarketingId(marketing.getId());
		if (null == tagList) {
			logger.warn("--->异常：当前数据（{}）无关联标签！", linkId);
			return;
		}
		qywxService.addCustTags(userId, extUserId, tagList.stream().map(MarketingTag::getTagValue).collect(Collectors.toList()));
		logger.info("--->提示：外部用户标签完成！");
	}

}
