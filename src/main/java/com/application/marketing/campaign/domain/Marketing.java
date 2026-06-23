package com.application.marketing.campaign.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

/**
 * 营销活动<br>
 */
@Entity
@Table(name = "marketing")
public class Marketing extends TopBaseDomain {
	/**
	 * 企微-获客链接
	 */
	public static final Integer TYPE_8 = 8;

	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 应用标识
	 */
	@Column(name = "app_id", length = 32, nullable = false)
	private String appId;
	/**
	 * 营销活动分类
	 */
	@Column(name = "type", length = 2, nullable = false)
	private Integer type;
	/**
	 * 营销活动名称
	 */
	@Column(name = "name", length = 32, nullable = false)
	private String name;
	/**
	 * 营销活动总结（摘要）
	 */
	@Column(name = "summary")
	private String summary;
	/**
	 * 营销投放渠道
	 */
	@Column(name = "channel", length = 32)
	private String channel;
	/**
	 * 营销活动链接
	 */
	@Column(name = "link", length = 255)
	private String link;
	/**
	 * 营销活动关联广告
	 */
	@Column(name = "ad_account", length = 32)
	private String adAccount;
	/**
	 * 营销活动状态
	 */
	@Column(name = "status", length = 32, nullable = false)
	private String status;
	/**
	 * 外部活动标识
	 */
	@Column(name = "extid", length = 32)
	private String extid;
	/**
	 * 创建人
	 */
	@Column(name = "create_id", length = 32, nullable = false)
	private String createId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAdAccount() {
		return adAccount;
	}

	public void setAdAccount(String adAccount) {
		this.adAccount = adAccount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExtid() {
		return extid;
	}

	public void setExtid(String extid) {
		this.extid = extid;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

}
