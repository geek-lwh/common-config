package com.aha.tech.aop;

import com.aha.tech.util.TracerUtils;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

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
            Tracer.SpanBuilder spanBuilder = tracer.buildSpan(TracerUtils.CLASS_METHOD)
                    .withTag(TracerUtils.CLASS, cls)
                    .withTag(TracerUtils.METHOD, mName);

            Span parentSpan = tracer.activeSpan();
            if (parentSpan != null) {
                spanBuilder.asChildOf(parentSpan).start();
            }
            Span childSpan = spanBuilder.start();

            try (Scope scope = tracer.scopeManager().activate(childSpan)) {
                return pjp.proceed();
            } catch (Exception e) {
                Tags.ERROR.set(childSpan, true);
                Map err = TracerUtils.errorTraceMap(e);
                childSpan.log(err);
                throw e;
            } finally {
                childSpan.finish();
            }
        }

        return pjp.proceed();
    }
}
