package com.flightdata.api.service;

import com.flightdata.api.dto.AirportDTO;
import com.flightdata.api.model.Airport;
import com.flightdata.api.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;

    public Page<AirportDTO> getAllAirports(Pageable pageable) {
        return airportRepository.findAll(pageable).map(this::toDTO);
    }

    public AirportDTO getByIata(String iata) {
        return airportRepository.findByIataCode(iata.toUpperCase())
                .map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Airport not found with IATA code: " + iata.toUpperCase()));
    }

    private AirportDTO toDTO(Airport airport) {
        return AirportDTO.builder()
                .iataCode(airport.getIataCode())
                .icaoCode(airport.getIcaoCode())
                .name(airport.getName())
                .type(airport.getType())
                .latitude(airport.getLatitude())
                .longitude(airport.getLongitude())
                .elevationFt(airport.getElevationFt())
                .municipality(airport.getMunicipality())
                .isoRegion(airport.getIsoRegion())
                .scheduledService(airport.getScheduledService())
                .wikipediaLink(airport.getWikipediaLink())
                .build();
    }
}
