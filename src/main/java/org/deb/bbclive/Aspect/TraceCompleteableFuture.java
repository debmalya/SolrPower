package org.deb.bbclive.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

@Aspect
@Component
public class TraceCompleteableFuture {
	private static final Logger LOGGER = LoggerFactory.getLogger(TraceCompleteableFuture.class);
	@Autowired
	private Tracer tracer;

	public TraceCompleteableFuture() {

	}

	@Pointcut("execution(public java.util.concurrent.CompletableFuture *(..))")
	public void inConcurrent() {
	}

	@Before("inConcurrent()")
	public void beforeSampleCreation(JoinPoint jp) {
		String methodName = "";
		if (jp != null && jp.getSignature() != null) {
			methodName = jp.getSignature().getName();
		}
		Span activeSpan = tracer.activeSpan();
		if (tracer.activeSpan() != null) {
			activeSpan.log(System.currentTimeMillis(), methodName);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("A request was issued form method: " + methodName + " active span :" + activeSpan);
		}
	}

	@AfterReturning("inConcurrent()")
	public void afterReturning(JoinPoint jp) {
		String methodName = "";
		if (jp != null && jp.getSignature() != null) {
			methodName = jp.getSignature().getName();
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("A request completed form method: " + methodName);
		}

	}

}
