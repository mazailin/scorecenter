package com.ruyicai.scorecenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.CaseLotBuy;
import com.ruyicai.lottery.domain.CaseLotBuyAndUserDTO;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.scorecenter.controller.ResponseData;
import com.ruyicai.scorecenter.exception.RuyicaiException;
import com.ruyicai.scorecenter.util.ErrorCode;
import com.ruyicai.scorecenter.util.HttpUtil;
import com.ruyicai.scorecenter.util.JsonUtil;
import com.ruyicai.scorecenter.util.StringUtil;

@Service
public class LotteryService {

	private Logger logger = LoggerFactory.getLogger(LotteryService.class);

	@Autowired
	MemcachedService<String> memcachedService;

	@Value("${lotteryurl}")
	String lotteryurl;

	/**
	 * @param lotno
	 *            彩种
	 * @param batchcode
	 *            期号
	 * @param state
	 *            状态，Null则默认1
	 * @return List<Tuserinfo>
	 */
	@SuppressWarnings("unchecked")
	public List<String> findUsernosByLotnoAndBatchcodeFromTlot(String lotno, String batchcode, BigDecimal state) {
		if (StringUtils.isBlank(batchcode)) {
			throw new IllegalArgumentException("the argument lotno is required");
		}
		if (StringUtils.isBlank(batchcode)) {
			throw new IllegalArgumentException("the argument batchcode is required");
		}
		if (state == null) {
			state = BigDecimal.ONE;
		}
		List<String> list = new ArrayList<String>();
		String url = lotteryurl + "/select/findUsernosByLotnoAndBatchcode?lotno=" + lotno + "&batchcode=" + batchcode
				+ "&state=" + state;
		try {
			String result = Request.Get(url).execute().returnContent().asString();
			list = JsonUtil.fromJsonToObject(result, ArrayList.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		return list;
	}

	/**
	 * @param userno
	 *            用户编号
	 * @return Tuserinfo
	 */
	public Tuserinfo findTuserinfoByUserno(String userno) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		Tuserinfo tuserinfo = null;
		String url = lotteryurl + "/tuserinfoes?find=ByUserno&json&userno=" + userno;
		try {
			String userJson = memcachedService.get(StringUtil.join("_", "Tuserinfo", userno));
			if (StringUtils.isNotBlank(userJson)) {
				tuserinfo = Tuserinfo.fromJsonToTuserinfo(userJson);
			}
			if (tuserinfo != null) {
				return tuserinfo;
			}
			logger.info("find user from lottery,userno:" + userno);
			String result = Request.Get(url.toString()).execute().returnContent().asString();
			if (StringUtils.isNotBlank(result)) {
				JSONObject jsonObject = new JSONObject(result);
				String errorCode = jsonObject.getString("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					String value = jsonObject.getString("value");
					tuserinfo = Tuserinfo.fromJsonToTuserinfo(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage());
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		return tuserinfo;
	}

	/**
	 * 赠送彩金
	 * 
	 * @param userno
	 * @param amt
	 * @param subchannel
	 * @param channel
	 * @param memo
	 * @return
	 */
	public Boolean directChargeProcess(String userno, BigDecimal amt, String subchannel, String channel, String memo) {
		Boolean flag = false;
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument mobileid is required");
		}
		if (amt == null) {
			throw new IllegalArgumentException("the argument mobileid is required");
		}
		logger.info("赠送彩金 user:{},amt:{}", new String[] { userno, amt.toString() });
		String subchannelStr = subchannel == null ? " " : subchannel;
		String channelStr = channel == null ? " " : channel;
		String url = lotteryurl + "/taccounts/doDirectChargeProcess";
		StringBuffer params = new StringBuffer();
		params.append("userno=" + userno).append("&amt=" + amt.toString()).append("&accesstype=2")
				.append("&subchannel=" + subchannelStr).append("&channel=" + channelStr);
		if (memo != null) {
			params.append("&memo=" + memo);
		}
		try {
			String result = HttpUtil.post(url, params.toString());
			ResponseData rd = JsonUtil.fromJsonToObject(result, ResponseData.class);
			if (rd.getErrorCode().equals("0")) {
				flag = true;
			} else {
				logger.error("赠送彩金错误信息:" + rd.getValue());
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + ",参数 " + params.toString() + ".失败" + e.getMessage());
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		return flag;
	}

	/**
	 * 查询合买参与人
	 * 
	 * @param caselotid
	 * @return
	 */
	public List<CaseLotBuy> selectCaseLotBuysWithOutPage(String caselotid) {
		if (StringUtils.isBlank(caselotid)) {
			throw new IllegalArgumentException("the caselotid argument is required");
		}
		logger.info("selectCaseLotBuysWithOutPage caselotid:" + caselotid);
		List<CaseLotBuy> resultList = new ArrayList<CaseLotBuy>();
		String url = lotteryurl + "/select/selectCaseLotBuysWithOutPage?caselotid=" + caselotid + "&withOutUser=1";
		try {
			String result = Request.Get(url).execute().returnContent().asString();
			JSONObject jsonObject = new JSONObject(result);
			String errorCode = jsonObject.getString("errorCode");
			if (errorCode.equals(ErrorCode.OK.value)) {
				if (jsonObject.has("value")) {
					String valueJson = jsonObject.getString("value");
					JSONObject valueObject = new JSONObject(valueJson);
					if (valueObject.has("caseLotBuyAndUser")) {
						String resultValue = valueObject.getString("caseLotBuyAndUser");
						Collection<CaseLotBuyAndUserDTO> collection = CaseLotBuyAndUserDTO
								.fromJsonArrayToCaseLoes(resultValue);
						for (CaseLotBuyAndUserDTO dto : collection) {
							resultList.add(dto.getCaseLotBuy());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		return resultList;
	}
}
