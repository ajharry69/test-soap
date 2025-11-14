package com.github.ajharry69.testsoap.temperature;

public interface TemperatureSoapClient {
    TemperatureResponse getTemperature(TemperatureRequest request);
}
