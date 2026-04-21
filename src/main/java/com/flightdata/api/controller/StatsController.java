package com.flightdata.api.controller;

import com.flightdata.api.dto.StatsDTO;
import com.flightdata.api.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final RouteService routeService;

    @GetMapping("/busiest-airports")
    public ResponseEntity<List<StatsDTO.BusiestAirport>> getBusiestAirports() {
        return ResponseEntity.ok(routeService.getBusiestAirports());
    }

    @GetMapping("/summary")
    public ResponseEntity<StatsDTO> getSummary() {
        return ResponseEntity.ok(routeService.getSummary());
    }
}
