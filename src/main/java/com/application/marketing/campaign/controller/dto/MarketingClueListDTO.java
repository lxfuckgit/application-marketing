package com.application.marketing.campaign.controller.dto;

import com.javapai.framework.common.page.BasePageArgs;

public class MarketingClueListDTO extends BasePageArgs {
	/**
	 * 线索名称
	 */
	private String clueName;
	/**
	 * 内部用户标识
	 */
	private String intUserId;
	/**
	 * 外部用户标识
	 */
	private String extUserId;
	/**
	 * 外部用户名称
	 */
	private String extUserName;

	public String getClueName() {
		return clueName;
	}

	public void setClueName(String clueName) {
		this.clueName = clueName;
	}

	public String getIntUserId() {
		return intUserId;
	}

	public void setIntUserId(String intUserId) {
		this.intUserId = intUserId;
	}

	public String getExtUserId() {
		return extUserId;
	}

	public void setExtUserId(String extUserId) {
		this.extUserId = extUserId;
	}

	public String getExtUserName() {
		return extUserName;
	}

	public void setExtUserName(String extUserName) {
		this.extUserName = extUserName;
	}

}
