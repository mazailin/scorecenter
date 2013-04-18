package com.ruyicai.scorecenter.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.scorecenter.service.MemcachedService;
import com.ruyicai.scorecenter.util.Page;
import com.ruyicai.scorecenter.util.Page.Sort;
import com.ruyicai.scorecenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "SCORETYPE")
public class ScoreType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SCORETYPE")
	private Integer scoreType;

	@Column(name = "SCOREJSON", columnDefinition = "text")
	private String scoreJson;

	@Column(name = "MEMO", length = 50)
	private String memo;

	@Column(name = "TIMES")
	private Integer times;

	@Column(name = "STATE")
	private Integer state;

	@Column(name = "MODIFYTIME")
	private Date modifyTime;

	@Autowired
	transient MemcachedService<ScoreType> memcachedService;

	@Transactional
	public static ScoreType saveOrUpdateScoreType(Integer scoreType, String memo, Integer times, Integer state,
			String scoreJson) {
		ScoreType type = ScoreType.findScoreType(scoreType);
		if (type != null) {
			type.setMemo(memo);
			type.setTimes(times);
			type.setState(state);
			type.setModifyTime(new Date());
			type.setScoreJson(scoreJson);
			type.merge();
		} else {
			type = new ScoreType();
			type.setScoreType(scoreType);
			type.setMemo(memo);
			type.setTimes(times);
			type.setState(state);
			type.setModifyTime(new Date());
			type.setScoreJson(scoreJson);
			type.persist();
		}
		if (type != null) {
			new ScoreType().memcachedService.set("ScoreType" + scoreType, type);
		}
		return type;
	}

	public static ScoreType findScoreTypeFromCache(Integer scoreType) {
		ScoreType type = new ScoreType().memcachedService.get("ScoreType" + scoreType);
		if (type == null) {
			type = ScoreType.findScoreType(scoreType);
		}
		if (type != null) {
			new ScoreType().memcachedService.checkToSet("ScoreType" + scoreType, type);
		}
		return type;
	}

	public static void findAllScoreTypes(Map<String, Object> conditionMap, Page<ScoreType> page) {
		EntityManager em = ScoreType.entityManager();
		String sql = "SELECT o FROM ScoreType o ";
		String countSql = "SELECT count(*) FROM ScoreType o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		List<Sort> sortList = page.fetchSort();
		StringBuilder orderSql = new StringBuilder(" ORDER BY ");
		if (page.isOrderBySetted()) {
			for (Sort sort : sortList) {
				orderSql.append(" " + sort.getProperty() + " " + sort.getDir() + ",");
			}
			orderSql.delete(orderSql.length() - 1, orderSql.length());
		} else {
			orderSql.append(" o.scoreType ASC ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<ScoreType> q = em.createQuery(tsql, ScoreType.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<ScoreType> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

}
