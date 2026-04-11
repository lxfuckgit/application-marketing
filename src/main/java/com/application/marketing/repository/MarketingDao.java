package com.application.marketing.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.domain.Marketing;

public interface MarketingDao extends PagingAndSortingRepository<Marketing, Long> {
	Marketing findByExtid(String extid);

}
