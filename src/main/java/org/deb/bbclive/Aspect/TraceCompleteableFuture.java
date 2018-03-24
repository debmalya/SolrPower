package org.deb.bbclive.Aspect;

import io.opentracing.Tracer;
import io.opentracing.contrib.concurrent.TracedCallable;
import io.opentracing.util.GlobalTracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Callable;

@Aspect
@Component
public class TraceCompleteableFuture {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceCompleteableFuture.class);
    private Tracer tracer;

    public TraceCompleteableFuture() {
        tracer = GlobalTracer.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("=============================================================");
            LOGGER.debug("TraceCompleteableFuture instance created, what happens then ?");
            LOGGER.debug("Tracer :" + tracer);
            LOGGER.debug("=============================================================");
        }
    }

        @Pointcut("execution(public java.util.concurrent.CompletableFuture *(..))")
//    @Pointcut("within(@com.deb.bbclive *)")
//    @Pointcut("target(java.util.concurrent.CompletableFuture)")
//    @Pointcut("execution(* com.deb...bbclive.*.*(..))")
    public void inConcurrent() {
    }

    @Before("inConcurrent()")
    //    @Before("execution(* com.gkatzioura.spring.aop.service.SampleService.createSample (java.lang.String)) && args(sampleName)")
//    @Before("execution(* org.deb.bbclive.Crawler.process (java.util.Set<String> , org.deb.bbclive.dao.SolrDao)) && args(uriSetToCrawl,solrDao)")
    public void beforeSampleCreation() {

        LOGGER.info("A request was issued for a with URLs: " );
    }


//    @Around("inConcurrent()")
//    public Object traceAfterPublicAsyncMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        Callable<Object> delegate = (Callable) proceedingJoinPoint.proceed();
//        System.out.println("Around called");
//        return new TracedCallable(delegate, this.tracer);
//    }


}
