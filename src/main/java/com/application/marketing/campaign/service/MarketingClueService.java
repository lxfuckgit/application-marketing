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
	public Long createMarketingClue(Long marketingId, String userId, String extUserId) {
		if (null == marketingId || StringUtils.isBlank(userId) || StringUtils.isBlank(extUserId)) {
			return 0L;
		}
		java.util.Optional<Marketing> optional = marketingRepository.findById(marketingId);
		if (optional.isEmpty()) {
			logger.error("--->[{}]营销记录不存在!", marketingId);
			return 0L;
		}

		MarketingClue clue = marketingClueDao.findByMarketingIdAndUserIdAndExtUserId(marketingId, userId, extUserId);
		if (null == clue) {
			clue = new MarketingClue();
			clue.setMarketingId(marketingId);
			clue.setAdAccount(optional.get().getAdAccount());
			clue.setUserId(userId);
			clue.setExtUserId(extUserId);
			WxCustContact customer = qyWeixinService.getWxCustomerInfo(extUserId);
			if (null != customer) {
				// 将[微信昵称]或[企微别名]当线索名称
				clue.setExtUserName(customer.getName());
			}
			marketingClueDao.save(clue);
		} else {
			logger.warn("--->当前营销线索重复，系统无法二次处理！");
			return 0L;
		}
		return 1L;
	}

}
