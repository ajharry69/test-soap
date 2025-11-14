package com.github.ajharry69.testsoap.temperature;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

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
        log.info("Converting {} Fahrenheit to Celsius...", request.getFahrenheitReading());
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

