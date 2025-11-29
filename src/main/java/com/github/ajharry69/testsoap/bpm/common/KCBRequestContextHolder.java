package com.github.ajharry69.testsoap.bpm.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class KCBRequestContextHolder {
    private static final ThreadLocal<KCBRequestContext> CONTEXT = new ThreadLocal<>();

    private KCBRequestContextHolder() {
    }

    public static void setContext(KCBRequestContext context) {
        log.info("Setting request context: {}", context);
        CONTEXT.set(context);
    }

    public static KCBRequestContext getContext() {
        log.info("Getting request context...");
        return CONTEXT.get();
    }

    public static void clear() {
        log.info("Clearing request context...");
        CONTEXT.remove();
    }
}
