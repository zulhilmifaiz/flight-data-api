package com.flightdata.api.repository;

import com.flightdata.api.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findBySourceAirport(String sourceAirport);

    List<Route> findByDestAirport(String destAirport);

    List<Route> findBySourceAirportAndDestAirport(String sourceAirport, String destAirport);

    @Query(value = """
            SELECT a.iata_code, COUNT(r.id) AS route_count
            FROM airports a
            LEFT JOIN routes r ON (r.source_airport = a.iata_code OR r.dest_airport = a.iata_code)
            WHERE a.iata_code IS NOT NULL AND a.iata_code != ''
            GROUP BY a.iata_code
            ORDER BY route_count DESC
            """, nativeQuery = true)
    List<Object[]> findBusiestAirports();
}
