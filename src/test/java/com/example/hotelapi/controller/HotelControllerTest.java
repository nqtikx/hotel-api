package com.example.hotelapi.controller;

import com.example.hotelapi.dto.*;
import com.example.hotelapi.entity.Hotel;
import com.example.hotelapi.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HotelControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HotelService hotelService;

    @InjectMocks
    private HotelController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getAllHotels_shouldReturnShortList() throws Exception {
        Hotel h = new Hotel();
        h.setId(1L);
        h.setName("Test Hotel");
        h.setDescription("desc");
        h.setHouseNumber(9);
        h.setStreet("Main St");
        h.setCity("TestCity");
        h.setPostCode("12345");
        h.setCountry("TC");
        h.setPhone("+1");
        when(hotelService.getAllHotels()).thenReturn(List.of(h));

        mockMvc.perform(get("/property-view/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Hotel"))
                .andExpect(jsonPath("$[0].address")
                        .value("9 Main St, TestCity, 12345, TC"))
                .andExpect(jsonPath("$[0].phone").value("+1"));
    }

    @Test
    void getHotelById_shouldReturnDetails() throws Exception {
        Hotel h = new Hotel();
        h.setId(1L);
        h.setName("T");
        h.setDescription("D");
        h.setBrand("B");
        h.setHouseNumber(1);
        h.setStreet("S");
        h.setCity("C");
        h.setCountry("Co");
        h.setPostCode("PC");
        h.setPhone("P");
        h.setEmail("e@e");
        h.setCheckIn("14:00");
        h.setCheckOut("12:00");
        h.setAmenities(List.of("WiFi", "Pool"));
        when(hotelService.getHotelById(1L))
                .thenReturn(Optional.of(h));

        mockMvc.perform(get("/property-view/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("B"))
                .andExpect(jsonPath("$.address.houseNumber").value(1))
                .andExpect(jsonPath("$.contacts.email").value("e@e"))
                .andExpect(jsonPath("$.amenities[1]").value("Pool"));
    }

    @Test
    void getHotelById_notFound_shouldReturn404() throws Exception {
        when(hotelService.getHotelById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/property-view/hotels/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchHotels_byCity_shouldReturnList() throws Exception {
        Hotel h = new Hotel();
        h.setId(2L);
        h.setName("CityHotel");
        h.setDescription("desc2");
        h.setHouseNumber(5);
        h.setStreet("City St");
        h.setCity("Minsk");
        h.setPostCode("220004");
        h.setCountry("Belarus");
        h.setPhone("+2");

        when(hotelService.search(null, null, "Minsk", null, null))
                .thenReturn(List.of(h));

        mockMvc.perform(get("/property-view/search")
                        .param("city", "Minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address")
                        .value("5 City St, Minsk, 220004, Belarus"));
    }

    @Test
    void createHotel_shouldReturnCreatedHotel() throws Exception {
        Hotel h = new Hotel();
        h.setId(3L);
        h.setName("New Hotel");
        h.setDescription("desc3");
        h.setHouseNumber(10);
        h.setStreet("New St");
        h.setCity("City");
        h.setPostCode("00000");
        h.setCountry("Country");
        h.setPhone("123");
        h.setEmail("e@e");

        when(hotelService.createHotel(any(Hotel.class))).thenReturn(h);

        String requestJson = """
        {
          "name": "New Hotel",
          "description": "desc3",
          "brand": "BrandX",
          "address": {
            "houseNumber": 10,
            "street": "New St",
            "city": "City",
            "country": "Country",
            "postCode": "00000"
          },
          "contacts": {
            "phone": "123",
            "email": "e@e"
          },
          "arrivalTime": {
            "checkIn": "12:00",
            "checkOut": "11:00"
          }
        }
        """;

        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Hotel"))
                .andExpect(jsonPath("$.address")
                        .value("10 New St, City, 00000, Country"));
    }

    @Test
    void addAmenities_shouldReturnSuccessMessage() throws Exception {
        String amenitiesJson = """
        ["Pool","Sauna","Gym"]
        """;

        mockMvc.perform(post("/property-view/hotels/1/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amenitiesJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Amenities added successfully."));

        verify(hotelService).addAmenities(
                eq(1L),
                argThat(list -> list.containsAll(List.of("Pool","Sauna","Gym")))
        );
    }

    @Test
    void getHistogram_validParam_shouldReturnMap() throws Exception {
        Map<String, Long> data = Map.of("Minsk", 2L);
        when(hotelService.getHistogram("city")).thenReturn(data);

        mockMvc.perform(get("/property-view/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk").value(2));
    }

    @Test
    void getHistogram_invalidParam_shouldReturnBadRequest() throws Exception {
        when(hotelService.getHistogram("unknown"))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/property-view/histogram/unknown"))
                .andExpect(status().isBadRequest());
    }

}
