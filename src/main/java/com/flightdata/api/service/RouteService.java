package com.flightdata.api.service;

import com.flightdata.api.dto.RouteDTO;
import com.flightdata.api.dto.StatsDTO;
import com.flightdata.api.model.Route;
import com.flightdata.api.repository.AirportRepository;
import com.flightdata.api.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final AirportRepository airportRepository;

    public List<RouteDTO> getRoutes(String origin, String destination) {
        boolean hasOrigin = origin != null && !origin.isBlank();
        boolean hasDest = destination != null && !destination.isBlank();

        List<Route> routes;
        if (hasOrigin && hasDest) {
            routes = routeRepository.findBySourceAirportAndDestAirport(
                    origin.toUpperCase(), destination.toUpperCase());
        } else if (hasOrigin) {
            routes = routeRepository.findBySourceAirport(origin.toUpperCase());
        } else if (hasDest) {
            routes = routeRepository.findByDestAirport(destination.toUpperCase());
        } else {
            routes = routeRepository.findAll();
        }

        return routes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<StatsDTO.BusiestAirport> getBusiestAirports() {
        return routeRepository.findBusiestAirports().stream()
                .map(row -> StatsDTO.BusiestAirport.builder()
                        .iataCode((String) row[0])
                        .routeCount(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    public StatsDTO getSummary() {
        return StatsDTO.builder()
                .totalAirports(airportRepository.count())
                .totalRoutes(routeRepository.count())
                .statesCovered(airportRepository.countDistinctRegions())
                .build();
    }

    private RouteDTO toDTO(Route route) {
        return RouteDTO.builder()
                .airline(route.getAirline())
                .sourceAirport(route.getSourceAirport())
                .destAirport(route.getDestAirport())
                .codeshare(route.getCodeshare())
                .stops(route.getStops())
                .equipment(route.getEquipment())
                .build();
    }
}
