package com.github.ajharry69.testsoap.temperature;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

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
    private MockMvc mvc;

    @MockitoBean
    private TemperatureService service;

    @Test
    void get_returns_200_and_list_payload() throws Exception {
        var response = new TemperatureResponse();
        response.setDegreesCelsius("98.98");
        when(service.get(any()))
                .thenReturn(List.of(response));

        var headers = new HttpHeaders(
                MultiValueMap.fromSingleValue(
                        Map.of(
                                "X-FeatureName", "101",
                                "X-ServiceCode", "101",
                                "X-ServiceName", "101",
                                "X-MinorServiceVersion", "101",
                                "X-ChannelCategory", "101",
                                "X-ChannelCode", "101",
                                "X-ChannelName", "101"
                        )
                )
        );
        mvc.perform(get("/temperature?fahrenheitReading=100").headers(headers))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].degreesCelsius", is("98.98"))
                );
    }
}
