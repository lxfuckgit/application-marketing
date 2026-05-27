package com.application.marketing.campaign.controller.dto;

import com.javapai.framework.common.page.BasePageArgs;

public class MarketingListDTO extends BasePageArgs {
	/**
	 * 排序方式（按某字段排序）
	 */
	private String sortBy;

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

}
