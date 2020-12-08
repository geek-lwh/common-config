package com.aha.tech.aop;

import com.aha.tech.util.TraceUtil;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author: luweihong
 * @Date: 2020/11/11
 */
@Aspect
@Component
public class TraceAop {

    @Around("@annotation(com.aha.tech.annotation.Trace)")
    public Object pxTraceProcess(ProceedingJoinPoint pjp) throws Throwable {
        Tracer tracer = GlobalTracer.get();
        if (tracer != null) {
            final String cls = pjp.getTarget().getClass().getName();
            final String mName = pjp.getSignature().getName();
            Tracer.SpanBuilder spanBuilder = tracer.buildSpan(cls + "#" + mName)
                    .withTag(TraceUtil.CLASS, cls)
                    .withTag(TraceUtil.METHOD, mName);

            Span parentSpan = tracer.activeSpan();
            if (parentSpan != null) {
                spanBuilder.asChildOf(parentSpan);
            }

            Span span = spanBuilder.start();
            try (Scope scope = tracer.scopeManager().activate(span)) {
                TraceUtil.setTraceIdTags(span);
                return pjp.proceed();
            } catch (Exception e) {
                TraceUtil.setCapturedErrorsTags(e);
                throw e;
            } finally {
                span.finish();
            }
        }

        return pjp.proceed();
    }
}
