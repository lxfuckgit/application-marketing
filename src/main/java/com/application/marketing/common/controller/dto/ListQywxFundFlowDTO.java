package com.application.marketing.common.controller.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javapai.framework.common.page.BasePageArgs;

public class ListQywxFundFlowDTO extends BasePageArgs {
	private String appId;
	/**
	 * 动账-开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateFrom;
	/**
	 * 动账-结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateTo;
	/**
	 * 收支类型(1：收入，2：支出)
	 */
	private Integer flowType;
	/**
	 * 动账类型(1：退款，2：交易手续费，3：收款，4：提现，5：其他)
	 */
	private Integer transactionType;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getFlowType() {
		return flowType;
	}

	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}

	public Integer getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}

}
