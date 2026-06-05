package com.application.marketing.campaign.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.campaign.controller.dto.MarketingClueListDTO;
import com.application.marketing.campaign.domain.Marketing;
import com.application.marketing.campaign.domain.MarketingClue;
import com.application.marketing.campaign.repository.MarketingClueDao;
import com.application.marketing.campaign.repository.MarketingDao;
import com.application.marketing.campaign.service.CampaignService;
import com.application.marketing.campaign.service.QyWeixinService;
import com.fasterxml.jackson.databind.JsonNode;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.thirdparty.params.WxCustContact;

@RestController
@RequestMapping("/marketing")
public class CampaignClueController {
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	MarketingDao marketingRepository;
	
	@Autowired
	MarketingClueDao marketingClueDao;

	@Autowired
	QyWeixinService qyWeixinService;
	
	/**
	 * 查询营销线索（分页）
	 */
	@RequestMapping("/pageCampaignClue")
	public PageResult<MarketingClue> pageCampaignClue(@RequestBody MarketingClueListDTO dto) {
		return campaignService.pageCampaignClue(dto);
	}

	/**
	 * 
	 * @param marketingId
	 * @return
	 */
	@RequestMapping("/syncCampaingnClueInfo")
	public RstResult<Void> syncCampaingnClueInfo(@RequestParam Long marketingId) {
		// 先检查是否存在
		java.util.Optional<Marketing> optional = marketingRepository.findById(marketingId);
		if (optional.isEmpty()) {
			throw new RuntimeException("营销记录不存在，ID: " + marketingId);
		}

		List<JsonNode> result = qyWeixinService.syncQiyeWeixinLinkClueList(optional.get().getExtid());
		result.forEach(action -> {
			syncCampaingnClueInfo(marketingId, optional.get().getAdAccount(), action);
		});
		return ResultBuilder.normalResult();
	}

	private void syncCampaingnClueInfo(Long marketingId,String adAccount, JsonNode userList) {
		String token = qyWeixinService.getAccessToken("ww5b77b727717ccd72");
		userList.forEach(action -> {
			String userId = action.get("userid").asText();
			String extUserId = action.get("external_userid").asText();
			MarketingClue mc = marketingClueDao.findByMarketingIdAndUserIdAndExtUserId(marketingId, userId, extUserId);
			if (null == mc) {
				mc = new MarketingClue();
				mc.setMarketingId(marketingId);
				mc.setAdAccount(adAccount);
				mc.setUserId(userId);
				mc.setExtUserId(extUserId);
				WxCustContact customer = qyWeixinService.getWxCustomerInfo(token, extUserId);
				if (null != customer) {
					// 将[微信昵称]或[企微别名]当线索名称
					mc.setExtUserName(customer.getName());
				}
				marketingClueDao.save(mc);
			}
		});
	}
	
}
