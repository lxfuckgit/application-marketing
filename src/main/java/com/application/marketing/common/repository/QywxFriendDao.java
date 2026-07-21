package com.application.marketing.common.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.application.marketing.common.domain.QywxFriend;

@Repository
public interface QywxFriendDao extends PagingAndSortingRepository<QywxFriend, Long> {
	QywxFriend findByLinkIdAndState(String linkId, String state);

//	@Query("SELECT qc FROM QywxFriend qc WHERE qc.linkId = :linkId and qc.userId = :userId and qc.externalUserId = :externalUserId")
//	QywxFriend findUserChatInfo(@Param("linkId") String linkId, @Param("userId") String userId, @Param("externalUserId") String externalUserId);

}
