package com.optiflow.platform.shared.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiRequestErrorsTest {

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @ValueSource(strings = {"/appointments/abc", "/patients/abc/appointments", "/optical-stores/abc/availability"})
  void explainsInvalidUuidParameters(String url) throws Exception {
    mockMvc.perform(get(url))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("El parámetro id debe ser un UUID válido."));
  }

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {"{", "{\"patientId\":\"abc\"}", "{\"patientId\":[]}"})
  void explainsMissingMalformedAndWrongTypeBodies(String body) throws Exception {
    mockMvc.perform(post("/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value(
            "El cuerpo de la solicitud debe contener un JSON válido con los datos en el formato esperado."));
  }

  @Test
  void keepsFieldValidationForReadableJson() throws Exception {
    mockMvc.perform(post("/appointments").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("patientId")));
  }
}
