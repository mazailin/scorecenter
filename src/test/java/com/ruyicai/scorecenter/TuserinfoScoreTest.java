package com.ruyicai.scorecenter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.scorecenter.controller.dto.TransScoreDTO;
import com.ruyicai.scorecenter.dao.TuserinfoScoreDao;
import com.ruyicai.scorecenter.domain.ScoreType;
import com.ruyicai.scorecenter.domain.TuserinfoScore;
import com.ruyicai.scorecenter.domain.TuserinfoScoreDetail;
import com.ruyicai.scorecenter.jms.listener.CaselotFinishListener;
import com.ruyicai.scorecenter.service.ScoreService;
import com.ruyicai.scorecenter.util.JsonUtil;
import com.ruyicai.scorecenter.util.Page;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class TuserinfoScoreTest {

	@Autowired
	private ScoreService scoreService;

	@Autowired
	private CaselotFinishListener caselotBetFullListener;

	@Autowired
	private TuserinfoScoreDao tuserinfoScoreDao;

	@Test
	public void testCreateScoreType() {
		Map<String, String> mapfu1 = new LinkedHashMap<String, String>();
		mapfu1.put("base", "500");
		ScoreType.saveOrUpdateScoreType(-1, "兑换积分", null, 1, JsonUtil.toJson(mapfu1));

		Map<String, String> map1 = new LinkedHashMap<String, String>();
		map1.put("base", "20");
		ScoreType.saveOrUpdateScoreType(1, "注册并完善信息", 1, 1, JsonUtil.toJson(map1));

		Map<String, String> map2 = new LinkedHashMap<String, String>();
		map2.put("base", "5");
		ScoreType.saveOrUpdateScoreType(2, "普通投注", null, 1, JsonUtil.toJson(map2));

		Map<String, String> map3 = new LinkedHashMap<String, String>();
		map3.put("base", "6");
		ScoreType.saveOrUpdateScoreType(3, "追号", null, 1, JsonUtil.toJson(map3));

		Map<String, String> map4 = new LinkedHashMap<String, String>();
		map4.put("step1max", "10");
		map4.put("step1base", "6");
		map4.put("step2max", "100");
		map4.put("step2base", "10");
		map4.put("step3max", "500");
		map4.put("step3base", "30");
		map4.put("step4base", "50");
		ScoreType.saveOrUpdateScoreType(4, "发起合买", null, 1, JsonUtil.toJson(map4));

		Map<String, String> map5 = new LinkedHashMap<String, String>();
		map5.put("base", "6");
		ScoreType.saveOrUpdateScoreType(5, "参与合买", null, 1, JsonUtil.toJson(map5));

		Map<String, String> map6 = new LinkedHashMap<String, String>();
		map6.put("base", "2");
		ScoreType.saveOrUpdateScoreType(6, "充值", null, 1, JsonUtil.toJson(map6));

		Map<String, String> map7 = new LinkedHashMap<String, String>();
		map7.put("base", "3");
		ScoreType.saveOrUpdateScoreType(7, "留言建议", 3, 1, JsonUtil.toJson(map7));

		Map<String, String> map8 = new LinkedHashMap<String, String>();
		map8.put("base", "2");
		ScoreType.saveOrUpdateScoreType(8, "用户登录", 1, 1, JsonUtil.toJson(map8));

		Map<String, String> map9 = new LinkedHashMap<String, String>();
		map9.put("base", "5");
		ScoreType.saveOrUpdateScoreType(9, "推广分享", 10, 1, JsonUtil.toJson(map9));

		ScoreType.saveOrUpdateScoreType(99, "赠送积分", null, 1, null);

		List<ScoreType> list = ScoreType.findAllScoreTypes();
		for (ScoreType type : list) {
			System.out.println(type.toString());
		}
	}

	@Test
	public void testAddScore() {
		TuserinfoScore addScore1 = tuserinfoScoreDao.addScore("123456", new BigDecimal(1000));
		TuserinfoScoreDetail.createTuserinfoScoreDetail("123456", null, new BigDecimal(1000), 1, addScore1.getScore(),
				null);
		TuserinfoScore addScore2 = tuserinfoScoreDao.addScore("123456", new BigDecimal(2000));
		TuserinfoScoreDetail.createTuserinfoScoreDetail("123456", null, new BigDecimal(2000), 2, addScore2.getScore(),
				null);
		TuserinfoScore addScore3 = tuserinfoScoreDao.addScore("123456", new BigDecimal(3000));
		TuserinfoScoreDetail.createTuserinfoScoreDetail("123456", null, new BigDecimal(3000), 3, addScore3.getScore(),
				null);
	}

	@Test
	public void testscoreService() {
		TuserinfoScore score = tuserinfoScoreDao.findTuserinfoScore("00000035", false);
		scoreService.addTuserinfoScore("00000035", null, 99, null, null, new BigDecimal(10000), null);
		TuserinfoScore score2 = tuserinfoScoreDao.findTuserinfoScore("00000035", false);
		Assert.assertTrue(score.getScore().add(new BigDecimal(10000)).compareTo(score2.getScore()) == 0);
		scoreService.transScore2Money("00000035", 10000);
		TuserinfoScore score3 = tuserinfoScoreDao.findTuserinfoScore("00000035", false);
		Assert.assertTrue(score2.getScore().subtract(new BigDecimal(10000)).compareTo(score3.getScore()) == 0);
	}

	@Test
	public void testscoreService2() {
		TuserinfoScore score = tuserinfoScoreDao.findScoreIfNotExist("00111337");
		System.out.println(score.toString());
	}

	@Test
	public void testCaselotBetFull() {
		String str = "{\"batchcode\":\"2012039\",\"buyAmtByFollower\":0,\"buyAmtByStarter\":200,\"caselotinfo\":null,\"commisionRatio\":0,\"content\":null,\"description\":\"rychm\",\"displayState\":2,\"displayStateMemo\":\"满员\",\"endTime\":null,\"full\":true,\"hasachievement\":0,\"id\":\"C00000001429717\",\"isWinner\":0,\"lotno\":\"F47104\",\"lotsType\":1,\"minAmt\":100,\"orderid\":\"TE2012022400134431\",\"participantCount\":1,\"safeAmt\":0,\"sortState\":0,\"startTime\":1330069141530,\"starter\":\"00001383\",\"state\":3,\"title\":\"如意彩合买\",\"totalAmt\":200,\"version\":5,\"visibility\":0,\"winBigAmt\":0,\"winDetail\":null,\"winEndTime\":null,\"winFlag\":null,\"winLittleAmt\":null,\"winPreAmt\":0,\"winStartTime\":null}";
		caselotBetFullListener.caselotFinishCustomer(str);
	}

	@Test
	public void testTransScore2Money() {
		TransScoreDTO dto = scoreService.transScore2Money("00000036", 500);
		System.out.println(dto);
	}

	@Test
	public void testFindTuserinfoScoreDetailByPage() {
		Page<TuserinfoScoreDetail> page = new Page<TuserinfoScoreDetail>(0, 20);
		TuserinfoScoreDetail.findTuserinfoScoreDetailByPage("00000137", new HashMap<String, Object>(), page, true);
		System.out.println(page.getList());
	}

	@Test
	public void testSaveScoreType() {
		Map<String, String> mapfu1 = new LinkedHashMap<String, String>();
		mapfu1.put("date", "2012-11-1 00:00:00");
		mapfu1.put("base", "500");
		mapfu1.put("base1", "500");
		mapfu1.put("base2", "1000");
		ScoreType.saveOrUpdateScoreType(-1, "兑换积分", null, 1, JsonUtil.toJson(mapfu1));
	}

}
