package com.application.marketing.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

@Entity
@Table(name = "message_group")
public class MessageGroup extends TopBaseDomain {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	/**
	 * 组名称
	 */
	@Column(name = "group_name", length = 128, nullable = false)
	private String groupName;
	/**
	 * 组通道（local本地消息；qyweixin企业微信群组）
	 */
	@Column(name = "group_channel", length = 32, nullable = false)
	private String groupChannel;

	/**
	 * 外部组标识
	 */
	@Column(name = "ext_id", length = 64)
	private String extId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupChannel() {
		return groupChannel;
	}

	public void setGroupChannel(String groupChannel) {
		this.groupChannel = groupChannel;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

}
