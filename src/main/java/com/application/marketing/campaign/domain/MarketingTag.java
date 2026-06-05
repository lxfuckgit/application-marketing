package com.application.marketing.campaign.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

/**
 * 营销活动相关标签<br>
 */
@Entity
@Table(name = "marketing_tag")
public class MarketingTag extends TopBaseDomain {
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
	 * 标签类型
	 */
	@Column(name = "tag_type", length = 30)
	private Integer tagType;

	/**
	 * 营销关联标签
	 */
	@Column(name = "tag_value", length = 32, nullable = false)
	private String tagValue;

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

	public Integer getTagType() {
		return tagType;
	}

	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

}
