package com.application.marketing.campaign.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

/**
 * 营销活动相关线索<br>
 */
@Entity
@Table(name = "marketing_clue")
public class MarketingClue extends TopBaseDomain {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	/**
	 * 营销标识
	 */
	@Column(name = "marketing_id", length = 10, nullable = false)
	private Long marketingId;
	/**
	 * 广告账户(冗余设计-防止业务端中途变更账户的情况）
	 */
	@Column(name = "ad_account", length = 32, nullable = false)
	private String adAccount;
	/**
	 * 内部用户标识(对接人）
	 */
	@Column(name = "user_id", length = 32, nullable = false)
	private String userId;
	/**
	 * 外部用户标识（线索用户标识）
	 */
	@Column(name = "ext_user_id", length = 32)
	private String extUserId;
	/**
	 * 外部用户名称（线索用户名称）
	 */
	@Column(name = "ext_user_name", length = 30, nullable = false)
	private String extUserName;
	/**
	 * 外部用户标签（空格分隔）
	 */
	@Column(name = "ext_user_tag", length = 30)
	private String extUserTag;

	/**
	 * 外部会话凭据
	 */
	@Column(name = "chat_key", length = 100)
	private String chatKey;

	/**
	 * 成员收到的此客户的消息次数
	 */
	@Column(name = "recv_msg_cnt", length = 11)
	private Integer recvMsgCnt;

	/**
	 * 营销序列号（每个营销活动每次营销曝光都会产生一个序列号）
	 */
	@Column(name = "exposure‌_id", length = 32)
	private String exposureId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMarketingId() {
		return marketingId;
	}

	public void setMarketingId(Long marketingId) {
		this.marketingId = marketingId;
	}

	public String getAdAccount() {
		return adAccount;
	}

	public void setAdAccount(String adAccount) {
		this.adAccount = adAccount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getExtUserTag() {
		return extUserTag;
	}

	public void setExtUserTag(String extUserTag) {
		this.extUserTag = extUserTag;
	}

	public String getChatKey() {
		return chatKey;
	}

	public void setChatKey(String chatKey) {
		this.chatKey = chatKey;
	}

	public Integer getRecvMsgCnt() {
		return recvMsgCnt;
	}

	public void setRecvMsgCnt(Integer recvMsgCnt) {
		this.recvMsgCnt = recvMsgCnt;
	}

	public String getExposureId() {
		return exposureId;
	}

	public void setExposureId(String exposureId) {
		this.exposureId = exposureId;
	}

}
