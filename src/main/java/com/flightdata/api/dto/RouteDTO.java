package com.flightdata.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDTO {
    private String airline;
    private String sourceAirport;
    private String destAirport;
    private String codeshare;
    private Integer stops;
    private String equipment;
}
