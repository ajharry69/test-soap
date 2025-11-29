package com.github.ajharry69.testsoap.bpm.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class KCBRequestFilterTest {

    @Test
    void contextIsSetForChainAndClearedAfterwards() throws Exception {
        var request = new MockHttpServletRequest("GET", "/any");
        var response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            var context = KCBRequestContextHolder.getContext();
            assertNotNull(context.conversationID());
            ((HttpServletResponse) res).setStatus(200);
        };

        var filter = new KCBRequestFilter();
        filter.doFilter(request, response, chain);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> {
                    // After filter returns, context should be cleared
                    assertNull(KCBRequestContextHolder.getContext());
                }
        );
    }
}
