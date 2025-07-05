package com.example.hotelapi.repository;

import com.example.hotelapi.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCityIgnoreCase(String city);
    List<Hotel> findByNameContainingIgnoreCase(String name);
    List<Hotel> findByBrandIgnoreCase(String brand);
    List<Hotel> findByCountryIgnoreCase(String country);
    List<Hotel> findByAmenitiesContainingIgnoreCase(String amenity);
}
