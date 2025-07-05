package com.example.hotelapi.controller;

import com.example.hotelapi.dto.*;
import com.example.hotelapi.entity.Hotel;
import com.example.hotelapi.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Hotels", description = "Операции для управления отелями")
@RestController
@RequestMapping(value = "/property-view", produces = MediaType.APPLICATION_JSON_VALUE)
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Operation(summary = "Список всех отелей", description = "Возвращает краткую информацию по всем отелям")
    @ApiResponse(responseCode = "200", description = "ОК")
    @GetMapping("/hotels")
    public List<Map<String, Object>> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Hotel h : hotels) {
            Map<String, Object> hotelMap = new LinkedHashMap<>();
            hotelMap.put("id", h.getId());
            hotelMap.put("name", h.getName());
            hotelMap.put("description", h.getDescription());
            hotelMap.put("address",
                    h.getHouseNumber() + " " + h.getStreet() +
                            ", " + h.getCity() +
                            ", " + h.getPostCode() +
                            ", " + h.getCountry()
            );
            hotelMap.put("phone", h.getPhone());
            result.add(hotelMap);
        }
        return result;
    }

    @Operation(summary = "Детали отеля по ID", description = "Возвращает полную информацию по отелю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найден"),
            @ApiResponse(responseCode = "404", description = "Не найден")
    })
    @GetMapping("/hotels/{id}")
    public ResponseEntity<HotelDetailsDto> getHotelById(
            @Parameter(description = "ID отеля", example = "1") @PathVariable Long id
    ) {
        return hotelService.getHotelById(id)
                .map(h -> {
                    HotelDetailsDto dto = new HotelDetailsDto();
                    dto.id = h.getId();
                    dto.name = h.getName();
                    dto.description = h.getDescription();
                    dto.brand = h.getBrand();

                    AddressDto addr = new AddressDto();
                    addr.houseNumber = h.getHouseNumber();
                    addr.street = h.getStreet();
                    addr.city = h.getCity();
                    addr.country = h.getCountry();
                    addr.postCode = h.getPostCode();
                    dto.address = addr;

                    ContactDto contacts = new ContactDto();
                    contacts.phone = h.getPhone();
                    contacts.email = h.getEmail();
                    dto.contacts = contacts;

                    ArrivalTimeDto arrival = new ArrivalTimeDto();
                    arrival.checkIn = h.getCheckIn();
                    arrival.checkOut = h.getCheckOut();
                    dto.arrivalTime = arrival;

                    dto.amenities = h.getAmenities();
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Поиск отелей", description = "Поиск по параметрам: name, brand, city, country, amenities")
    @ApiResponse(responseCode = "200", description = "ОК")
    @GetMapping("/search")
    public List<Map<String, Object>> searchHotels(
            @Parameter(description = "Имя (частичное)")    @RequestParam(required = false) String name,
            @Parameter(description = "Бренд (точно)")      @RequestParam(required = false) String brand,
            @Parameter(description = "Город (точно)")      @RequestParam(required = false) String city,
            @Parameter(description = "Страна (точно)")     @RequestParam(required = false) String country,
            @Parameter(description = "Удобство (точно)")   @RequestParam(required = false) String amenities
    ) {
        List<Hotel> found = hotelService.search(name, brand, city, country, amenities);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Hotel h : found) {
            Map<String, Object> hotelMap = new LinkedHashMap<>();
            hotelMap.put("id", h.getId());
            hotelMap.put("name", h.getName());
            hotelMap.put("description", h.getDescription());
            hotelMap.put("address",
                    h.getHouseNumber() + " " + h.getStreet() +
                            ", " + h.getCity() +
                            ", " + h.getPostCode() +
                            ", " + h.getCountry()
            );
            hotelMap.put("phone", h.getPhone());
            result.add(hotelMap);
        }
        return result;
    }

    @Operation(summary = "Создать новый отель", description = "Создаёт и возвращает краткую информацию о новом отеле")
    @ApiResponse(responseCode = "200", description = "Создан")
    @PostMapping("/hotels")
    public ResponseEntity<Map<String, Object>> createHotel(
            @Parameter(description = "Данные отеля") @RequestBody HotelCreateDto dto
    ) {
        Hotel h = new Hotel();
        h.setName(dto.name);
        h.setDescription(dto.description);
        h.setBrand(dto.brand);
        h.setHouseNumber(dto.address.houseNumber);
        h.setStreet(dto.address.street);
        h.setCity(dto.address.city);
        h.setCountry(dto.address.country);
        h.setPostCode(dto.address.postCode);
        h.setPhone(dto.contacts.phone);
        h.setEmail(dto.contacts.email);
        h.setCheckIn(dto.arrivalTime.checkIn);
        h.setCheckOut(dto.arrivalTime.checkOut);

        Hotel saved = hotelService.createHotel(h);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", saved.getId());
        resp.put("name", saved.getName());
        resp.put("description", saved.getDescription());
        resp.put("address",
                saved.getHouseNumber() + " " + saved.getStreet() +
                        ", " + saved.getCity() +
                        ", " + saved.getPostCode() +
                        ", " + saved.getCountry()
        );
        resp.put("phone", saved.getPhone());
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Добавить amenities к отелю", description = "Прикрепляет новый список удобств к отелю")
    @ApiResponse(responseCode = "200", description = "Удобства добавлены")
    @PostMapping("/hotels/{id}/amenities")
    public ResponseEntity<String> addAmenities(
            @Parameter(description = "ID отеля")              @PathVariable Long id,
            @Parameter(description = "Список удобств")         @RequestBody List<String> amenities
    ) {
        hotelService.addAmenities(id, amenities);
        return ResponseEntity.ok("Amenities added successfully.");
    }

    @Operation(summary = "Гистограмма по параметру", description = "Группировка: brand, city, country, amenities")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ОК"),
            @ApiResponse(responseCode = "400", description = "Неподдерживаемый параметр")
    })
    @GetMapping("/histogram/{param}")
    public ResponseEntity<Map<String, Long>> getHistogram(
            @Parameter(description = "Параметр группировки", example = "city") @PathVariable String param
    ) {
        try {
            return ResponseEntity.ok(hotelService.getHistogram(param));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}