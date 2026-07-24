package com.application.marketing.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.marketing.controller.dto.CheckClueDTO;
import com.javapai.framework.action.ResultBuilder;
import com.javapai.framework.action.RstResult;

/**
 * 三方定制需求
 */
@RestController
public class ThirdCustController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@org.springframework.beans.factory.annotation.Autowired
	protected org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

	/**
	 * 检查外部加粉关联的广告和竞价师信息。
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping("/checkCampaignClue")
	public RstResult<Map<String, Object>> checkCampaignClue(@RequestBody CheckClueDTO dto) {
		StringBuffer sb = new StringBuffer();
		sb.append("select mc.ad_account,mp.party_id as bidder_id from marketing_clue mc");
		sb.append(" left join marketing m on mc.marketing_id=m.id");
		sb.append(" left join marketing_party mp on mc.marketing_id=mp.marketing_id and mp.party_type=9");
		sb.append(" where m.app_id=? and mc.user_id=? and ext_user_name=?");
		try {
			Map<String, Object> result = jdbcTemplate.queryForMap(sb.toString(), dto.getAppId(), dto.getStaffId(), dto.getCustomerName());
			return ResultBuilder.normalResult(result);
		} catch (DataAccessException e) {
			logger.warn("--->查询异常：{}",e.getMessage());
			return ResultBuilder.normalResult();
		}
	}

}
