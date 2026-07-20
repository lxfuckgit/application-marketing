package com.application.marketing.common.controller.vo;

public class ChatCountVO {
	private String linkId;
	private String state;
	private String userId;
	private String extUserId;
	private Integer recvMsgCnt;

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

	public Integer getRecvMsgCnt() {
		return recvMsgCnt;
	}

	public void setRecvMsgCnt(Integer recvMsgCnt) {
		this.recvMsgCnt = recvMsgCnt;
	}

}
