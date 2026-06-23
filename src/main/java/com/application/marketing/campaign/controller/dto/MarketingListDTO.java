package com.application.marketing.campaign.controller.dto;

import com.javapai.framework.common.page.BasePageArgs;

public class MarketingListDTO extends BasePageArgs {
	private String appId;
	private Integer type;
	private String name;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
