package com.example.hotelapi.service;

import com.example.hotelapi.entity.Hotel;
import com.example.hotelapi.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    private HotelRepository repo;
    private HotelService service;

    @BeforeEach
    void setUp() {
        repo = mock(HotelRepository.class);
        service = new HotelService(repo);
    }

    @Test
    void getAllHotels_returnsAll() {
        var h = new Hotel();
        h.setName("H1");
        when(repo.findAll()).thenReturn(List.of(h));

        var out = service.getAllHotels();
        assertEquals(1, out.size());
        assertEquals("H1", out.get(0).getName());
    }

    @Test
    void search_byName() {
        var h = new Hotel(); h.setName("Foo");
        when(repo.findByNameContainingIgnoreCase("oo")).thenReturn(List.of(h));

        var out = service.search("oo", null, null, null, null);
        assertEquals(1, out.size());
        assertEquals("Foo", out.get(0).getName());
    }

    @Test
    void search_byBrand() {
        var h = new Hotel(); h.setBrand("Hilton");
        when(repo.findByBrandIgnoreCase("Hilton")).thenReturn(List.of(h));

        var out = service.search(null, "Hilton", null, null, null);
        assertEquals(1, out.size());
        assertEquals("Hilton", out.get(0).getBrand());
    }

    @Test
    void search_byCity() {
        var h = new Hotel(); h.setCity("Minsk");
        when(repo.findByCityIgnoreCase("Minsk")).thenReturn(List.of(h));

        var out = service.search(null, null, "Minsk", null, null);
        assertEquals(1, out.size());
        assertEquals("Minsk", out.get(0).getCity());
    }

    @Test
    void search_byCountry() {
        var h = new Hotel(); h.setCountry("Belarus");
        when(repo.findByCountryIgnoreCase("Belarus")).thenReturn(List.of(h));

        var out = service.search(null, null, null, "Belarus", null);
        assertEquals(1, out.size());
        assertEquals("Belarus", out.get(0).getCountry());
    }

    @Test
    void search_byAmenity() {
        var h = new Hotel(); h.setAmenities(List.of("WiFi"));
        when(repo.findByAmenitiesContainingIgnoreCase("WiFi")).thenReturn(List.of(h));

        var out = service.search(null, null, null, null, "WiFi");
        assertEquals(1, out.size());
        assertTrue(out.get(0).getAmenities().contains("WiFi"));
    }

    @Test
    void histogram_brand() {
        var h1 = new Hotel(); h1.setBrand("A");
        var h2 = new Hotel(); h2.setBrand("B");
        var h3 = new Hotel(); h3.setBrand("A");
        when(repo.findAll()).thenReturn(List.of(h1, h2, h3));

        var hist = service.getHistogram("brand");
        assertEquals(2, hist.get("A"));
        assertEquals(1, hist.get("B"));
    }

    @Test
    void histogram_city() {
        var h1 = new Hotel(); h1.setCity("X");
        var h2 = new Hotel(); h2.setCity("X");
        when(repo.findAll()).thenReturn(List.of(h1, h2));

        var hist = service.getHistogram("city");
        assertEquals(2, hist.get("X"));
    }

    @Test
    void histogram_country() {
        var h = new Hotel(); h.setCountry("C");
        when(repo.findAll()).thenReturn(List.of(h));

        var hist = service.getHistogram("country");
        assertEquals(1, hist.get("C"));
    }

    @Test
    void histogram_amenities() {
        var h1 = new Hotel(); h1.setAmenities(List.of("P1", "P2"));
        var h2 = new Hotel(); h2.setAmenities(List.of("P1"));
        when(repo.findAll()).thenReturn(List.of(h1, h2));

        var hist = service.getHistogram("amenities");
        assertEquals(2, hist.get("P1"));
        assertEquals(1, hist.get("P2"));
    }

    @Test
    void histogram_invalidParam_throws() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class,
                () -> service.getHistogram("foo"));
    }
}
