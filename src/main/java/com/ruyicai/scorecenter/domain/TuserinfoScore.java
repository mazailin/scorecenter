package com.ruyicai.scorecenter.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.scorecenter.exception.RuyicaiException;
import com.ruyicai.scorecenter.service.MemcachedService;
import com.ruyicai.scorecenter.util.ErrorCode;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TUSERINFOSCORE")
public class TuserinfoScore implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "SCORE", columnDefinition = "decimal")
	private BigDecimal score;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "LASTMODIFYTIME")
	private Date lastModifyTime;

	@Autowired
	transient MemcachedService<TuserinfoScore> memcachedService;

	public static TuserinfoScore findTuserinfoScore(String id, boolean lock) {
		EntityManager em = TuserinfoScore.entityManager();
		TuserinfoScore score = em.find(TuserinfoScore.class, id, lock ? LockModeType.PESSIMISTIC_WRITE
				: LockModeType.NONE);
		return score;
	}

	public static TuserinfoScore findTuserinfoScoreFromCache(String userno) {
		TuserinfoScore tuserinfoScore = new TuserinfoScore().memcachedService.get("TuserinfoScore" + userno);
		if (tuserinfoScore == null) {
			tuserinfoScore = findScoreIfNotExist(userno);
		}
		if (tuserinfoScore != null) {
			new TuserinfoScore().memcachedService.checkToSet("TuserinfoScore" + userno, tuserinfoScore);
		}
		return tuserinfoScore;
	}

	public static TuserinfoScore findScoreIfNotExist(String userno) {
		TuserinfoScore score = TuserinfoScore.findTuserinfoScore(userno);
		if (score == null) {
			score = createTuserinfoScore(userno);
		}
		return score;
	}

	@Transactional
	public static TuserinfoScore createTuserinfoScore(String userno) {
		TuserinfoScore score = new TuserinfoScore();
		score.setUserno(userno);
		score.setScore(BigDecimal.ZERO);
		score.setCreateTime(new Date());
		score.persist();
		new TuserinfoScore().memcachedService.set("TuserinfoScore" + userno, score);
		return score;
	}

	@Transactional
	public static TuserinfoScore addScore(String userno, BigDecimal addScore) {
		TuserinfoScore score = TuserinfoScore.findTuserinfoScore(userno, true);
		if (score == null) {
			score = createTuserinfoScore(userno);
		}
		if (addScore.compareTo(BigDecimal.ZERO) > 0) {
			score.setScore(score.getScore().add(addScore));
			score.setLastModifyTime(new Date());
			score.merge();
			new TuserinfoScore().memcachedService.set("TuserinfoScore" + userno, score);
		}
		return score;
	}

	@Transactional
	public static TuserinfoScore deductScore(String userno, BigDecimal deductScore) {
		TuserinfoScore tuserinfoScore = TuserinfoScore.findScoreIfNotExist(userno);
		if (tuserinfoScore.getScore().compareTo(deductScore) < 0) {
			throw new RuyicaiException(ErrorCode.ScoreCenter_NOT_ENOUGH);
		}
		if (deductScore.compareTo(BigDecimal.ZERO) > 0) {
			tuserinfoScore.setScore(tuserinfoScore.getScore().subtract(deductScore));
			tuserinfoScore.setLastModifyTime(new Date());
			tuserinfoScore.merge();
			new TuserinfoScore().memcachedService.set("TuserinfoScore" + userno, tuserinfoScore);
		}
		return tuserinfoScore;
	}
}