package com.github.ajharry69.testsoap.countries;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CountryServiceTest {

    @Test
    void get_returns_response_from_client() {
        CountrySoapClient client = req -> {
            CountriesResponse r = new CountriesResponse();
            CountryResponse c = new CountryResponse();
            c.setIsoCode("AX");
            c.setName("Åland Islands");
            r.setCountries(List.of(c));
            return r;
        };

        var service = new CountryService(client);

        var actual = service.get();

        assertThat(actual.getCountries().stream().map(CountryResponse::getIsoCode))
                .isEqualTo(List.of("AX"));
    }
}
