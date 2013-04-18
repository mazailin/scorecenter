package com.ruyicai.scorecenter;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.lottery.domain.CaseLotBuy;
import com.ruyicai.scorecenter.service.LotteryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class LotteryServiceTest {

	@Autowired
	private LotteryService lotteryService;

	@Test
	public void test() {
		List<CaseLotBuy> list = lotteryService.selectCaseLotBuysWithOutPage("C00000001429717");
		for (CaseLotBuy buy : list) {
			System.out.println(buy.toString());
		}
	}
}
