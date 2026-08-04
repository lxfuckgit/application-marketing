package com.application.marketing.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qywx_user")
public class QywxUser {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "app_id", length = 32, nullable = false)
	private String appId;

	/**
	 * 企微部门ID（默认0）
	 */
	@Column(name = "dept_id", length = 32, nullable = false)
	private String deptId;

//	@Column(name = "ext_id", length = 32)
//	private String extId;
	/**
	 * 企微成员ID
	 */
	@Column(name = "user_id", length = 32, nullable = false)
	private String userId;
	/**
	 * 企微成员名称
	 */
	@Column(name = "user_name", length = 30, nullable = false)
	private String userName;
	/**
	 * 企微成员昵称
	 */
	@Column(name = "nick_name", length = 60, nullable = false)
	private String nickName;
	/**
	 * 企微成员手机号
	 */
	@Column(name = "user_mobile", length = 12)
	private String userMobile;

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

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

}
