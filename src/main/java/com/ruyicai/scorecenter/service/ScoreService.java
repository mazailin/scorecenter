package com.ruyicai.scorecenter.service;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.scorecenter.controller.dto.TransScoreDTO;
import com.ruyicai.scorecenter.dao.TuserinfoScoreDao;
import com.ruyicai.scorecenter.domain.ScoreType;
import com.ruyicai.scorecenter.domain.TuserinfoScore;
import com.ruyicai.scorecenter.domain.TuserinfoScoreDetail;
import com.ruyicai.scorecenter.exception.RuyicaiException;
import com.ruyicai.scorecenter.util.ErrorCode;
import com.ruyicai.scorecenter.util.JsonUtil;

@Service
public class ScoreService {

	private Logger logger = LoggerFactory.getLogger(ScoreService.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TuserinfoScoreDao tuserinfoScoreDao;
	
	@Autowired
	AsyncService asyncService;

	/*
	 * @Autowired private TjmsserviceService tjmsserviceService;
	 */

	/**
	 * 增加用户积分
	 * 
	 * @param userno
	 *            用户编号
	 * @param bussinessId
	 *            业务ID
	 * @param scoreType
	 *            积分类型
	 * @param buyAmt
	 *            购买金额
	 * @param totalAmt
	 *            总金额(合买时使用)
	 * @param giveScore
	 *            赠送积分
	 * @return Boolean 是否增加积分
	 */
	public Boolean addTuserinfoScore(String userno, String bussinessId, Integer scoreType, BigDecimal buyAmt,
			BigDecimal totalAmt, BigDecimal giveScore, String memo) {
		logger.info("增加用户积分userno:{},bussinessId:{},scoreType:{},buyAmt:{},totalAmt:{},giveScore:{}", new String[] {
				userno, bussinessId, scoreType + "", buyAmt + "", totalAmt + "", giveScore + "" });
		Boolean flag = false;
		ScoreType type = ScoreType.findScoreTypeFromCache(scoreType);
		Integer times = type.getTimes();
		if (type != null && type.getState() == 1) {
			if (times != null) {
				Integer count = TuserinfoScoreDetail.findCountByTime(userno, scoreType);
				logger.info("积分增加次数count:" + count + ",userno:" + userno);
				if (times <= count) {
					logger.info("用户userno:{}参加{}已达到{}次不再增加积分", new String[] { userno, type.getMemo(), count + "" });
					return flag;
				}
			}
			if (type.getNum() != null) {
				Integer num = type.getNum();
				Integer count = TuserinfoScoreDetail.findCount(userno, scoreType);
				if (count >= num) {
					logger.info("用户userno:{}已参加{}不再增加积分", new String[] { userno, type.getMemo() });
					return flag;
				}
			}
			BigDecimal addScore = computeScore(userno, bussinessId, scoreType, buyAmt, totalAmt, giveScore, type);
			if (addScore.compareTo(BigDecimal.ZERO) > 0) {
				flag = true;
				doAddTuserinfoScore(userno, bussinessId, scoreType, memo, addScore);
			}
		} else {
			logger.error("无效积分类型userno:{},scoreType:{},bussinessId:{}");
		}
		return flag;
	}

	@Transactional
	public void doAddTuserinfoScore(String userno, String bussinessId, Integer scoreType, String memo,
			BigDecimal addScore) {
		TuserinfoScore tuserinfoScore = tuserinfoScoreDao.addScore(userno, addScore);
		TuserinfoScoreDetail detail = TuserinfoScoreDetail.createTuserinfoScoreDetail(userno, bussinessId, addScore,
				scoreType, tuserinfoScore.getScore(), memo);
		logger.info("增加积分,userno:{},addScore:{},scoreType:{},bussinessId:{},tuserinfoScoreDetailId:{}", new String[] {
				userno, addScore + "", scoreType + "", bussinessId, detail.getId() + "" });
	}

	/**
	 * 积分换彩金
	 * 
	 * @param userno
	 *            用户编号
	 * @param bussinessId
	 *            业务ID
	 * @param scoreType
	 *            积分类型
	 * @param buyAmt
	 *            购买金额
	 * @param totalAmt
	 *            总金额(合买时使用)
	 * @param giveScore
	 *            赠送积分
	 * @return Boolean 是否增加积分
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public TransScoreDTO transScore2Money(String userno, Integer score) {
		ScoreType type = ScoreType.findScoreTypeFromCache(-1);
		if (type != null && type.getState() == 1) {
			HashMap<String, String> map = null;
			if (type != null && StringUtils.isNotBlank(type.getScoreJson())) {
				map = JsonUtil.fromJsonToObject(type.getScoreJson(), HashMap.class);
				if (map != null && map.containsKey("base")) {
					BigDecimal base = new BigDecimal(map.get("base"));
					Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
					if (tuserinfo == null) {
						throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
					}
					if (score <= 0 || score % base.intValue() != 0) {
						throw new RuyicaiException(ErrorCode.ScoreCenter_Tran2MoneyLotmulti_Error);
					}
					BigDecimal money = new BigDecimal(score).divide(base).multiply(new BigDecimal(100));
					TuserinfoScore deductScore = tuserinfoScoreDao.deductScore(userno, new BigDecimal(score));
					TuserinfoScoreDetail.createTuserinfoScoreDetail(userno, null, new BigDecimal(score), -1,
							deductScore.getScore(), "积分换彩金");
					Boolean directChargeProcess = lotteryService.directChargeProcess(userno, money,
							tuserinfo.getSubChannel(), tuserinfo.getChannel(), "积分兑换彩金");
					if (directChargeProcess == false) {
						throw new RuyicaiException(ErrorCode.ERROR);
					}
					TransScoreDTO dto = new TransScoreDTO();
					dto.setMoney(money);
					dto.setTuserinfoScore(deductScore);
					//积分兑换彩金超过5000元的发送短信
					asyncService.sendMessage(userno,score);
					return dto;
				}
			}
		} else {
			if (type != null) {
				if (type.getState() != 1) {
					logger.error("兑换积分未开启userno:{},score:{}", new String[] { userno, score + "" });
					throw new RuyicaiException(ErrorCode.ScoreCenter_Tran2Money_DISABLE);
				}
			} else {
				logger.error("无效积分类型userno:{},score:{}", new String[] { userno, score + "" });
				throw new RuyicaiException(ErrorCode.ScoreCenter_TYPE_DISABLE);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public BigDecimal computeScore(String userno, String bussinessId, Integer scoreType, BigDecimal buyAmt,
			BigDecimal totalAmt, BigDecimal giveScore, ScoreType type) {
		logger.info("计算积分userno:{},bussinessId:{},scoreType:{},buyAmt:{},totalAmt:{},giveScore:{},type:{}",
				new String[] { userno, bussinessId, scoreType + "", buyAmt + "", totalAmt + "", giveScore + "",
						type + "" });
		BigDecimal addScore = BigDecimal.ZERO;
		HashMap<String, String> map = null;
		if (type != null && StringUtils.isNotBlank(type.getScoreJson())) {
			map = JsonUtil.fromJsonToObject(type.getScoreJson(), HashMap.class);
		}
		switch (scoreType) {
		case 1:// 注册并完善信息
			if (map != null && map.containsKey("base")) {
				addScore = new BigDecimal(map.get("base"));
			}
			break;
		case 2:// 普通投注
			if (buyAmt != null && buyAmt.compareTo(BigDecimal.ZERO) > 0) {
				if (map != null && map.containsKey("base")) {
					BigDecimal buyYuan = buyAmt.divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
					addScore = buyYuan.multiply(new BigDecimal(map.get("base")));
				}
			}
			break;
		case 3:// 追号
			if (buyAmt != null && buyAmt.compareTo(BigDecimal.ZERO) > 0) {
				if (map != null && map.containsKey("base")) {
					BigDecimal buyYuan = buyAmt.divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
					addScore = buyYuan.multiply(new BigDecimal(map.get("base")));
				}
			}
			break;
		case 4:// 发起合买
			if (buyAmt != null && totalAmt != null && totalAmt.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal totalYuan = totalAmt.divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
				BigDecimal buyYuan = buyAmt.divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
				if (map != null) {
					if (totalYuan.compareTo(new BigDecimal(map.get("step1max"))) < 0) {
						addScore = buyYuan.multiply(new BigDecimal(map.get("step1base")));
					}
					if (totalYuan.compareTo(new BigDecimal(map.get("step1max"))) >= 0) {
						addScore = buyYuan.multiply(new BigDecimal(map.get("step2base")));
					}
				}
			}
			break;
		case 5:// 参与合买
			if (buyAmt != null && buyAmt.compareTo(BigDecimal.ZERO) > 0) {
				if (map != null && map.containsKey("base")) {
					BigDecimal buyYuan = buyAmt.divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
					addScore = buyYuan.multiply(new BigDecimal(map.get("base")));
				}
			}
			break;
		case 6:// 充值
			if (buyAmt != null && buyAmt.compareTo(BigDecimal.ZERO) > 0) {
				if (map != null && map.containsKey("base")) {
					BigDecimal buyYuan = buyAmt.divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
					addScore = buyYuan.multiply(new BigDecimal(map.get("base")));
				}
			}
			break;
		case 7:// 留言建议
			if (map != null && map.containsKey("base")) {
				addScore = new BigDecimal(map.get("base"));
			}
			break;
		case 8:// 用户登录
			if (map != null && map.containsKey("base")) {
				addScore = new BigDecimal(map.get("base"));
			}
			break;
		case 9:// 推广分享
			if (map != null && map.containsKey("base")) {
				addScore = new BigDecimal(map.get("base"));
			}
			break;
		case 10:// 如意彩竞猜
			if (giveScore != null) {
				addScore = giveScore;
			}
			break;
		case 11:// 幸运抽奖
			if (giveScore != null) {
				addScore = giveScore;
			}
			break;
		case 12:// 购买积分
			if (giveScore != null) {
				addScore = giveScore;
			}
			break;
		case 13:// 赠送积分。只送一次
			if (giveScore != null) {
				addScore = giveScore;
			}
			break;
		case 99:// 赠送积分
			if (giveScore != null) {
				addScore = giveScore;
			}
			break;
		}
		logger.info("计算积分结果:" + addScore);
		return addScore;
	}

	/**
	 * 如意彩竞猜扣积分
	 * 
	 * @param userno
	 * @param score
	 * @param quizId
	 */
	public void quizDeductScore(String userno, BigDecimal score, Integer quizId) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The argument userno is required");
		}
		if (score == null || score.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("The argument score is required.");
		}
		if (quizId == null) {
			throw new IllegalArgumentException("The argument quizId is required.");
		}
		ScoreType type = ScoreType.findScoreTypeFromCache(-2);
		if (type != null && type.getState().equals(1)) {
			Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
			if (tuserinfo == null) {
				throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
			}
			TuserinfoScore deductScore = tuserinfoScoreDao.deductScore(userno, score);
			TuserinfoScoreDetail.createTuserinfoScoreDetail(userno, quizId + "", score, -2, deductScore.getScore(), "");
		} else {
			if (type != null) {
				if (type.getState() != 1) {
					logger.error("兑换积分未开启userno:{},score:{}", new String[] { userno, score + "" });
					throw new RuyicaiException(ErrorCode.ScoreCenter_QuizDeductScore_DISABLE);
				}
			} else {
				logger.error("无效积分类型userno:{},score:{}", new String[] { userno, score + "" });
				throw new RuyicaiException(ErrorCode.ScoreCenter_TYPE_DISABLE);
			}
		}
	}

	/**
	 * 扣除积分
	 * 
	 * @param userno 用户id
	 * @param score 小号积分
	 * @param businessId 业务id
	 * @param memo 备忘
	 * @param scoreType 积分类型
	 * @param 积分类型 备忘
	 */
	public void deductScore(String userno, BigDecimal score, String businessId, String memo, Integer scoreType) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The argument userno is required");
		}
		if (score == null) {
			throw new IllegalArgumentException("The argument score is required.");
		}
		if (businessId == null) {
			throw new IllegalArgumentException("The argument businessId is required.");
		}
		if (scoreType == null) {
			throw new IllegalArgumentException("The argument scoreType is required.");
		}
		ScoreType type = ScoreType.findScoreTypeFromCache(scoreType);
		if (type != null && type.getState().equals(1)) {
			Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
			if (tuserinfo == null) {
				throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
			}
			TuserinfoScore deductScore = tuserinfoScoreDao.deductScore(userno, score);
			TuserinfoScoreDetail.createTuserinfoScoreDetail(userno, businessId, score, scoreType,
					deductScore.getScore(), memo);
		} else {
			if (type != null) {
				if (type.getState() != 1) {
					logger.error("兑换积分未开启userno:{},score:{}", new String[] { userno, score + "" });
					throw new RuyicaiException(ErrorCode.ScoreCenter_QuizDeductScore_DISABLE);
				}
			} else {
				logger.error("无效积分类型userno:{},score:{}", new String[] { userno, score + "" });
				throw new RuyicaiException(ErrorCode.ScoreCenter_TYPE_DISABLE);
			}
		}
	}
}
