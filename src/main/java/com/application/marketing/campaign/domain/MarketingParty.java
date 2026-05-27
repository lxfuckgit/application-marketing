package com.application.marketing.campaign.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

/**
 * 营销活动相关人员<br>
 */
@Entity
@Table(name = "marketing_party")
public class MarketingParty extends TopBaseDomain {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 营销活动ID
	 */
	@Column(name = "marketing_id", length = 10, nullable = false)
	private Long marketingId;

	/**
	 * 营销关联人员ID
	 */
	@Column(name = "party_id", length = 32, nullable = false)
	private String partyId;

	/**
	 * 人类类型
	 */
	@Column(name = "party_type", length = 2, nullable = false)
	private Integer partyType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMarketingId() {
		return marketingId;
	}

	public void setMarketingId(Long marketingId) {
		this.marketingId = marketingId;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Integer getPartyType() {
		return partyType;
	}

	public void setPartyType(Integer partyType) {
		this.partyType = partyType;
	}

}
