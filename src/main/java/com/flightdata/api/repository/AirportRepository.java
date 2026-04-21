package com.flightdata.api.repository;

import com.flightdata.api.model.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    Page<Airport> findAll(Pageable pageable);

    Optional<Airport> findByIataCode(String iataCode);

    @Query("SELECT a.iataCode FROM Airport a WHERE a.iataCode IS NOT NULL AND a.iataCode <> ''")
    List<String> findAllIataCodes();

    @Query("SELECT COUNT(DISTINCT a.isoRegion) FROM Airport a")
    long countDistinctRegions();
}
