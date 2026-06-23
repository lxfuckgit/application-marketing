package com.application.marketing.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javapai.framework.common.domain.TopBaseDomain;

/**
 * 第三方托管账号信息。
 * 
 * @author pooja
 *
 */
@Entity
@Table(name = "thrid_account")
public class ThirdAccount extends TopBaseDomain {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 托管账号标识
	 */
	@Column(name = "app_id", length = 32, nullable = false)
	private String appId;

	/**
	 * 托管账号密钥
	 */
	@Column(name = "app_secret", length = 64, nullable = false)
	private String appSecret;

	/**
	 * 访问令牌
	 */
	@Column(name = "access_token", length = 256)
	private String accessToken;

	/**
	 * 令牌有效期(13位的毫秒数）<br>
	 * 采用基本类型以保证其默认值=0
	 */
	@Column(name = "expires_time")
	private long expiresTime;

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

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}

}
