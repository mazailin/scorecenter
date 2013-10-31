package com.ruyicai.scorecenter.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private ScoreService scoreService;

	@Async
	public void addTuserinfoScore(String userno, String bussinessId, Integer scoreType, BigDecimal buyAmt,
			BigDecimal totalAmt, BigDecimal giveScore, String memo) {
		logger.info("异步增加用户积分userno:{},bussinessId:{},scoreType:{},buyAmt:{},totalAmt:{},giveScore:{}", new String[] {
				userno, bussinessId, scoreType + "", buyAmt + "", totalAmt + "", giveScore + "" });
		scoreService.addTuserinfoScore(userno, bussinessId, scoreType, buyAmt, totalAmt, giveScore, memo);
	}
}
