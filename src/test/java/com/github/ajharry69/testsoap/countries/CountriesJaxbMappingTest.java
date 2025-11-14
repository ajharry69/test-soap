package com.github.ajharry69.testsoap.countries;

import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CountriesJaxbMappingTest {

    @Test
    void unmarshal_response_with_namespace_maps_list_items() throws Exception {
        // language=XML
        String xml = """
            <ListOfCountryNamesByNameResponse xmlns="http://www.oorsprong.org/websamples.countryinfo">
              <ListOfCountryNamesByNameResult>
                <tCountryCodeAndName>
                  <sISOCode>AX</sISOCode>
                  <sName>Åland Islands</sName>
                </tCountryCodeAndName>
                <tCountryCodeAndName>
                  <sISOCode>AF</sISOCode>
                  <sName>Afghanistan</sName>
                </tCountryCodeAndName>
              </ListOfCountryNamesByNameResult>
            </ListOfCountryNamesByNameResponse>
            """;

        var marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.github.ajharry69.testsoap.countries");
        marshaller.afterPropertiesSet();

        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        var doc = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        CountriesResponse response = (CountriesResponse) marshaller.unmarshal(new DOMSource(doc.getDocumentElement()));

        assertThat(response).isNotNull();
        assertThat(response.getCountries()).isNotNull();
        assertThat(response.getCountries()).hasSize(2);
        assertThat(response.getCountries().getFirst().getIsoCode()).isEqualTo("AX");
        assertThat(response.getCountries().getFirst().getName()).isEqualTo("Åland Islands");
    }
}
