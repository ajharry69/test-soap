package com.github.ajharry69.testsoap.countries;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

        when(service.get()).thenReturn(response);

        mvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countries[0].isoCode").value("AX"))
                .andExpect(jsonPath("$.countries[1].name").value("Afghanistan"));
    }
}
