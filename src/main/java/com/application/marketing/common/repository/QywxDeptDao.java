package com.application.marketing.common.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.common.domain.QywxDept;

public interface QywxDeptDao extends PagingAndSortingRepository<QywxDept, Long> {
	QywxDept findByAppIdAndExtDeptId(String appId, String extDeptId);

}
