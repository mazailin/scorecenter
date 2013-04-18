package com.ruyicai.scorecenter.jms.listener;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.scorecenter.service.ScoreService;

@Service
public class OrderAfterBetListener {

	private Logger logger = LoggerFactory.getLogger(OrderAfterBetListener.class);

	@Autowired
	private ScoreService scoreService;

	public void orderAfterBetCustomer(@Body String orderJson) {
		Torder torder = Torder.fromJsonToTorder(orderJson);
		if (torder != null) {
			if (StringUtils.isBlank(torder.getTlotcaseid())) {
				if (StringUtils.isBlank(torder.getTsubscribeflowno())) {
					logger.info("普通投注计算积分,orderJson:" + orderJson);
					scoreService.addTuserinfoScore(torder.getBuyuserno(), torder.getId(), 2, torder.getAmt(), null,
							null, null);
				} else {
					logger.info("追号投注计算积分,orderJson:" + orderJson);
					scoreService.addTuserinfoScore(torder.getUserno(), torder.getId(), 3, torder.getAmt(), null, null,
							null);
				}
			}
		}
	}

}
