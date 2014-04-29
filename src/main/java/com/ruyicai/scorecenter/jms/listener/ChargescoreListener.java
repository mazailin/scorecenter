package com.ruyicai.scorecenter.jms.listener;

import java.math.BigDecimal;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.scorecenter.service.ScoreService;

@Service
public class ChargescoreListener {

	private Logger logger = LoggerFactory.getLogger(LuckyDrawAwardListener.class);

	@Autowired
	private ScoreService scoreService;

	public void chargescoreConsumer(@Header("bussinessId") String bussinessId, @Header("memo") String memo,
			@Header("userno") String userno, @Header("giveScore") Long giveScore) {
		logger.info("充值积分 bussinessId:{} memo:{} userno:{} giveScore:{}", new String[] { bussinessId, memo, userno,
				giveScore + "" });
		scoreService.addTuserinfoScore(userno, bussinessId, 12, null, null, new BigDecimal(giveScore), memo);
	}
}
