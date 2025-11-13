package com.github.ajharry69.testsoap;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponents5MessageSender;

import java.time.Duration;

@Configuration
class SoapClientConfig {
    @Bean
    public Jaxb2Marshaller jaxbMarshaller() {
        var marshaller = new Jaxb2Marshaller();
        // Scan ALL packages that contain JAXB models
        marshaller.setPackagesToScan(
                "com.github.ajharry69.testsoap.countries",
                "com.github.ajharry69.testsoap.temperature"
        );
        return marshaller;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() throws SOAPException {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setMessageFactory(MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL));
        messageFactory.afterPropertiesSet();
        return messageFactory;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(SaajSoapMessageFactory messageFactory, Jaxb2Marshaller marshaller, HttpComponents5MessageSender messageSender) {
        var template = new WebServiceTemplate(messageFactory);
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);

        // Use the high-performance, pooled HTTP client
        template.setMessageSender(messageSender);
        return template;
    }

    @Bean
    public HttpComponents5MessageSender httpComponentsMessageSender() {
        var sender = new HttpComponents5MessageSender();
        sender.setConnectionTimeout(Duration.ofSeconds(5));
        sender.setReadTimeout(Duration.ofSeconds(10));
        sender.setMaxTotalConnections(200);
        /*sender.setMaxConnectionsPerHost(
                Map.of("www.w3schools.com", "50")
        );*/
        sender.setAcceptGzipEncoding(false);
        return sender;
    }
}