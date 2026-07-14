package com.application.marketing.campaign.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.application.marketing.campaign.domain.MarketingParty;

public interface MarketingPartyDao extends PagingAndSortingRepository<MarketingParty, Long> {

	@Query("SELECT mp FROM MarketingParty mp WHERE mp.marketingId = :marketingId")
	public List<MarketingParty> listByMarketingId(@Param("marketingId") Long marketingId);

	@Query("SELECT mp.partyId FROM MarketingParty mp WHERE mp.marketingId = :marketingId")
	public List<String> listPartyIdByMarketingId(@Param("marketingId") Long marketingId);

}
