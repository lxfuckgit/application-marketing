package com.application.marketing.common.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.marketing.common.domain.QywxFundFlow;
import com.application.marketing.common.repository.QywxFundFlowDao;
import com.javapai.framework.utils.UtilJson;
import com.thirdparty.eweixin.PaymentClient;
import com.thirdparty.params.WxFundFlow;
import com.thirdparty.params.WxFundFlowResult;

@Service
public class QywxPaymentService extends QywxService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	QywxFundFlowDao qywxFundFlowDao;

	PaymentClient client = new PaymentClient();

	@Transactional
	public void getFundFlow(String appId, String billDate) {
		// 1. 计算当天 00:00:00 和次日 00:00:00 的时间戳（秒）
		LocalDate date = LocalDate.parse(billDate);
		long beginTime = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
		long endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
		List<QywxFundFlow> fundFlowList = qywxFundFlowDao.listFundFlow(beginTime, endTime);
		if (null != fundFlowList && fundFlowList.size() > 0) {
			logger.info("--->当前日期[{}]的交易数据无法二次同步！", billDate);
			return;
		}
		
		// 2.查询当天的资金流水
		String cursor = null;
		boolean nextPage = true;
		List<WxFundFlow> records = new ArrayList<>();
		String token = getAccessToken(appId);
		while (nextPage) {
			// 提取本页流水记录
			String jsonString = client.getFundFlow(token, beginTime, endTime, cursor);
			WxFundFlowResult jsonResult = UtilJson.json2Object(jsonString, WxFundFlowResult.class);
			if (null != jsonResult && jsonResult.ifSuccess()) {
				records.addAll(jsonResult.getFundFlowList());
				logger.info("--->当前日期[{}]的交易数据累计数量：{}", billDate, records.size());
			} else {
				logger.info("--->当前日期[{}]的交易数据同步异常：{}", billDate, jsonResult.getErrmsg());
			}

			// 检查是否还有下一页
			if (StringUtils.isBlank(jsonResult.getNextCursor())) {
				nextPage = false;
			} else {
				cursor = jsonResult.getNextCursor();
				logger.info("--->继续拉取...");
			}
		}

		// 3.删除当天的资金流水
//		int r = qywxFundFlowDao.deleteFundFlow(beginTime, endTime);
//		logger.info("--->[{}]本地历史数据清理中...", r);

		// 4.保存当天的资金流水
		int totalCount = 0;
		final int BATCH_SIZE = 200;
		for (int i = 0; i < records.size(); i += BATCH_SIZE) {
			int end = Math.min(i + BATCH_SIZE, records.size());
			List<WxFundFlow> batchList = records.subList(i, end);
			totalCount += doBatchSave(batchList);
		}
		logger.info("--->当日[{}]数据[{}]条已同步完毕！", billDate, totalCount);
	}

	private int doBatchSave(List<WxFundFlow> batchList) {
		String sql = "insert into qywx_fund_flow (timestamp, request_no, transaction_type, fund_flow_type, transaction_amount, account_balance, out_trade_no, mch_id, operator_userid) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				WxFundFlow record = batchList.get(i);
				ps.setObject(1, record.getTimestamp());// 官方返回的单位是:秒
				ps.setString(2, record.getRequestNo());
				ps.setObject(3, record.getTransactionType());
				ps.setObject(4, record.getFundFlowType());
				ps.setObject(5, record.getTransactionAmount());
				ps.setObject(6, record.getAccountBalance());
				ps.setString(7, record.getOutTradeNo());
				ps.setString(8, record.getMchId());
				ps.setString(9, record.getOperatorUserid());
			}

			@Override
			public int getBatchSize() {
				return batchList.size();
			}
		}).length;
	}

}
