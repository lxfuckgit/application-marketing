package com.application.marketing.campaign.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.campaign.domain.MarketingClue;

public interface MarketingClueDao extends PagingAndSortingRepository<MarketingClue, Long> {
	MarketingClue findByMarketingIdAndUserIdAndExtUserId(Long marketingId, String userId, String extUserId);

}
