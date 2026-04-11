package com.application.marketing.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.domain.ThirdAccount;

public interface ThirdAccountDao extends PagingAndSortingRepository<ThirdAccount, Long> {
	ThirdAccount findByAppId(String appId);

}
