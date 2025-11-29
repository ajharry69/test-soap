package com.github.ajharry69.testsoap.countries;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CountriesController.class)
class CountriesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CountryService service;

    @Test
    void get_returns_200_and_list_payload() throws Exception {
        var response = new CountriesResponse();
        var a = new CountryResponse();
        a.setIsoCode("AX");
        a.setName("Åland Islands");
        var b = new CountryResponse();
        b.setIsoCode("AF");
        b.setName("Afghanistan");
        response.setCountries(List.of(a, b));

        when(service.get())
                .thenReturn(response);

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
        mvc.perform(get("/countries").headers(headers))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.countries[0].isoCode").value("AX"),
                        jsonPath("$.countries[1].name").value("Afghanistan")
                );
    }
}
