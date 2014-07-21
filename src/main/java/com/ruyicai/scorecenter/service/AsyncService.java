package com.ruyicai.scorecenter.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruyicai.scorecenter.util.HttpUtil;
import com.ruyicai.scorecenter.util.StringUtil;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private ScoreService scoreService;

	@Value("${msgcenterurl}")
	String msgcenterurl;
	
	@Value("${warningmobiles}")
	String warningmobiles;
	
	@Async
	public void addTuserinfoScore(String userno, String bussinessId, Integer scoreType, BigDecimal buyAmt,
			BigDecimal totalAmt, BigDecimal giveScore, String memo) {
		logger.info("异步增加用户积分userno:{},bussinessId:{},scoreType:{},buyAmt:{},totalAmt:{},giveScore:{}", new String[] {
				userno, bussinessId, scoreType + "", buyAmt + "", totalAmt + "", giveScore + "" });
		scoreService.addTuserinfoScore(userno, bussinessId, scoreType, buyAmt, totalAmt, giveScore, memo);
	}
	
	/**
	 * 异步发送短信.
	 * @param userno 用户编号
	 * @param score 兑换积分
	 */
	@Async
	public void sendMessage(String userno , Integer score)
	{
		String url = msgcenterurl + "/sms/send";
		String param = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String today = format.format(date);
			BigDecimal scoreMoney = new BigDecimal(score).divide(new BigDecimal(500));
			if(scoreMoney.compareTo(new BigDecimal(5000)) > 0){
				String[] mobileNo = warningmobiles.split(",");
				for (String mobile : mobileNo) {
					if (!StringUtil.isEmpty(mobile)) {
						param = "mobileIds=" + mobile + "&text=" + "用户编号为:" + userno
								+ "的用户于" + today + "积分兑换彩金总金额为：" + scoreMoney + "元，超出了所设定的预警值5000元。";
						String result = HttpUtil.post(url, param);
					}
				}
			}
		} catch (Exception e) {
			logger.info("send url:" + url + ",param:" + param +" error: " + e.getMessage());
		}
	}
}
