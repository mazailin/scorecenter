package com.ruyicai.scorecenter.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.scorecenter.controller.dto.TransScoreDTO;
import com.ruyicai.scorecenter.domain.ScoreType;
import com.ruyicai.scorecenter.domain.TuserinfoScore;
import com.ruyicai.scorecenter.domain.TuserinfoScoreDetail;
import com.ruyicai.scorecenter.exception.RuyicaiException;
import com.ruyicai.scorecenter.service.ScoreService;
import com.ruyicai.scorecenter.util.ErrorCode;
import com.ruyicai.scorecenter.util.JsonUtil;
import com.ruyicai.scorecenter.util.Page;

@Controller
public class ScoreCenterController {

	private Logger logger = LoggerFactory.getLogger(ScoreCenterController.class);

	@Autowired
	private ScoreService scoreService;

	/**
	 * 增加用户积分
	 * 
	 * @param userno
	 *            用户编号
	 * @param scoreType
	 *            积分类型
	 * @param bussinessId
	 *            业务ID
	 * @param buyAmt
	 *            购买集合
	 * @param totalAmt
	 *            合买方案总集合
	 * @param giveScore
	 *            赠送积分数
	 * @param memo
	 *            描述
	 * @return
	 */
	@RequestMapping(value = "/addTuserinfoScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData addTuserinfoScore(@RequestParam("userno") String userno, @RequestParam("scoreType") Integer scoreType,
			@RequestParam(value = "bussinessId", required = false) String bussinessId,
			@RequestParam(value = "buyAmt", required = false) BigDecimal buyAmt,
			@RequestParam(value = "totalAmt", required = false) BigDecimal totalAmt,
			@RequestParam(value = "giveScore", required = false) BigDecimal giveScore,
			@RequestParam(value = "memo", required = false) String memo) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("/addTuserinfoScore userno:{},scoreType:{}", new String[] { userno, scoreType + "" });
		try {
			Boolean flag = scoreService.addTuserinfoScore(userno, bussinessId, scoreType, buyAmt, totalAmt, giveScore,
					memo);
			rd.setValue(flag);
		} catch (RuyicaiException e) {
			logger.error("增加用户积分异常,userno:{},scoreType:{}", new String[] { userno, scoreType + "" }, e);
			result = e.getErrorCode();
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("增加用户积分异常,userno:{},scoreType:{}", new String[] { userno, scoreType + "" }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询用户积分
	 * 
	 * @param userno
	 * @return
	 */
	@RequestMapping(value = "/findScoreByUserno")
	public @ResponseBody
	ResponseData findScoreByUserno(@RequestParam("userno") String userno) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		logger.info("/findScoreByUserno userno:" + userno);
		try {
			TuserinfoScore tuserinfoScore = TuserinfoScore.findScoreIfNotExist(userno);
			rd.setValue(tuserinfoScore);
		} catch (RuyicaiException e) {
			logger.error("查询用户积分异常,userno:{}", new String[] { userno }, e);
			result = e.getErrorCode();
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("查询用户积分异常,userno:{}", new String[] { userno }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询用户积分详细
	 * 
	 * @param userno
	 *            用户编号
	 * @param condition
	 *            查询条件JSON格式
	 * @param startLine
	 *            开始行
	 * @param endLine
	 *            查询记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderDir
	 *            排序类型
	 * @return Page
	 */
	@RequestMapping(value = "/findScoreDetailByUserno")
	public @ResponseBody
	ResponseData findScoreDetailByUserno(@RequestParam("userno") String userno,
			@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "flag", required = false, defaultValue = "false") Boolean flag,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		Page<TuserinfoScoreDetail> page = new Page<TuserinfoScoreDetail>(startLine, endLine, orderBy, orderDir);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			TuserinfoScoreDetail.findTuserinfoScoreDetailByPage(userno, conditionMap, page, flag);
			rd.setValue(page);
		} catch (RuyicaiException e) {
			logger.error("查询用户积分详细异常,userno:{}", new String[] { userno }, e);
			result = e.getErrorCode();
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("查询用户积分详细异常,userno:{}", new String[] { userno }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 积分兑换彩金
	 * 
	 * @param userno
	 *            用户编号
	 * @param score
	 *            积分
	 */
	@RequestMapping(value = "/transScore2Money", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData transScore2Money(@RequestParam("userno") String userno, @RequestParam(value = "score") Integer score) {
		ResponseData rd = new ResponseData();
		logger.info("积分兑换彩金userno:{},score:{}",new String[]{userno,score+""});
		ErrorCode result = ErrorCode.OK;
		try {
			TransScoreDTO transScoreDTO = scoreService.transScore2Money(userno, score);
			rd.setValue(transScoreDTO);
		} catch (RuyicaiException e) {
			logger.error("积分兑换彩金异常,userno:{}", new String[] { userno }, e);
			result = e.getErrorCode();
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("积分兑换彩金异常,userno:{}", new String[] { userno }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 创建或更新积分类型
	 * 
	 * @param scoreType
	 *            积分类型
	 * @param memo
	 *            积分描述
	 * @param times
	 *            每天增加次数(没有限制，不传次参数)
	 * @param state
	 *            积分类型是否有效:1有效,2:无效
	 * @param scoreJson
	 *            积分基数JSON串
	 * @return
	 */
	@RequestMapping(value = "/saveOrUpdateScoreType", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData saveOrUpdateScoreType(@RequestParam("scoreType") Integer scoreType, @RequestParam("memo") String memo,
			@RequestParam(value = "times", required = false) Integer times, @RequestParam("state") Integer state,
			@RequestParam("scoreJson") String scoreJson) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			ScoreType type = ScoreType.saveOrUpdateScoreType(scoreType, memo, times, state, scoreJson);
			rd.setValue(type);
		} catch (RuyicaiException e) {
			logger.error("创建或更新积分类型异常,scoreType:{}", new String[] { scoreType + "" }, e);
			result = e.getErrorCode();
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("创建或更新积分类型异常,scoreType:{}", new String[] { scoreType + "" }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询积分类型
	 * 
	 * @param condition
	 *            查询条件JSON串
	 * @param startLine
	 *            开始行数
	 * @param endLine
	 *            每页显示记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderDir
	 *            排序类型
	 * @return
	 */
	@RequestMapping(value = "/selectScoreTypes")
	public @ResponseBody
	ResponseData selectScoreTypes(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		ResponseData rd = new ResponseData();
		Page<ScoreType> page = new Page<ScoreType>(startLine, endLine, orderBy, orderDir);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			ScoreType.findAllScoreTypes(conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (RuyicaiException e) {
			e.printStackTrace();
			logger.error("查询积分类型出错, errorcode:" + e.getErrorCode().value, e);
			rd.setErrorCode(e.getErrorCode().value);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询积分类型出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}

}
