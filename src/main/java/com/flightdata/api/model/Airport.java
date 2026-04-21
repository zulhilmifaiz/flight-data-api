package com.flightdata.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airports", indexes = {
        @Index(name = "idx_iata_code", columnList = "iata_code"),
        @Index(name = "idx_iso_region", columnList = "iso_region")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "ident", length = 20)
    private String ident;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "elevation_ft")
    private Integer elevationFt;

    @Column(name = "continent", length = 10)
    private String continent;

    @Column(name = "iso_country", length = 10, nullable = false)
    private String isoCountry;

    @Column(name = "iso_region", length = 10, nullable = false)
    private String isoRegion;

    @Column(name = "municipality", length = 100)
    private String municipality;

    @Column(name = "scheduled_service", length = 5)
    private String scheduledService;

    @Column(name = "icao_code", length = 10)
    private String icaoCode;

    @Column(name = "iata_code", length = 10)
    private String iataCode;

    @Column(name = "gps_code", length = 10)
    private String gpsCode;

    @Column(name = "local_code", length = 20)
    private String localCode;

    @Column(name = "home_link", length = 500)
    private String homeLink;

    @Column(name = "wikipedia_link", length = 500)
    private String wikipediaLink;

    @Column(name = "keywords", length = 1000)
    private String keywords;
}
