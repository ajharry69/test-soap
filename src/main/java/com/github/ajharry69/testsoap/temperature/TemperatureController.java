package com.github.ajharry69.testsoap.temperature;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
