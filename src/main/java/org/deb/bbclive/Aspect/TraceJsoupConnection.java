package org.deb.bbclive.Aspect;

import io.opentracing.Tracer;
import io.opentracing.contrib.concurrent.TracedCallable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;

@Aspect
public class TraceJsoupConnection {
    @Autowired
    private Tracer tracer;

//    public TraceJsoupConnection(Tracer tracer) {
//        this.tracer = tracer;
//        System.out.printf("%s", "=============================================================");
//        System.out.printf("%s", "TraceJsoupConnection instance created, what happens then ?");
//        System.out.printf("%s", "=============================================================");
//    }

    @Pointcut("execution(public * *(..))")
    public void anyPublicOperation() {}

    @Pointcut("within(org.jsoup..*)")
    public void inJsoup() {}

    @Pointcut("anyPublicOperation() && inJsoup()")
    public void jsoupOperation() {}



    @Before("concurrentOperation()")
    public Object jsoupOperation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Callable<Object> delegate = (Callable)proceedingJoinPoint.proceed();
        System.out.println("Jsoup Operation Before called");
        return new TracedCallable(delegate, this.tracer);
    }

    @Around("concurrentOperation()")
    public Object traceAfterPublicAsyncMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Callable<Object> delegate = (Callable)proceedingJoinPoint.proceed();
        System.out.println("Jsoup Operation around called");
        return new TracedCallable(delegate, this.tracer);
    }

    @AfterReturning("concurrentOperation()")
    public Object traceAfterReturningPublicAsyncMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Callable<Object> delegate = (Callable)proceedingJoinPoint.proceed();
        System.out.println("Jsoup Operation after returning called");
        return new TracedCallable(delegate, this.tracer);
    }

    @After("concurrentOperation()")
    public void doCloseTraces() {
        // ...
        System.out.println("After Jsoup Operation.....");
    }
}
