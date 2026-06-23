package com.application.marketing.common.controller.dto;

import com.javapai.framework.common.page.BasePageArgs;

public class ListQywxUserDTO extends BasePageArgs {
	private String appId;
	private String deptId;
	/**
	 * 企业微信名称/企业微信昵称
	 */
	private String nickName;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
