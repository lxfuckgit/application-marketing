package com.application.marketing.common.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.application.marketing.common.domain.MessageGroup;

public interface MessageGroupDao extends PagingAndSortingRepository<MessageGroup, Long> {
	MessageGroup findByExtId(String extid);

}
