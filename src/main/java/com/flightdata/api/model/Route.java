package com.flightdata.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes", indexes = {
        @Index(name = "idx_source_airport", columnList = "source_airport"),
        @Index(name = "idx_dest_airport", columnList = "dest_airport")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "airline", length = 10)
    private String airline;

    @Column(name = "airline_id", length = 10)
    private String airlineId;

    @Column(name = "source_airport", length = 10, nullable = false)
    private String sourceAirport;

    @Column(name = "source_airport_id", length = 10)
    private String sourceAirportId;

    @Column(name = "dest_airport", length = 10, nullable = false)
    private String destAirport;

    @Column(name = "dest_airport_id", length = 10)
    private String destAirportId;

    @Column(name = "codeshare", length = 5)
    private String codeshare;

    @Column(name = "stops")
    private Integer stops;

    @Column(name = "equipment", length = 100)
    private String equipment;
}
