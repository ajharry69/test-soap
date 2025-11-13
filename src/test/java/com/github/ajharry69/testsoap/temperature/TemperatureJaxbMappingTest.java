package com.github.ajharry69.testsoap.temperature;

import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class TemperatureJaxbMappingTest {

    @Test
    void unmarshal_response_with_namespace_maps_value() throws Exception {
        // language=XML
        String xml = """
            <FahrenheitToCelsiusResponse xmlns="https://www.w3schools.com/xml/">
                <FahrenheitToCelsiusResult>23.8888888888889</FahrenheitToCelsiusResult>
            </FahrenheitToCelsiusResponse>
            """;

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.github.ajharry69.testsoap.temperature");
        marshaller.afterPropertiesSet();

        var source = new StreamSource(new StringReader(xml));
        TemperatureResponse actual = (TemperatureResponse) marshaller.unmarshal(source);

        assertThat(actual.getDegreesCelsius()).isEqualTo("23.8888888888889");
    }
}
