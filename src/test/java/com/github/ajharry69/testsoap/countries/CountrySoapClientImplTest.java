package com.github.ajharry69.testsoap.countries;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CountrySoapClientImplTest {

    private static Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller m = new Jaxb2Marshaller();
        m.setPackagesToScan("com.github.ajharry69.testsoap.countries");
        try {
            m.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return m;
    }

    @Test
    void getCountries_success_maps_list() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
        RestTemplate rt = Mockito.mock(RestTemplate.class);
        when(builder.build()).thenReturn(rt);
        CountrySoapClientImpl client = new CountrySoapClientImpl(builder, marshaller());

        // language=XML
        String envelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <m:ListOfCountryNamesByNameResponse xmlns:m="http://www.oorsprong.org/websamples.countryinfo">
                      <m:ListOfCountryNamesByNameResult>
                        <m:tCountryCodeAndName>
                          <m:sISOCode>AX</m:sISOCode>
                          <m:sName>Åland Islands</m:sName>
                        </m:tCountryCodeAndName>
                        <m:tCountryCodeAndName>
                          <m:sISOCode>AF</m:sISOCode>
                          <m:sName>Afghanistan</m:sName>
                        </m:tCountryCodeAndName>
                      </m:ListOfCountryNamesByNameResult>
                    </m:ListOfCountryNamesByNameResponse>
                  </soap:Body>
                </soap:Envelope>
                """;

        when(rt.postForObject(eq("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso"), any(), eq(String.class)))
                .thenReturn(envelope);

        var actual = Objects.requireNonNull(client.getCountries(new CountriesRequest()));

        assertThat(actual.getCountries().stream().map(CountryResponse::getIsoCode).toList())
                .isEqualTo(List.of("AX", "AF"));
    }

    @Test
    void getCountries_when_error_returns_empty_list() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
        RestTemplate rt = Mockito.mock(RestTemplate.class);
        when(builder.build()).thenReturn(rt);
        CountrySoapClientImpl client = new CountrySoapClientImpl(builder, marshaller());

        when(rt.postForObject(eq("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso"), any(), eq(String.class)))
                .thenThrow(new RuntimeException("boom"));

        var actual = client.getCountries(new CountriesRequest());

        assertThat(actual.getCountries()).isEmpty();
    }
}
