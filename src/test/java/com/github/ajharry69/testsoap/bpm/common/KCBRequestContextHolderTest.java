package com.github.ajharry69.testsoap.bpm.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KCBRequestContextHolderTest {

    @Test
    void setGetAndClearBehavior() {
        assertNull( KCBRequestContextHolder.getContext());

        var ctx = new KCBRequestContext("conv-123");
        KCBRequestContextHolder.setContext(ctx);
        assertEquals("conv-123", KCBRequestContextHolder.getContext().conversationID());

        KCBRequestContextHolder.clear();
        assertNull(KCBRequestContextHolder.getContext());
    }
}
