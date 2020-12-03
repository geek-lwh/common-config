package com.aha.tech.component;

import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * @Author: luweihong
 * @Date: 2020/12/3
 */
public class TTLScope implements Scope {

    private final TTLScopeManager scopeManager;
    private final Span wrapped;
    private final TTLScope toRestore;

    TTLScope(TTLScopeManager scopeManager, Span wrapped) {
        this.scopeManager = scopeManager;
        this.wrapped = wrapped;
        this.toRestore = scopeManager.ttlScope.get();
        scopeManager.ttlScope.set(this);
    }

    @Override
    public void close() {
        if (scopeManager.ttlScope.get() != this) {
            // This shouldn't happen if users call methods in the expected order. Bail out.
            return;
        }

        scopeManager.ttlScope.set(toRestore);
    }

    public Span span() {
        return this.wrapped;
    }
}
