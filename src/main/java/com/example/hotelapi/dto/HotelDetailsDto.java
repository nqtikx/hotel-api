package com.example.hotelapi.dto;

import java.util.List;

public class HotelDetailsDto {
    public Long id;
    public String name;
    public String description;
    public String brand;
    public AddressDto address;
    public ContactDto contacts;
    public ArrivalTimeDto arrivalTime;
    public List<String> amenities;
}
