package com.github.ajharry69.testsoap.countries;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

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
    private List<CountryResponse> countries;
}

interface CountrySoapClient {
    CountriesResponse getCountries(CountriesRequest request);
}

@Component
@Slf4j
@RequiredArgsConstructor
class CountrySoapClientImpl implements CountrySoapClient {
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public CountriesResponse getCountries(CountriesRequest request) {
        try {
            return (CountriesResponse) webServiceTemplate.marshalSendAndReceive(
                    "https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso",
                    request,
                    message -> ((SoapMessage) message)
                            .setSoapAction("https://soap-service-free.mock.beeceptor.com/CountryInfoService.wso/ListOfCountryNamesByName")
            );
        } catch (SoapFaultClientException e) {
            log.error("Error converting temperature", e);
            var response = new CountriesResponse();
            response.setCountries(Collections.emptyList());
            return response;
        }
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