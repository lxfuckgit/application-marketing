package com.application.marketing.campaign.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.repository.MarketingClueDao;
import com.application.marketing.campaign.repository.MarketingDao;
import com.thirdparty.params.WxCustContact;

@Service
public class MarketingClueService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MarketingDao marketingRepository;
	@Autowired
	MarketingClueDao marketingClueDao;
	@Autowired
	QyWeixinService qyWeixinService;

	/**
	 * 
	 * @param marketingId
	 * @param userId
	 * @param extUserId
	 * @return
	 */
	public Long createMarketingClue(MarketingClue clue) {
		if (null == clue.getMarketingId() || StringUtils.isBlank(clue.getUserId()) || StringUtils.isBlank(clue.getExtUserId())) {
			logger.error("--->[{}]营销记录必要数据缺失!", clue.getMarketingId());
			return 0L;
		}
		java.util.Optional<Marketing> optional = marketingRepository.findById(clue.getMarketingId());
		if (optional.isEmpty()) {
			logger.error("--->[{}]营销记录不存在!", clue.getMarketingId());
			return 0L;
		} else {
			clue.setAdAccount(optional.get().getAdAccount());
		}
		
		// 验证数据有效性
		MarketingClue oldClue = marketingClueDao.findByMarketingIdAndUserIdAndExtUserId(clue.getMarketingId(), clue.getUserId(), clue.getExtUserId());
		if(null != oldClue) {
			logger.warn("--->当前营销线索重复，系统无法二次处理！");
			return 0L;
		}
		
		WxCustContact customer = qyWeixinService.getWxCustomerInfo(clue.getExtUserId());
		if (null != customer) {
			// 将[微信昵称]或[企微别名]当线索名称
			clue.setExtUserName(customer.getName());
		}
		marketingClueDao.save(clue);
//		logger.info("--->营销线索保存完毕！");
		return 1L;
	}
	
}
