package com.github.ajharry69.testsoap.countries;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListOfCountryNamesByName", namespace = "https://soap-service-free.mock.beeceptor.com/CountryInfoService")
class CountriesRequest {
}

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tCountryCodeAndName", namespace = "http://www.oorsprong.org/websamples.countryinfo")
class CountryResponse {
    @XmlElement(name = "sISOCode", namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private String isoCode;

    @XmlElement(name = "sName", namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private String name;
}

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListOfCountryNamesByNameResponse", namespace = "http://www.oorsprong.org/websamples.countryinfo")
class CountriesResponse {
    @XmlElementWrapper(name = "ListOfCountryNamesByNameResult", namespace = "http://www.oorsprong.org/websamples.countryinfo")
    @XmlElement(name = "tCountryCodeAndName", namespace = "http://www.oorsprong.org/websamples.countryinfo")
    private List<CountryResponse> countries;
}

interface CountrySoapClient {
    CountriesResponse getCountries(CountriesRequest request);
}

@Component
@Slf4j
class CountrySoapClientImpl implements CountrySoapClient {
    private final RestClient restClient;
    private final Jaxb2Marshaller marshaller;

    public CountrySoapClientImpl(RestClient.Builder builder, Jaxb2Marshaller marshaller) {
        this.restClient = builder.build();
        this.marshaller = marshaller;
    }

    @Override
    public CountriesResponse getCountries(CountriesRequest request) {
        try {
            // Build SOAP 1.1 envelope
            // language=XML
            String envelope = """
                    <?xml version="1.0" encoding="utf-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="https://soap-service-free.mock.beeceptor.com/CountryInfoService">
                      <soap:Body>
                        <tns:ListOfCountryNamesByName/>
                      </soap:Body>
                    </soap:Envelope>
                    """;

            var xml = restClient.post()
                    .uri("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso")
                    .headers(headers -> {
                        headers.setContentType(MediaType.TEXT_XML);
                        headers.setAccept(List.of(MediaType.TEXT_XML, MediaType.APPLICATION_XML));
                        headers.add("SOAPAction", "https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso/ListOfCountryNamesByName");
                    })
                    .body(envelope)
                    .retrieve()
                    .body(String.class);

            // Parse and extract the payload element
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            var doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            var nl = doc.getElementsByTagNameNS("http://www.oorsprong.org/websamples.countryinfo", "ListOfCountryNamesByNameResponse");
            if (nl.getLength() == 0) return getEmptyCountriesResponse();

            var responseEl = nl.item(0);
            var source = new DOMSource(responseEl);
            return (CountriesResponse) marshaller.unmarshal(source);
        } catch (Exception e) {
            log.error("Error fetching countries", e);
            return getEmptyCountriesResponse();
        }
    }

    private static CountriesResponse getEmptyCountriesResponse() {
        var r = new CountriesResponse();
        r.setCountries(Collections.emptyList());
        return r;
    }
}

@Slf4j
@AllArgsConstructor
@Service
class CountryService {
    private final CountrySoapClient client;

    public CountriesResponse get() {
        var request = new CountriesRequest();
        return client.getCountries(request);
    }
}

@AllArgsConstructor
@RestController
@RequestMapping("/countries")
class CountriesController {
    private final CountryService service;

    @GetMapping
    public ResponseEntity<CountriesResponse> get() {
        return ResponseEntity.ok(service.get());
    }
}