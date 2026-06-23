package com.application.marketing.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qywx_dept")
public class QywxDept {
	@Id
	@Column(name = "id", length = 10)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "app_id", length = 32, nullable = false)
	private String appId;

	@Column(name = "parent_id", length = 10)
	private Long parentId;

	@Column(name = "ext_dept_id", length = 32, nullable = false)
	private String extDeptId;

	@Column(name = "dept_name", length = 30, nullable = false)
	private String deptName;

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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getExtDeptId() {
		return extDeptId;
	}

	public void setExtDeptId(String extDeptId) {
		this.extDeptId = extDeptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

}
