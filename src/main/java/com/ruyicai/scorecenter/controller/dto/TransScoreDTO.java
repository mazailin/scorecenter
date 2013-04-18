package com.ruyicai.scorecenter.controller.dto;

import java.math.BigDecimal;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.scorecenter.domain.TuserinfoScore;

@RooJson
@RooJavaBean
public class TransScoreDTO {

	BigDecimal money;

	TuserinfoScore tuserinfoScore;

}
