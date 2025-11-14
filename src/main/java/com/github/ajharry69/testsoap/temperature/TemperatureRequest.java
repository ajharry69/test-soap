package com.github.ajharry69.testsoap.temperature;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FahrenheitToCelsius", namespace = "https://www.w3schools.com/xml/")
public class TemperatureRequest {
    @XmlElement(name = "Fahrenheit", namespace = "https://www.w3schools.com/xml/")
    private String fahrenheitReading;
}