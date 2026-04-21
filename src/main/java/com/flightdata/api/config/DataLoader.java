package com.flightdata.api.config;

import com.flightdata.api.model.Airport;
import com.flightdata.api.model.Route;
import com.flightdata.api.repository.AirportRepository;
import com.flightdata.api.repository.RouteRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;

    @Value("${app.data.airports-file}")
    private String airportsFile;

    @Value("${app.data.routes-file}")
    private String routesFile;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (airportRepository.count() > 0) {
            log.info("Data already loaded ({} airports, {} routes). Skipping DataLoader.",
                    airportRepository.count(), routeRepository.count());
            return;
        }

        log.info("Starting data load...");
        Set<String> myIataCodes = loadAirports();
        loadRoutes(myIataCodes);
        log.info("Data load complete. Airports: {}, Routes: {}",
                airportRepository.count(), routeRepository.count());
    }

    private Set<String> loadAirports() {
        Set<String> iataSet = new HashSet<>();
        List<Airport> airports = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(airportsFile), StandardCharsets.UTF_8))) {

            reader.readNext(); // skip header row

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length < 9 || !"MY".equals(trim(row[8]))) {
                    continue;
                }

                Airport airport = Airport.builder()
                        .sourceId(parseLong(row[0]))
                        .ident(trim(row[1]))
                        .type(trim(row[2]))
                        .name(trim(row[3]))
                        .latitude(parseDouble(row[4]))
                        .longitude(parseDouble(row[5]))
                        .elevationFt(parseInt(row[6]))
                        .continent(trim(row[7]))
                        .isoCountry(trim(row[8]))
                        .isoRegion(row.length > 9 ? trim(row[9]) : null)
                        .municipality(row.length > 10 ? trim(row[10]) : null)
                        .scheduledService(row.length > 11 ? trim(row[11]) : null)
                        .icaoCode(row.length > 12 ? trim(row[12]) : null)
                        .iataCode(row.length > 13 ? trim(row[13]) : null)
                        .gpsCode(row.length > 14 ? trim(row[14]) : null)
                        .localCode(row.length > 15 ? trim(row[15]) : null)
                        .homeLink(row.length > 16 ? trim(row[16]) : null)
                        .wikipediaLink(row.length > 17 ? trim(row[17]) : null)
                        .keywords(row.length > 18 ? trim(row[18]) : null)
                        .build();

                airports.add(airport);

                String iata = airport.getIataCode();
                if (iata != null && !iata.isBlank()) {
                    iataSet.add(iata);
                }
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Failed to read airports file: {}", airportsFile, e);
            throw new RuntimeException("Failed to load airports", e);
        }

        airportRepository.saveAll(airports);
        log.info("Loaded {} Malaysian airports ({} with IATA codes)", airports.size(), iataSet.size());
        return iataSet;
    }

    private void loadRoutes(Set<String> myIataCodes) {
        List<Route> routes = new ArrayList<>();
        int skipped = 0;
        int lineNum = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(routesFile), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                String[] parts = line.split(",", -1);

                if (parts.length < 9) {
                    skipped++;
                    continue;
                }

                String srcAirport = trim(parts[2]);
                String dstAirport = trim(parts[4]);

                if (srcAirport == null || dstAirport == null) {
                    skipped++;
                    continue;
                }

                if (!myIataCodes.contains(srcAirport) && !myIataCodes.contains(dstAirport)) {
                    continue;
                }

                Route route = Route.builder()
                        .airline(trim(parts[0]))
                        .airlineId(nullIfBackslashN(parts[1]))
                        .sourceAirport(srcAirport)
                        .sourceAirportId(nullIfBackslashN(parts[3]))
                        .destAirport(dstAirport)
                        .destAirportId(nullIfBackslashN(parts[5]))
                        .codeshare(trim(parts[6]))
                        .stops(parseInt(parts[7]))
                        .equipment(trim(parts[8]))
                        .build();

                routes.add(route);
            }
        } catch (IOException e) {
            log.error("Failed to read routes file: {}", routesFile, e);
            throw new RuntimeException("Failed to load routes", e);
        }

        routeRepository.saveAll(routes);
        log.info("Loaded {} MY-related routes from {} lines (skipped {} malformed)",
                routes.size(), lineNum, skipped);
    }

    private String trim(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private String nullIfBackslashN(String value) {
        if (value == null) return null;
        String t = value.trim();
        return "\\N".equals(t) || t.isEmpty() ? null : t;
    }

    private Long parseLong(String value) {
        try { return Long.parseLong(value.trim()); }
        catch (Exception e) { return null; }
    }

    private Double parseDouble(String value) {
        try { return Double.parseDouble(value.trim()); }
        catch (Exception e) { return null; }
    }

    private Integer parseInt(String value) {
        try { return Integer.parseInt(value.trim()); }
        catch (Exception e) { return null; }
    }
}
