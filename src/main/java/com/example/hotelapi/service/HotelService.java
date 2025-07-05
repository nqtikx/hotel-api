package com.example.hotelapi.service;

import com.example.hotelapi.entity.Hotel;
import com.example.hotelapi.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public void addAmenities(Long id, List<String> amenities) {
        hotelRepository.findById(id).ifPresent(hotel -> {
            hotel.getAmenities().addAll(amenities);
            hotelRepository.save(hotel);
        });
    }

    public List<Hotel> search(String name, String brand, String city, String country, String amenity) {
        if (name != null) return hotelRepository.findByNameContainingIgnoreCase(name);
        if (brand != null) return hotelRepository.findByBrandIgnoreCase(brand);
        if (city != null) return hotelRepository.findByCityIgnoreCase(city);
        if (country != null) return hotelRepository.findByCountryIgnoreCase(country);
        if (amenity != null) return hotelRepository.findByAmenitiesContainingIgnoreCase(amenity);
        return hotelRepository.findAll();
    }

    public Map<String, Long> getHistogram(String param) {
        List<Hotel> hotels = hotelRepository.findAll();
        Map<String, Long> histogram = new HashMap<>();

        switch (param.toLowerCase()) {
            case "brand":
                for (Hotel h : hotels) {
                    String key = h.getBrand() != null ? h.getBrand() : "Unknown";
                    histogram.put(key, histogram.getOrDefault(key, 0L) + 1);
                }
                break;

            case "city":
                for (Hotel h : hotels) {
                    String key = h.getCity() != null ? h.getCity() : "Unknown";
                    histogram.put(key, histogram.getOrDefault(key, 0L) + 1);
                }
                break;

            case "country":
                for (Hotel h : hotels) {
                    String key = h.getCountry() != null ? h.getCountry() : "Unknown";
                    histogram.put(key, histogram.getOrDefault(key, 0L) + 1);
                }
                break;

            case "amenities":
                for (Hotel h : hotels) {
                    for (String amenity : h.getAmenities()) {
                        histogram.put(amenity, histogram.getOrDefault(amenity, 0L) + 1);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported histogram param: " + param);
        }

        return histogram;
    }

}
