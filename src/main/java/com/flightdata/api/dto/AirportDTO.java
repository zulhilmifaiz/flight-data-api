package com.flightdata.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirportDTO {
    private String iataCode;
    private String icaoCode;
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private Integer elevationFt;
    private String municipality;
    private String isoRegion;
    private String scheduledService;
    private String wikipediaLink;
}
