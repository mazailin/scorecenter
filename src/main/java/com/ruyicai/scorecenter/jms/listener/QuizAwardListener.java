package com.ruyicai.scorecenter.jms.listener;

import java.math.BigDecimal;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.scorecenter.service.ScoreService;

@Service
public class QuizAwardListener {

	private Logger logger = LoggerFactory.getLogger(QuizAwardListener.class);
	
	@Autowired
	private ScoreService scoreService;

	public void quizAwardConsumer(@Header("bussinessId") String bussinessId, @Header("memo") String memo,
			@Header("userno") String userno, @Header("giveScore") Long giveScore) {
		logger.info("如意彩竞猜赠送积分 quizid:{} memo:{} userno:{} giveScore:{}", new String[] {bussinessId, memo, userno, giveScore + ""});
		scoreService.addTuserinfoScore(userno, bussinessId, 10, null, null, new BigDecimal(giveScore), memo);
	}
}
