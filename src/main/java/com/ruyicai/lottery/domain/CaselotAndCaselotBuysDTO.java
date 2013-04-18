package com.ruyicai.lottery.domain;

import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class CaselotAndCaselotBuysDTO {
	List<CaseLotBuyAndUserDTO> caseLotBuyAndUser;
	CaseLot caseLot;

	public CaselotAndCaselotBuysDTO(List<CaseLotBuyAndUserDTO> caseLotBuyAndUser, CaseLot caseLot) {
		super();
		this.caseLotBuyAndUser = caseLotBuyAndUser;
		this.caseLot = caseLot;
	}
}