package com.github.ajharry69.testsoap.temperature;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FahrenheitToCelsius", namespace = "https://www.w3schools.com/xml/")
class TemperatureRequest {
    @XmlElement(name = "Fahrenheit", namespace = "https://www.w3schools.com/xml/")
    private String fahrenheitReading;
}

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FahrenheitToCelsiusResponse", namespace = "https://www.w3schools.com/xml/")
class TemperatureResponse {
    @XmlElement(name = "FahrenheitToCelsiusResult", namespace = "https://www.w3schools.com/xml/")
    private String degreesCelsius;
}

interface TemperatureSoapClient {
    TemperatureResponse getTemperature(TemperatureRequest request);
}

@Component
@Slf4j
class TemperatureSoapClientImpl implements TemperatureSoapClient {
    private final WebServiceTemplate webServiceTemplate;
    private final SoapMessageFactory messageFactory;

    TemperatureSoapClientImpl(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
        this.messageFactory = createMessageFactory();
    }

    @Override
    public TemperatureResponse getTemperature(TemperatureRequest request) {
        webServiceTemplate.setMessageFactory(this.messageFactory);
        try {
            return (TemperatureResponse) webServiceTemplate.marshalSendAndReceive(
                    "https://www.w3schools.com/xml/tempconvert.asmx",
                    request
            );
        } catch (SoapFaultClientException e) {
            log.error("Error converting temperature", e);
            return new TemperatureResponse();
        }
    }

    private SoapMessageFactory createMessageFactory() {
        var messageFactory = new SaajSoapMessageFactory();
        try {
            messageFactory.setMessageFactory(MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL));
        } catch (SOAPException e) {
            log.error("Error creating SOAP message factory", e);
        }
        messageFactory.afterPropertiesSet();
        return messageFactory;
    }
}

@Slf4j
@AllArgsConstructor
@Service
class TemperatureService {
    private final TemperatureSoapClient client;

    public List<TemperatureResponse> get(TemperatureRequest request) {
        return List.of(client.getTemperature(request));
    }
}

@AllArgsConstructor
@RestController
@RequestMapping("/temperature")
class TemperatureController {
    private final TemperatureService service;

    @GetMapping
    public ResponseEntity<List<TemperatureResponse>> get(@RequestParam String fahrenheitReading) {
        var request = new TemperatureRequest();
        request.setFahrenheitReading(fahrenheitReading);
        return ResponseEntity.ok(service.get(request));
    }
}