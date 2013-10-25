package com.ruyicai.scorecenter.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.scorecenter.domain.Tjmsservice;

@Service
public class TjmsserviceService {

	private Logger logger = LoggerFactory.getLogger(TjmsserviceService.class);

	@Transactional
	public boolean createTjmsservice(String value, String type, String memo) {
		try {
			Tjmsservice tjmsservice = Tjmsservice.findTjmsservice(value, type);
			if (tjmsservice == null) {
				Tjmsservice.createTjmsservice(value, type, new Date(), memo);
				return true;
			}
		} catch (Exception e) {
			logger.error("数据库已存在记录, value:" + value + ", type:" + type + ", memo:" + memo);
		}
		return false;
	}
}
