package com.github.ajharry69.testsoap.temperature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TemperatureController.class)
class TemperatureControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TemperatureService service;

    @Test
    void get_returns_200_and_list_payload() throws Exception {
        TemperatureResponse r = new TemperatureResponse();
        r.setDegreesCelsius("98.98");
        when(service.get(any()))
                .thenReturn(List.of(r));

        mvc.perform(get("/temperature?fahrenheitReading=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].degreesCelsius", is("98.98")));
    }
}
