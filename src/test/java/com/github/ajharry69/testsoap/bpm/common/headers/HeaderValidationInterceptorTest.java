package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeadersValidationException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.RegexValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HeaderValidationInterceptorTest {

    private static MockHttpServletRequest getMockHttpServletRequest() {
        var request = new MockHttpServletRequest();
        request.addHeader("X-FeatureCode", "101");
        request.addHeader("X-FeatureName", "BPM");
        request.addHeader("X-ServiceCode", "10001");
        request.addHeader("X-ServiceName", "ke-kcb-salesforce-bpm-st-v1.0.0");
        request.addHeader("X-ChannelCode", "10");
        request.addHeader("X-ChannelCategory", "102");
        request.addHeader("X-ChannelName", "App");
        request.addHeader("X-RouteCode", "SFA");
        request.addHeader("X-TimeStamp", "1750844604");
        request.addHeader("X-ServiceMode", "NA");
        request.addHeader("X-SubscriberEvents", "NA");
        request.addHeader("X-CallBackURL", "https://example.com/callback");
        request.addHeader("X-ServiceSubCategory", "upload-document");
        request.addHeader("X-MinorServiceVersion", "1.0");
        return request;
    }

    @Test
    void whenNoProperties_thenPreHandlePasses() {
        var props = new HeaderValidationProperties();
        var interceptor = new HeaderValidationInterceptor(props);

        var request = getMockHttpServletRequest();

        assertTrue(interceptor.preHandle(request, null, null));
    }

    @Test
    void whenValidHeaders_thenPreHandleReturnsTrue() {
        var rule = HeaderRule.builder()
                .headerName("X-Custom")
                .validator((n, v) -> v.equals("OK"))
                .build();
        var props = new HeaderValidationProperties();
        props.setHeaders(Set.of(rule));
        var interceptor = new HeaderValidationInterceptor(props);

        var request = getMockHttpServletRequest();
        request.addHeader("X-Custom", "OK");

        assertTrue(interceptor.preHandle(request, null, null));
    }

    @Test
    void whenMissingRequired_thenThrowsAggregatedException() {
        var required = HeaderRule.builder().headerName("X-Req").required(true).build();
        var invalid = HeaderRule.builder().headerName("X-Invalid").required(true)
                .validator(new RegexValidator("^v\\d+$")).build();

        var props = new HeaderValidationProperties();
        props.setHeaders(new HashSet<>(Set.of(required, invalid)));
        var interceptor = new HeaderValidationInterceptor(props);

        var request = getMockHttpServletRequest();
        request.addHeader("X-Invalid", "bad");

        var ex = assertThrows(HeadersValidationException.class,
                () -> interceptor.preHandle(request, null, null));

        assertAll(
                () -> assertEquals(2, ex.getHeaderExceptions().size()),
                () -> assertTrue(ex.getHeaderExceptions().stream().anyMatch(e -> e instanceof MissingHeaderException &&
                        e.getRule().getHeaderName().equals("X-Req"))),
                () -> assertTrue(ex.getHeaderExceptions().stream().anyMatch(e -> e instanceof InvalidHeaderValueException &&
                        e.getRule().getHeaderName().equals("X-Invalid")))
        );
    }
}