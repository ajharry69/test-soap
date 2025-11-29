package com.github.ajharry69.testsoap.bpm.common.headers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestMessageIdRetrieverTest {

    @Test
    void whenMultipartPayloadParamContainsJson_thenExtractsMessageId() {
        var req = new MockHttpServletRequest("POST", "/any");
        req.setContentType("multipart/form-data; boundary=----WebKitFormBoundary");

        //language=JSON
        var json = """
                {
                  "messageID": "123e4567-e89b-12d3-a456-426614174000",
                  "primaryData": {
                    "example": "Value"
                  },
                  "additionalData": []
                }""";
        req.setParameter("payload", json);
        req.setParameter("requestSignature", "example");

        var retriever = new RequestMessageIdRetriever.Default(new ObjectMapper());
        var actual = retriever.retrieveMessageID(req);

        assertEquals("123e4567-e89b-12d3-a456-426614174000", actual);
    }

    @Test
    void whenApplicationJsonBody_thenExtractsMessageId() {
        var req = new MockHttpServletRequest("POST", "/any");
        req.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");
        // language=JSON
        var json = """
                {
                  "messageID": "abc-001",
                  "primaryData": {
                    "example": "Value"
                  },
                  "additionalData": []
                }""";
        req.setContent(json.getBytes(UTF_8));

        var retriever = new RequestMessageIdRetriever.Default(new ObjectMapper());
        var actual = retriever.retrieveMessageID(req);

        assertEquals("abc-001", actual);
    }

    @ParameterizedTest
    @MethodSource
    void whenMissingOrMalformed_thenReturnsNull(HttpServletRequest request) {
        var retriever = new RequestMessageIdRetriever.Default(new ObjectMapper());

        assertNull(retriever.retrieveMessageID(request));
    }

    static Stream<HttpServletRequest> whenMissingOrMalformed_thenReturnsNull() {
        var req1 = new MockHttpServletRequest("POST", "/any");
        req1.setContentType("application/json");
        req1.setContent("not-json".getBytes(UTF_8));

        var req2 = new MockHttpServletRequest("POST", "/any");
        req2.setContentType("multipart/form-data");
        req2.setParameter("payload", "{}");

        var req3 = new MockHttpServletRequest("POST", "/any");
        req3.setContentType("multipart/form-data");
        req3.setParameter("requestSignature", "example");

        var req4 = new MockHttpServletRequest("POST", "/any");
        req4.setContentType("multipart/form-data");
        req4.setParameter("payload", " 9 ");

        var req5 = new MockHttpServletRequest("POST", "/any");

        return Stream.of(req1, req2, req3, req4, req5);
    }
}
