package com.application.marketing.common.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.common.domain.QywxUser;

public interface QywxUserDao extends PagingAndSortingRepository<QywxUser, Long> {
	QywxUser findByUserId(String userId);

}
