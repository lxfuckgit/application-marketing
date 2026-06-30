package com.application.marketing.common.controller.dto;

import java.util.List;

public class WeixinGroupCreate {
	private String groupName;
	private List<String> memberList;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<String> memberList) {
		this.memberList = memberList;
	}

}
