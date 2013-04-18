package com.ruyicai.scorecenter.jms.listener;

import java.math.BigDecimal;
import java.util.List;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.CaseLotBuy;
import com.ruyicai.scorecenter.service.LotteryService;
import com.ruyicai.scorecenter.service.ScoreService;

@Service
public class CaselotFinishListener {

	private Logger logger = LoggerFactory.getLogger(CaselotFinishListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private ScoreService scoreService;

	@Transactional
	public void caselotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买满员积分 caseLotJson:" + caseLotJson);
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(caseLotJson);
		if (caseLot != null && StringUtils.isNotBlank(caseLot.getId())) {
			List<CaseLotBuy> list = lotteryService.selectCaseLotBuysWithOutPage(caseLot.getId());
			for (CaseLotBuy buy : list) {
				if (buy.getUserno().equals(caseLot.getStarter())) {
					scoreService.addTuserinfoScore(buy.getUserno(), String.valueOf(buy.getId()), 4, buy.getNum(),
							new BigDecimal(caseLot.getTotalAmt()), null, null);
				} else {
					scoreService.addTuserinfoScore(buy.getUserno(), String.valueOf(buy.getId()), 5, buy.getNum(),
							new BigDecimal(caseLot.getTotalAmt()), null, null);
				}
			}
		}
	}

}
