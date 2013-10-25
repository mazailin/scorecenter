package com.ruyicai.scorecenter.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.scorecenter.util.DateUtil;
import com.ruyicai.scorecenter.util.Page;
import com.ruyicai.scorecenter.util.Page.Sort;
import com.ruyicai.scorecenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TUSERINFOSCOREDETAIL")
public class TuserinfoScoreDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "BUSSINESSID", length = 50)
	private String bussinessId;

	@Column(name = "SCORE", columnDefinition = "decimal")
	private BigDecimal score;

	@Column(name = "SCORETYPE")
	private Integer scoreType;

	@Column(name = "STATE")
	private Integer state;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "CURRENTSCORE", columnDefinition = "decimal")
	private BigDecimal currentScore;

	@Column(name = "memo")
	private String memo;

	@Transactional
	public static TuserinfoScoreDetail createTuserinfoScoreDetail(String userno, String bussinessId, BigDecimal score,
			Integer scoreType, BigDecimal currentScore, String memo) {
		TuserinfoScoreDetail detail = new TuserinfoScoreDetail();
		detail.setUserno(userno);
		detail.setBussinessId(bussinessId);
		detail.setScore(score);
		detail.setCurrentScore(currentScore);
		detail.setScoreType(scoreType);
		detail.setState(1);
		detail.setCreateTime(new Date());
		detail.setMemo(memo);
		detail.persist();
		return detail;
	}

	public static Integer findCountByTime(String userno, Date date, Integer scoreType) {
		EntityManager em = TuserinfoScoreDetail.entityManager();
		String dateStr = DateUtil.format("yyyy-MM-dd", date);
		String countSql = "SELECT count(*) FROM TuserinfoScoreDetail o WHERE date_format(o.createTime,'%Y-%m-%d') = ? AND o.scoreType = ? AND o.userno = ?";
		TypedQuery<Long> total = em.createQuery(countSql, Long.class).setParameter(1, dateStr)
				.setParameter(2, scoreType).setParameter(3, userno);
		int count = total.getSingleResult().intValue();
		return count;
	}

	public static Integer findCount(String userno, Integer scoreType) {
		EntityManager em = TuserinfoScoreDetail.entityManager();
		String countSql = "SELECT count(*) FROM TuserinfoScoreDetail o WHERE o.scoreType = ? AND o.userno = ?";
		TypedQuery<Long> total = em.createQuery(countSql, Long.class).setParameter(1, scoreType)
				.setParameter(2, userno);
		int count = total.getSingleResult().intValue();
		return count;
	}

	public static void findTuserinfoScoreDetailByPage(String userno, Map<String, Object> conditionMap,
			Page<TuserinfoScoreDetail> page, Boolean flag) {
		EntityManager em = TuserinfoScoreDetail.entityManager();
		String sql = "SELECT o FROM TuserinfoScoreDetail o ";
		String countSql = "SELECT count(*) FROM TuserinfoScoreDetail o ";
		String sumSql = "SELECT sum(o.score) FROM TuserinfoScoreDetail o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		if (StringUtils.isNotBlank(userno)) {
			whereSql.append(" AND o.userno = :userno ");
		}
		List<Sort> sortList = page.fetchSort();
		StringBuilder orderSql = new StringBuilder(" ORDER BY ");
		if (page.isOrderBySetted()) {
			for (Sort sort : sortList) {
				orderSql.append(" " + sort.getProperty() + " " + sort.getDir() + ",");
			}
			orderSql.delete(orderSql.length() - 1, orderSql.length());
		} else {
			orderSql.append(" o.createTime desc ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		String tSumSql = sumSql + whereSql.toString();
		TypedQuery<TuserinfoScoreDetail> q = em.createQuery(tsql, TuserinfoScoreDetail.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		Query sumq = null;
		if (flag) {
			sumq = em.createQuery(tSumSql);
		}
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
			if (sumq != null) {
				PropertyFilter.setMatchValue2Query(sumq, pfList);
			}
		}
		if (StringUtils.isNotBlank(userno)) {
			q.setParameter("userno", userno);
			total.setParameter("userno", userno);
			if (sumq != null) {
				sumq.setParameter("userno", userno);
			}
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<TuserinfoScoreDetail> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		if (sumq != null) {
			@SuppressWarnings("unchecked")
			Object sumscoreobj = sumq.getSingleResult();
			BigDecimal sumscore = (BigDecimal) sumscoreobj;
			TuserinfoScoreDetail tongji = new TuserinfoScoreDetail();
			tongji.setUserno("合计");
			tongji.setScore(sumscore);
			tongji.setCreateTime(null);
			resultList.add(tongji);
		}
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
