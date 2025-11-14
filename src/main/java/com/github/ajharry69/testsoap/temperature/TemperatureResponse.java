package com.github.ajharry69.testsoap.temperature;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FahrenheitToCelsiusResponse", namespace = "https://www.w3schools.com/xml/")
public class TemperatureResponse {
    @XmlElement(name = "FahrenheitToCelsiusResult", namespace = "https://www.w3schools.com/xml/")
    private String degreesCelsius;
}