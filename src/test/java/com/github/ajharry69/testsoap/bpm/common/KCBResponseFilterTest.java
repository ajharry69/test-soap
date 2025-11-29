package com.github.ajharry69.testsoap.bpm.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class KCBResponseFilterTest {

    @Test
    void shouldSetElapsedTimeHeaderWhenXTimestampPresent() throws Exception {
        var request = new MockHttpServletRequest("GET", "/any");
        // KCBResponseFilter expects X-TimeStamp in seconds (epoch second), not millis
        request.addHeader("X-TimeStamp", String.valueOf(System.currentTimeMillis() / 1000));

        var response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((HttpServletResponse) res).setStatus(200);

        var filter = new KCBResponseFilter();
        filter.doFilter(request, response, chain);

        var header = response.getHeader("X-ElapsedTime");
        assertNotNull(header, "X-ElapsedTime header should be set when X-TimeStamp is provided");
        long elapsed = Long.parseLong(header);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> {
                    // elapsed should be non-negative and within a reasonable bound for the test run
                    assertTrue(elapsed >= 0 && elapsed < 60_000, "elapsed should be >= 0 and < 60s");
                }
        );
    }

    @Test
    void shouldNotSetElapsedTimeHeaderWhenXTimestampMissing() throws Exception {
        var request = new MockHttpServletRequest("GET", "/any");
        var response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((HttpServletResponse) res).setStatus(200);

        var filter = new KCBResponseFilter();
        filter.doFilter(request, response, chain);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertNull(
                        response.getHeader("X-ElapsedTime"),
                        "X-ElapsedTime should not be set when X-TimeStamp is missing"
                )
        );
    }
}
