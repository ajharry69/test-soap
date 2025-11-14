package com.github.ajharry69.testsoap.temperature;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
class TemperatureService {
    private final TemperatureSoapClient client;

    public List<TemperatureResponse> get(TemperatureRequest request) {
        return List.of(client.getTemperature(request));
    }
}
