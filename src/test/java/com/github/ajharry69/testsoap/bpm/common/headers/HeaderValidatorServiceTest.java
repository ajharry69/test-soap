package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.RegexValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderValidatorServiceTest {

    private final HeaderValidatorService service = new HeaderValidatorService();

    @Test
    void whenRequiredAndMissing_thenThrowsMissingHeaderException() {
        var rule = HeaderRule.builder().headerName("X-Req").required(true).build();

        assertAll(
                () -> assertThrows(MissingHeaderException.class, () -> service.validate(rule, "")),
                () -> assertThrows(MissingHeaderException.class, () -> service.validate(rule, null)),
                () -> assertThrows(MissingHeaderException.class, () -> service.validate(rule, " "))
        );
    }

    @Test
    void whenOptionalAndMissing_thenDoesNothing() {
        var rule = HeaderRule.builder().headerName("X-Opt").required(false).build();

        assertAll(
                () -> assertDoesNotThrow(() -> service.validate(rule, "")),
                () -> assertDoesNotThrow(() -> service.validate(rule, null)),
                () -> assertDoesNotThrow(() -> service.validate(rule, " "))
        );
    }

    @Test
    void whenPresentButInvalid_thenThrowsInvalidHeaderValueException() {
        var rule = HeaderRule.builder().headerName("X-Regex").required(true)
                .validator(new RegexValidator("^v\\d+$")).build();

        assertThrows(InvalidHeaderValueException.class, () -> service.validate(rule, "bad"));
    }

    @Test
    void whenPresentAndValid_thenPasses() {
        var rule = HeaderRule.builder().headerName("X-Regex").required(true)
                .validator(new RegexValidator("^v\\d+$")).build();

        assertDoesNotThrow(() -> service.validate(rule, "v1"));
    }
}