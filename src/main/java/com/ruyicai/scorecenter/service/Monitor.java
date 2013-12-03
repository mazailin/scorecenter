package com.ruyicai.scorecenter.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class Monitor {

	private static Logger logger = LoggerFactory.getLogger(Monitor.class);

	private static long TIMEOUT = 5000l;

	@Pointcut(value = "execution(* com.ruyicai.scorecenter.service.LotteryService.*(..))")
	public void businessMethods() {
	}

	@Around("businessMethods()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		// 类名称
		String clazzString = pjp.getTarget().getClass().getName();
		// 方法名称
		String methodName = pjp.getSignature().getName();
		Object output = pjp.proceed();
		long elapsedTime = System.currentTimeMillis() - start;
		if (elapsedTime > TIMEOUT) {
			logger.warn("{}.{} cost time:{}", new String[] { clazzString, methodName, elapsedTime + "" });
		}
		return output;
	}
}
