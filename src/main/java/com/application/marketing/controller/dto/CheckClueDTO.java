package com.application.marketing.controller.dto;

public class CheckClueDTO {
	private String appId;
	/**
	 * 客服绑在企微的电话
	 */
	private String staffMobile;
	/**
	 * 客人名称/客人昵称
	 */
	private String customerName;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getStaffMobile() {
		return staffMobile;
	}

	public void setStaffMobile(String staffMobile) {
		this.staffMobile = staffMobile;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

}
