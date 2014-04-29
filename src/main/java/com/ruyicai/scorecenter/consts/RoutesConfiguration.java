package com.ruyicai.scorecenter.consts;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {

	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Autowired
	private CamelContext camelContext;

	@PostConstruct
	public void init() throws Exception {
		logger.info("init camel routes");
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1).redeliveryDelay(3000);
				from("jms:queue:VirtualTopicConsumers.scorecenter.orderAfterBetTopic?concurrentConsumers=20").to(
						"bean:orderAfterBetListener?method=orderAfterBetCustomer").routeId("订单投注成功计算积分");
				from("jms:queue:VirtualTopicConsumers.scorecenter.caselotFinish").to(
						"bean:caselotFinishListener?method=caselotFinishCustomer").routeId("合买期截计算积分");
				/*from("jms:queue:VirtualTopicConsumers.scorecenter.actioncenter").to(
						"bean:fundJmsListener?method=fundJmsCustomer").routeId("用户充值计算积分");*/
				from("jms:queue:VirtualTopicConsumers.scorecenter.quizAward?concurrentConsumers=10").to(
						"bean:quizAwardListener?method=quizAwardConsumer").routeId("如意彩竞猜奖励积分");
				from("jms:queue:VirtualTopicConsumers.scorecenter.chargescore?concurrentConsumers=10").to(
						"bean:chargescoreListener?method=chargescoreConsumer").routeId("充值积分");
				from("jms:queue:VirtualTopicConsumers.scorecenter.luckyDrawAward?concurrentConsumers=10").to(
						"bean:luckyDrawAwardListener?method=luckyDrawAwardConsumer").routeId("幸运抽奖奖励积分");
			}
		});
	}
}
