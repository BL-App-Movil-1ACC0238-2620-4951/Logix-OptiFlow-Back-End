package com.optiflow.platform.searchbooking.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class SearchBookingSteps {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TimeSlotRepository timeSlotRepository;

  private String storeId;
  private String patientId;
  private String timeSlotId;
  private String missingStoreName;
  private MvcResult response;

  @Dado("que existen ópticas y sucursales registradas")
  public void registeredStoresExist() throws Exception {
    storeId = firstStoreId();
  }

  @Cuando("el paciente consulta los establecimientos disponibles")
  public void listsStores() throws Exception {
    response = mockMvc.perform(get("/optical-stores")).andReturn();
  }

  @Entonces("obtiene la información y las direcciones de los establecimientos")
  public void checksStoreDetails() throws Exception {
    assertEquals(200, response.getResponse().getStatus());
    JsonNode stores = responseJson().get("opticalStores");
    assertTrue(stores.isArray() && !stores.isEmpty());
    for (JsonNode store : stores) {
      UUID.fromString(store.get("id").asText());
      assertFalse(store.get("name").asText().isBlank());
      assertFalse(store.get("address").asText().isBlank());
    }
  }

  @Entonces("puede consultar los horarios disponibles de una óptica")
  public void checksAvailability() throws Exception {
    MvcResult availability = mockMvc.perform(get("/optical-stores/" + storeId + "/availability"))
        .andExpect(status().isOk()).andReturn();
    JsonNode slots = json(availability).get("timeSlots");
    assertTrue(slots.isArray() && !slots.isEmpty());
    for (JsonNode slot : slots) {
      assertEquals(storeId, slot.get("opticalStoreId").asText());
      assertEquals("AVAILABLE", slot.get("status").asText());
      assertTrue(Instant.parse(slot.get("endDateTime").asText())
          .isAfter(Instant.parse(slot.get("startDateTime").asText())));
    }
  }

  @Dado("que ninguna óptica coincide con los criterios de búsqueda")
  public void unmatchedCriteria() {
    missingStoreName = "bdd-no-store-" + UUID.randomUUID();
  }

  @Cuando("el paciente realiza la búsqueda")
  public void searchesStores() throws Exception {
    response = mockMvc.perform(get("/optical-stores/search")
        .param("name", missingStoreName)).andReturn();
  }

  @Entonces("obtiene una lista vacía y un mensaje informativo")
  public void checksEmptySearch() throws Exception {
    assertEquals(200, response.getResponse().getStatus());
    assertEquals(0, responseJson().get("opticalStores").size());
    assertEquals("No optical stores match the search criteria.",
        responseJson().get("message").asText());
  }

  @Dado("que existe un paciente registrado")
  public void registersPatient() throws Exception {
    patientId = createPatient();
  }

  @Dado("existe un horario disponible en una óptica")
  public void createsAvailableSlot() throws Exception {
    storeId = firstStoreId();
    TimeSlotId slotId = TimeSlotId.generate();
    Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
    TimeSlot slot = TimeSlot.publish(slotId, OpticalStoreId.of(UUID.fromString(storeId)),
        start, start.plus(30, ChronoUnit.MINUTES));
    timeSlotRepository.save(slot);
    timeSlotId = slot.id().value().toString();
  }

  @Dado("existe un horario reservado por otro paciente")
  public void reservesSlotForAnotherPatient() throws Exception {
    createsAvailableSlot();
    mockMvc.perform(post("/appointments").contentType(MediaType.APPLICATION_JSON)
        .content(bookingJson(createPatient()))).andExpect(status().isCreated());
  }

  @Cuando("el paciente confirma la reserva de ese horario")
  public void booksSlot() throws Exception {
    response = mockMvc.perform(post("/appointments").contentType(MediaType.APPLICATION_JSON)
        .content(bookingJson(patientId))).andReturn();
  }

  @Entonces("el servicio responde con código {int}")
  public void checksStatus(int expected) {
    assertEquals(expected, response.getResponse().getStatus());
  }

  @Entonces("la cita queda en estado {string}")
  public void checksAppointmentStatus(String expected) throws Exception {
    assertEquals(expected, responseJson().get("status").asText());
    assertEquals(patientId, responseJson().get("patientId").asText());
    assertEquals(timeSlotId, responseJson().get("timeSlotId").asText());
  }

  @Entonces("la cita registrada puede consultarse")
  public void checksPersistedAppointment() throws Exception {
    String appointmentId = responseJson().get("id").asText();
    MvcResult stored = mockMvc.perform(get("/appointments/" + appointmentId))
        .andExpect(status().isOk()).andReturn();
    assertEquals(appointmentId, json(stored).get("id").asText());
    assertEquals(patientId, json(stored).get("patientId").asText());
    assertEquals("CONFIRMED", json(stored).get("status").asText());
  }

  @Entonces("el horario deja de estar disponible")
  public void checksSlotIsNoLongerAvailable() throws Exception {
    MvcResult availability = mockMvc.perform(get("/optical-stores/" + storeId + "/availability"))
        .andExpect(status().isOk()).andReturn();
    for (JsonNode slot : json(availability).get("timeSlots")) {
      assertFalse(timeSlotId.equals(slot.get("id").asText()));
    }
  }

  @Entonces("informa que el horario ya no está disponible")
  public void checksConflictMessage() throws Exception {
    assertEquals("The selected time slot is no longer available.",
        responseJson().get("message").asText());
  }

  private String createPatient() throws Exception {
    String request = objectMapper.writeValueAsString(Map.of(
        "name", "BDD Patient", "email", "bdd-" + UUID.randomUUID() + "@optiflow.test",
        "phone", "999888777", "password", "testpass12"));
    MvcResult patient = mockMvc.perform(post("/patients")
        .contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isCreated()).andReturn();
    return json(patient).get("id").asText();
  }

  private String firstStoreId() throws Exception {
    MvcResult stores = mockMvc.perform(get("/optical-stores"))
        .andExpect(status().isOk()).andReturn();
    JsonNode list = json(stores).get("opticalStores");
    assertTrue(list.isArray() && !list.isEmpty());
    return list.get(0).get("id").asText();
  }

  private String bookingJson(String patient) throws Exception {
    return objectMapper.writeValueAsString(Map.of("patientId", patient,
        "opticalStoreId", storeId, "timeSlotId", timeSlotId));
  }

  private JsonNode responseJson() throws Exception {
    return json(response);
  }

  private JsonNode json(MvcResult result) throws Exception {
    return objectMapper.readTree(result.getResponse().getContentAsString());
  }
}
