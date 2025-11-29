package com.github.ajharry69.testsoap.bpm.common.headers;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HeaderValidationPropertiesTest {

    @Test
    void getHeaders_containsDefaults() {
        var props = new HeaderValidationProperties();

        var actual = props.getHeaders();

        assertAll(
                () -> assertFalse(actual.isEmpty()),
                () -> assertTrue(actual.contains(HeaderRule.builder().headerName("X-FeatureCode").build())),
                () -> assertTrue(actual.contains(HeaderRule.builder().headerName("X-FeatureName").build())),
                () -> assertTrue(actual.contains(HeaderRule.builder().headerName("X-ChannelCode").build())),
                () -> assertTrue(actual.contains(HeaderRule.builder().headerName("X-CallBackURL").build()))
        );
    }

    @Test
    void setHeaders_thenGetHeaders_mergesWithDefaults() {
        var props = new HeaderValidationProperties();
        var custom = HeaderRule.builder().headerName("X-Custom").required(false).build();
        props.setHeaders(new HashSet<>(Set.of(custom)));

        var actual = props.getHeaders();

        assertAll(
                () -> assertTrue(actual.contains(custom)),
                () -> assertTrue(actual.contains(HeaderRule.builder().headerName("X-FeatureName").build()))
        );
    }
}
