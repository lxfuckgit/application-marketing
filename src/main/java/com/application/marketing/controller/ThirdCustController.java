package com.application.marketing.controller;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.common.service.QywxUserService;
import com.application.marketing.controller.dto.CheckClueDTO;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;
import com.javapai.framework.enums.ErrorCode;

/**
 * 三方定制需求
 */
@RestController
@RequestMapping("/third")
public class ThirdCustController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	QywxUserService qywxUserService;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	/**
	 * 检查外部加粉关联的广告和竞价师信息。
	 * 
	 * @param dto
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/checkCampaignClue")
	public RstResult<Map<String, Object>> checkCampaignClue(@RequestBody CheckClueDTO dto) {
		if(StringUtils.isBlank(dto.getAppId())) {
			return ResultBuilder.buildResult(ErrorCode.PARAMS_APPID);
		}
		if ("lmm".equals(dto.getAppId())) {
			dto.setAppId("wwe50af746b57947e8");
		} else if ("bht".equals(dto.getAppId())) {
			dto.setAppId("ww5b77b727717ccd72");
		} else {
			logger.warn("--->无效参数:({})", dto.getAppId());
			return ResultBuilder.buildResult(ErrorCode.INVALID_APPID);
		}
		
		/* 通过电话转换userId */
		String wxUserId = qywxUserService.getUserIdByMobile(dto.getAppId(), dto.getStaffMobile());
		if (Objects.isNull(wxUserId)) {
			logger.warn("--->当前电话({}/{})无关联用户！", dto.getAppId(), dto.getStaffMobile());
			return ResultBuilder.normalResult();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("select mc.ad_account,mp.party_id as bidder_id from marketing_clue mc");
		sb.append(" left join marketing m on mc.marketing_id=m.id");
		sb.append(" left join marketing_party mp on mc.marketing_id=mp.marketing_id and mp.party_type=9");
		sb.append(" where m.app_id=? and mc.user_id=? and ext_user_name=?");
		try {
			Map<String, Object> result = jdbcTemplate.queryForMap(sb.toString(), dto.getAppId(), wxUserId, dto.getCustomerName());
			return ResultBuilder.normalResult(result);
		} catch (DataAccessException e) {
			logger.warn("--->查询异常：{}",e.getMessage());
			return ResultBuilder.normalResult();
		}
	}

}
