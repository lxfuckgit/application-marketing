package com.application.marketing.campaign.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.campaign.domain.Marketing;

public interface MarketingDao extends PagingAndSortingRepository<Marketing, Long> {
	Marketing findByExtid(String extid);
	
	Marketing findByTypeAndExtid(Integer type, String extid);

}
