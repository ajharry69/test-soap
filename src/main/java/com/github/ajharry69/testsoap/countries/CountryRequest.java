package com.github.ajharry69.testsoap.countries;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.List;

@Builder
record CountryRequest(String name, String code) {
}

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response", namespace = "http://api-v1.gen.mm.vodafone.com/mminterface/response")
class CountryResponse {
    @XmlElement(name = "ResponseCode")
    private String responseCode;

    @XmlElement(name = "ConversationID")
    private String conversationID;

    @XmlElement(name = "ResponseDesc")
    private String responseDesc;

    @XmlElement(name = "OriginatorConversationID")
    private String originatorConversationID;

    @XmlElement(name = "ServiceStatus")
    private String serviceStatus;
}

interface CountrySoapClient {
    CountryResponse getCountry(CountryRequest request);
}

@Component
@Slf4j
@RequiredArgsConstructor
class CountrySoapClientImpl implements CountrySoapClient {
    private final WebServiceTemplate webServiceTemplate;
    private final Jaxb2Marshaller jaxbMarshaller;

    @Override
    public CountryResponse getCountry(CountryRequest request) {
        return null;
    }
}

@Slf4j
@AllArgsConstructor
@Service
class CountryService {
    private final CountrySoapClient client;

    public List<CountryResponse> get() {
        var request = CountryRequest.builder()
                .build();
        return List.of(client.getCountry(request));
    }
}

@AllArgsConstructor
@RestController
@RequestMapping("/countries")
class CountriesController {
    private final CountryService service;

    @GetMapping
    public ResponseEntity<List<CountryResponse>> get() {
        return ResponseEntity.ok(service.get());
    }
}