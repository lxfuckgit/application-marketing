package com.application.marketing.campaign.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.campaign.domain.MarketingParty;

public interface MarketingPartyDao extends PagingAndSortingRepository<MarketingParty, Long> {

}
