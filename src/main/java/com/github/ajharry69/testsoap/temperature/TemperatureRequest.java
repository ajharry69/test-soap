package com.github.ajharry69.testsoap.temperature;

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
record TemperatureRequest(String name, String code) {
}

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response", namespace = "http://api-v1.gen.mm.vodafone.com/mminterface/response")
class TemperatureResponse {
    @XmlElement(name = "ResponseCode")
    private String name;

    @XmlElement(name = "ConversationID")
    private String code;
}

interface TemperatureSoapClient {
    TemperatureResponse getTemperature(TemperatureRequest request);
}

@Component
@Slf4j
@RequiredArgsConstructor
class TemperatureSoapClientImpl implements TemperatureSoapClient {
    private final WebServiceTemplate webServiceTemplate;
    private final Jaxb2Marshaller jaxbMarshaller;

    @Override
    public TemperatureResponse getTemperature(TemperatureRequest request) {
        return null;
    }
}

@Slf4j
@AllArgsConstructor
@Service
class TemperatureService {
    private final TemperatureSoapClient client;

    public List<TemperatureResponse> get() {
        var request = TemperatureRequest.builder()
                .build();
        return List.of(client.getTemperature(request));
    }
}

@AllArgsConstructor
@RestController
@RequestMapping("/temperature")
class TemperatureController {
    private final TemperatureService service;

    @GetMapping
    public ResponseEntity<List<TemperatureResponse>> get() {
        return ResponseEntity.ok(service.get());
    }
}