package com.application.marketing.campaign.controller.dto;

import com.javapai.framework.common.page.BasePageArgs;

public class MarketingClueListDTO extends BasePageArgs {
	/**
	 * 线索名称
	 */
	private String clueName;

	public String getClueName() {
		return clueName;
	}

	public void setClueName(String clueName) {
		this.clueName = clueName;
	}

}
