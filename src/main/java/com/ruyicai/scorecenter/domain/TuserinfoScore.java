package com.ruyicai.scorecenter.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Entity()
@Table(name = "TUSERINFOSCORE")
public class TuserinfoScore implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "SCORE")
	private BigDecimal score;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "LASTMODIFYTIME")
	private Date lastModifyTime;

}