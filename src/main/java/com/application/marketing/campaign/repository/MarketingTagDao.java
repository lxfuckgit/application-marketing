package com.application.marketing.campaign.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.campaign.domain.MarketingTag;

public interface MarketingTagDao extends PagingAndSortingRepository<MarketingTag, Long> {
	List<MarketingTag> findByMarketingId(Long marketingId);

}
