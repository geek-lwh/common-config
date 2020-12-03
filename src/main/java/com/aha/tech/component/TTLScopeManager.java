package com.aha.tech.component;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;

/**
 * @Author: luweihong
 * @Date: 2020/12/3
 */
public class TTLScopeManager implements ScopeManager {

    final TransmittableThreadLocal<TTLScope> ttlScope = new TransmittableThreadLocal();

    @Override
    public Scope activate(Span span) {
        return new TTLScope(this, span);
    }

    @Override
    public Span activeSpan() {
        TTLScope scope = ttlScope.get();
        return scope == null ? null : scope.span();
    }

}
