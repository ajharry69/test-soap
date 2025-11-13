package com.github.ajharry69.testsoap.temperature;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TemperatureServiceTest {

    @Test
    void get_returns_list_with_converted_value_from_client() {
        TemperatureSoapClient fake = req -> {
            var r = new TemperatureResponse();
            r.setDegreesCelsius("0");
            return r;
        };
        var service = new TemperatureService(fake);

        var actual = service.get(new TemperatureRequest());

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(Objects.requireNonNull(actual).getFirst().getDegreesCelsius()).isEqualTo("0")
        );
    }

    @Test
    void get_when_fault_returns_list_with_null_value() {
        TemperatureSoapClient fake = req -> new TemperatureResponse();
        var service = new TemperatureService(fake);

        var actual = service.get(new TemperatureRequest());

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(Objects.requireNonNull(actual).getFirst().getDegreesCelsius()).isNull()
        );
    }
}
