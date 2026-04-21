package com.flightdata.api.controller;

import com.flightdata.api.dto.AirportDTO;
import com.flightdata.api.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping
    public ResponseEntity<Page<AirportDTO>> getAllAirports(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(airportService.getAllAirports(pageable));
    }

    @GetMapping("/{iata}")
    public ResponseEntity<AirportDTO> getAirport(@PathVariable String iata) {
        return ResponseEntity.ok(airportService.getByIata(iata));
    }
}
