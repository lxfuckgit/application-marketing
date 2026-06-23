package com.application.marketing.common.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.common.domain.ThirdAccount;

public interface ThirdAccountDao extends PagingAndSortingRepository<ThirdAccount, Long> {
	ThirdAccount findByAppId(String appId);

}
