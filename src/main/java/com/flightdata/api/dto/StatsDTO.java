package com.flightdata.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDTO {

    private Long totalAirports;
    private Long totalRoutes;
    private Long statesCovered;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusiestAirport {
        private String iataCode;
        private Long routeCount;
    }
}
