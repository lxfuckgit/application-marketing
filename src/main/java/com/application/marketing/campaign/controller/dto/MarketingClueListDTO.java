package com.application.marketing.campaign.controller.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javapai.framework.common.page.BasePageArgs;
import com.javapai.framework.utils.UtilDateTime;

public class MarketingClueListDTO extends BasePageArgs {
	/**
	 * 曝光标识
	 */
	private String exposure‌Id;
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
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = UtilDateTime.FORMAT_DATE_TIME1, timezone = "GMT+8")
	private LocalDateTime[] createTime;
	
	public String getExposure‌Id() {
		return exposure‌Id;
	}

	public void setExposure‌Id(String exposure‌Id) {
		this.exposure‌Id = exposure‌Id;
	}

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

	public LocalDateTime[] getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime[] createTime) {
		this.createTime = createTime;
	}
	
}
