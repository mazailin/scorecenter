package com.ruyicai.scorecenter.jms.listener;

import java.math.BigDecimal;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.scorecenter.service.ScoreService;

@Service
public class FundJmsListener {

	private Logger logger = LoggerFactory.getLogger(FundJmsListener.class);

	@Autowired
	private ScoreService scoreService;

	public void fundJmsCustomer(@Header("TTRANSACTIONID") String ttransactionid,
			@Header("LADDERPRESENTFLAG") Long ladderpresentflag, @Header("USERNO") String userno,
			@Header("AMT") Long amt, @Header("TYPE") Integer type) {
		if (type == 1) {
			logger.info("充值赠送积分,userno:{},amt:{},ttransactionid:{},ladderpresentflag:{}", new String[] { userno,
					amt + "", ttransactionid, ladderpresentflag + "" });
			scoreService.addTuserinfoScore(userno, ttransactionid, 6, new BigDecimal(amt), null, null, null);
		}
	}

}
