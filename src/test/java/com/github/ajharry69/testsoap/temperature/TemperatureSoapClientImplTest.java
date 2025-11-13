package com.github.ajharry69.testsoap.temperature;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TemperatureSoapClientImplTest {

    @Test
    void getTemperature_success_returnsResponse() {
        WebServiceTemplate template = Mockito.mock(WebServiceTemplate.class);
        TemperatureSoapClientImpl client = new TemperatureSoapClientImpl(template);

        TemperatureResponse response = new TemperatureResponse();
        // Service returns text value; our model stores String
        // Using reflection to set private field via Lombok-generated setter is not available; use accessor
        response.setDegreesCelsius("0");

        when(template.marshalSendAndReceive(anyString(), Mockito.<Object>any())).thenReturn(response);

        TemperatureRequest req = new TemperatureRequest();
        req.setFahrenheitReading("32");
        TemperatureResponse actual = client.getTemperature(req);

        assertThat(actual).isNotNull();
        assertThat(actual.getDegreesCelsius()).isEqualTo("0");
    }

    @Test
    void getTemperature_fault_returnsEmptyResponse() {
        WebServiceTemplate template = Mockito.mock(WebServiceTemplate.class);
        TemperatureSoapClientImpl client = new TemperatureSoapClientImpl(template);

        // Throw a mocked SoapFaultClientException to simulate SOAP Fault from server
        SoapFaultClientException fault = Mockito.mock(SoapFaultClientException.class);
        when(template.marshalSendAndReceive(anyString(), Mockito.<Object>any())).thenThrow(fault);

        TemperatureRequest req = new TemperatureRequest();
        req.setFahrenheitReading("bad");
        TemperatureResponse actual = client.getTemperature(req);

        assertThat(actual).isNotNull();
        // Our implementation returns a new TemperatureResponse with null value on fault
        assertThat(actual.getDegreesCelsius()).isNull();
    }
}
