package com.application.marketing.common.repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import com.application.marketing.common.controller.dto.ListQywxFundFlowDTO;
import com.application.marketing.common.domain.QywxFundFlow;
import com.javapai.framework.action.PageResult;
import com.javapai.framework.common.service.AbstractBizService;

@Component
public class QywxFundFlowDao extends AbstractBizService {
	
	public PageResult<QywxFundFlow> listFundFlow(ListQywxFundFlowDTO dto) {
		/* 初始化参数 */
		List<Object> params = new ArrayList<Object>();
		
		if (null == dto.getDateFrom()) {
			// 默认时间 当天00:00:00
			dto.setDateFrom(LocalDate.now());
		}
		if (null == dto.getDateTo()) {
			// 默认时间 当天00:00:00
			dto.setDateTo(LocalDate.now());
		}
		
		/* 构造查询语句 */
		StringBuffer sb = new StringBuffer("select * from qywx_fund_flow where timestamp>=? and timestamp <=?");
		params.add(dto.getDateFrom().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
		params.add(dto.getDateTo().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
		if (null != dto.getFlowType()) {
			sb.append(" and fund_flow_type = ?");
			params.add(dto.getFlowType());
		}
		if (null != dto.getTransactionType()) {
			sb.append(" and transaction_type = ?");
			params.add(dto.getTransactionType());
		}
		sb.append(" order by timestamp desc,fund_flow_type");
		return getPage(sb.toString(), params, dto.getPageIndex(), dto.getPageSize(), QywxFundFlow.class);
	}
	
	/**
	 * 查询指定区间内的数据
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<QywxFundFlow> listFundFlow(long beginTime, long endTime) {
		String sql = "select * from qywx_fund_flow where timestamp>=? and timestamp <=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<QywxFundFlow>(QywxFundFlow.class), beginTime, endTime);
	}
	
	/**
	 * 删除指定区间内的数据
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public int deleteFundFlow(long beginTime, long endTime) {
		String sql = "delete from qywx_fund_flow where timestamp>=? and timestamp <=?";
		return jdbcTemplate.update(sql, beginTime, endTime);
	}
}
