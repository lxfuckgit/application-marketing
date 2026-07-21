package com.application.marketing.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

@Entity
@Table(name = "qywx_friend")
public class QywxFriend extends TopBaseDomain {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 获客链接ID
	 */
	@Column(name = "link_id", length = 32, nullable = false)
	private String linkId;

	/**
	 * 渠道标识
	 */
	@Column(name = "state", length = 32, nullable = false)
	private String state;

	public QywxFriend() {
	}

	public QywxFriend(String linkId, String state) {
		this.linkId = linkId;
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
