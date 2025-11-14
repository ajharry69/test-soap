package com.github.ajharry69.testsoap.countries;

import com.github.ajharry69.testsoap.SoapClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountrySoapClientImplTest {
    @Mock
    private RestClient.Builder builder;
    @Mock
    private RestClient rt;
    @Mock
    private RestClient.RequestBodyUriSpec uriSpec;
    @Mock
    private RestClient.RequestBodySpec bodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    private CountrySoapClientImpl client;

    @BeforeEach
    void setUp() {
        when(builder.build())
                .thenReturn(rt);
        this.client = new CountrySoapClientImpl(builder, new SoapClientConfig().jaxbMarshaller());
    }

    @Test
    void getCountries_success_maps_list() {
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

        when(rt.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso"))).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenReturn(bodySpec);
        when(bodySpec.body(Mockito.<Object>any())).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(envelope);

        var actual = Objects.requireNonNull(client.getCountries(new CountriesRequest()));

        assertThat(actual.getCountries().stream().map(CountryResponse::getIsoCode).toList())
                .isEqualTo(List.of("AX", "AF"));

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(bodySpec).body(bodyCaptor.capture());
        String sentEnvelope = (String) bodyCaptor.getValue();
        assertThat(sentEnvelope)
                .contains("<ListOfCountryNamesByName")
                .contains("https://soap-service-free.mock.beeceptor.com/CountryInfoService");
    }

    @Test
    void getCountries_when_error_returns_empty_list() {
        when(rt.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso"))).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenReturn(bodySpec);
        when(bodySpec.body(Mockito.<Object>any())).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenThrow(new RuntimeException("boom"));

        var actual = client.getCountries(new CountriesRequest());

        assertThat(actual.getCountries()).isEmpty();
    }

    @Test
    void getCountries_when_blank_payload_returns_empty_list() {
        when(rt.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso"))).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenReturn(bodySpec);
        when(bodySpec.body(Mockito.<Object>any())).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn("   ");

        var actual = client.getCountries(new CountriesRequest());

        assertThat(actual.getCountries()).isEmpty();
    }

    @Test
    void getCountries_when_response_missing_target_element_returns_empty_list() {
        // language=XML
        String envelope = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <m:SomethingElse xmlns:m="http://www.oorsprong.org/websamples.countryinfo"/>
                  </soap:Body>
                </soap:Envelope>
                """;

        when(rt.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso"))).thenReturn(bodySpec);
        when(bodySpec.headers(any())).thenReturn(bodySpec);
        when(bodySpec.body(any())).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(String.class))).thenReturn(envelope);

        var actual = client.getCountries(new CountriesRequest());

        assertThat(actual.getCountries()).isEmpty();
    }
}
