package com.application.marketing.campaign.controller.dto;

import java.util.List;

public class MarketingCreateDTO {
	private Long userId;
	/**
	 * 活动类型
	 */
	private Integer type;
	/**
	 * 活动名称
	 */
	private String name;
	/**
	 * 活动服务人员（例如：客服）
	 */
	private List<String> staffList;
	/**
	 * 活动关联的广告账户
	 */
	private String adAccount;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public List<String> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<String> staffList) {
		this.staffList = staffList;
	}

	public String getAdAccount() {
		return adAccount;
	}

	public void setAdAccount(String adAccount) {
		this.adAccount = adAccount;
	}

}
