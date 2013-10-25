package com.ruyicai.scorecenter.domain;

import java.util.Date;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(identifierType = TjmsservicePK.class, versionField = "", table = "TJMSSERVICE")
public class Tjmsservice {

	private String memo;

	private Date processtime;

	public static Tjmsservice createTjmsservice(String value, String type, Date processtime, String memo) {
		TjmsservicePK id = new TjmsservicePK(value, type);
		Tjmsservice tjmsservice = new Tjmsservice();
		tjmsservice.setId(id);
		tjmsservice.setMemo(memo);
		tjmsservice.setProcesstime(processtime);
		tjmsservice.persist();
		return tjmsservice;
	}

	public static Tjmsservice findTjmsservice(String value, String type) {
		return findTjmsservice(new TjmsservicePK(value, type));
	}
}
