package com.application.marketing.common.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.application.marketing.common.controller.dto.ListQywxUserDTO;
import com.application.marketing.common.domain.QywxUser;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;

@Component
public class QywxUserV2Dao extends AbstractBizService {

	public PageResult<QywxUser> listUser(ListQywxUserDTO dto) {
		/* 初始化参数 */
		List<Object> params = new ArrayList<Object>();
		params.add(dto.getAppId());
		/* 构造查询语句 */
		StringBuffer sb = new StringBuffer("select * from qywx_user where app_id=?");
		if (StringUtils.isNotBlank(dto.getDeptId())) {
			sb.append(" and dept_id=?");
			params.add(dto.getDeptId());
		}
		if (StringUtils.isNotBlank(dto.getNickName())) {
			sb.append(" and nick_name=?");
			params.add("%" + dto.getNickName() + "%");
		}
//		sb.append(" order by create_time desc");
		PageResult<QywxUser> result = getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(),
				QywxUser.class);
//		result.getData().forEach(action -> {
//			ClaimDTO oc = orderClaimRepository.getOrderClaimByOrderId(action.getId());
//			if (null != oc) {
//				action.setCommentStatus(oc.getCommentStatus());
//			}
//		});
		return result;
	}
}
