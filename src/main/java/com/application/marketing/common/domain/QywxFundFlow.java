package com.application.marketing.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qywx_fund_flow")
public class QywxFundFlow {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 动账时间（秒级）
	 */
	private Long timestamp;

	/**
	 * 关联单号，即微信支付资金流水单号
	 */
	@Column(name = "request_no", length = 32, nullable = false)
	private String requestNo;

	/**
	 * 动账类型。1：退款，2：交易手续费，3：收款，4：提现，5：其他
	 */
	@Column(name = "transaction_type", length = 3)
	private Integer transactionType;

	/**
	 * 收支类型。1：收入，2：支出
	 */
	@Column(name = "fund_flow_type", length = 3)
	private Integer fundFlowType;

	/**
	 * 动账金额，整型数据，单位：分
	 */
	@Column(name = "transaction_amount", length = 3)
	private Integer transactionAmount;

	/**
	 * 账户余额，整型数据，单位：分
	 */
	@Column(name = "account_balance", length = 3)
	private Integer accountBalance;

	/**
	 * 商户单号，即业务凭证号
	 */
	@Column(name = "out_trade_no", length = 64, nullable = false)
	private String outTradeNo;

	/**
	 * 商户号ID
	 */
	@Column(name = "mch_id", length = 32)
	private String mchId;

	/**
	 * 操作人用户ID
	 */
	@Column(name = "operator_userid", length = 64)
	private String operatorUserid;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public Integer getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Integer transactionType) {
		this.transactionType = transactionType;
	}

	public Integer getFundFlowType() {
		return fundFlowType;
	}

	public void setFundFlowType(Integer fundFlowType) {
		this.fundFlowType = fundFlowType;
	}

	public Integer getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Integer transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Integer getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Integer accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getOperatorUserid() {
		return operatorUserid;
	}

	public void setOperatorUserid(String operatorUserid) {
		this.operatorUserid = operatorUserid;
	}

}
