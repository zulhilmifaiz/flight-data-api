# flight-data-api

A REST API built with Spring Boot that serves Malaysian aviation data (airports and routes) sourced from the OurAirports and OpenFlights open datasets.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Data Sources](#data-sources)
- [Architecture](#architecture)
- [Database Schema](#database-schema)

---

## Overview

On startup, the application automatically loads two CSV datasets into MySQL:

- **134 Malaysian airports** filtered from the global OurAirports dataset
- **874 routes** involving at least one Malaysian airport, filtered from the OpenFlights routes dataset

The data is loaded only once. Subsequent restarts detect existing data and skip the load.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Language |
| Spring Boot 3.3.5 | Application framework |
| Spring Data JPA | Database access layer |
| MySQL 8 | Relational database |
| Hibernate | ORM / schema management |
| Lombok | Boilerplate reduction |
| OpenCSV 5.9 | CSV parsing |
| Maven | Build tool |

---

## Project Structure

```
flight-data-api/
├── airports.csv                          # OurAirports dataset (source data)
├── routes.dat                            # OpenFlights routes dataset (source data)
├── pom.xml                               # Maven dependencies
└── src/
    └── main/
        ├── java/com/flightdata/api/
        │   ├── FlightDataApiApplication.java     # Entry point
        │   ├── config/
        │   │   └── DataLoader.java               # Loads CSV data into MySQL on startup
        │   ├── controller/
        │   │   ├── AirportController.java         # GET /api/airports
        │   │   ├── RouteController.java           # GET /api/routes
        │   │   └── StatsController.java           # GET /api/stats
        │   ├── dto/
        │   │   ├── AirportDTO.java                # Airport API response shape
        │   │   ├── RouteDTO.java                  # Route API response shape
        │   │   └── StatsDTO.java                  # Stats API response shape
        │   ├── model/
        │   │   ├── Airport.java                   # JPA entity for airports table
        │   │   └── Route.java                     # JPA entity for routes table
        │   ├── repository/
        │   │   ├── AirportRepository.java         # Database queries for airports
        │   │   └── RouteRepository.java           # Database queries for routes
        │   └── service/
        │       ├── AirportService.java            # Airport business logic
        │       └── RouteService.java              # Route and stats business logic
        └── resources/
            └── application.properties            # App configuration
```

---

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8
- VS Code with **Extension Pack for Java** and **Spring Boot Extension Pack** installed

### Setup

**1. Clone or open the project in VS Code**

**2. Configure your MySQL password**

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.password=your_mysql_password
```

**3. Run the application**

In VS Code, open the Spring Boot Dashboard and click **Run** next to `flight-data-api`.

Or via terminal (requires Maven installed):
```bash
mvn spring-boot:run
```

**4. First startup**

The DataLoader will run automatically and log:
```
Starting data load...
Loaded 134 Malaysian airports (59 with IATA codes)
Loaded 874 MY-related routes from 67663 lines
Data load complete. Airports: 134, Routes: 874
```

The API is ready at `http://localhost:8080`.

---

## Configuration

All settings are in `src/main/resources/application.properties`.

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/flight_data` | MySQL connection URL |
| `spring.datasource.username` | `root` | MySQL username |
| `spring.datasource.password` | *(set this)* | MySQL password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Hibernate auto-creates/updates schema |
| `app.data.airports-file` | `airports.csv` | Path to airports CSV (relative to project root) |
| `app.data.routes-file` | `routes.dat` | Path to routes data file |
| `spring.data.web.pageable.default-page-size` | `20` | Default page size for paginated endpoints |
| `spring.data.web.pageable.max-page-size` | `100` | Maximum allowed page size |

---

## API Reference

Base URL: `http://localhost:8080`

---

### GET /api/airports

Returns a paginated list of all Malaysian airports.

**Query Parameters**

| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | integer | `0` | Page number (zero-based) |
| `size` | integer | `20` | Number of results per page |
| `sort` | string | none | Sort field and direction e.g. `name,asc` |

**Example Request**
```
GET /api/airports?page=0&size=3&sort=name,asc
```

**Example Response** `200 OK`
```json
{
  "content": [
    {
      "iataCode": "AOR",
      "icaoCode": "WMKA",
      "name": "Sultan Abdul Halim Airport",
      "type": "medium_airport",
      "latitude": 6.189667,
      "longitude": 100.398183,
      "elevationFt": 15,
      "municipality": "Alor Setar",
      "isoRegion": "MY-02",
      "scheduledService": "yes",
      "wikipediaLink": "https://en.wikipedia.org/wiki/Sultan_Abdul_Halim_Airport"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 3
  },
  "totalElements": 134,
  "totalPages": 45,
  "last": false,
  "first": true
}
```

---

### GET /api/airports/{iata}

Returns a single airport by its IATA code.

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `iata` | string | 3-letter IATA airport code (case-insensitive) |

**Example Request**
```
GET /api/airports/KUL
```

**Example Response** `200 OK`
```json
{
  "iataCode": "KUL",
  "icaoCode": "WMKK",
  "name": "Kuala Lumpur International Airport",
  "type": "large_airport",
  "latitude": 2.745578,
  "longitude": 101.709917,
  "elevationFt": 69,
  "municipality": "Kuala Lumpur",
  "isoRegion": "MY-10",
  "scheduledService": "yes",
  "wikipediaLink": "https://en.wikipedia.org/wiki/Kuala_Lumpur_International_Airport"
}
```

**Error Response** `404 Not Found`
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Airport not found with IATA code: XYZ"
}
```

---

### GET /api/routes

Returns routes involving Malaysian airports. Supports optional filtering by origin and/or destination IATA code.

**Query Parameters**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `origin` | string | No | Filter by source airport IATA code |
| `destination` | string | No | Filter by destination airport IATA code |

**Example Requests**
```
GET /api/routes                            # all 874 MY-related routes
GET /api/routes?origin=KUL                 # all routes departing KUL
GET /api/routes?destination=PEN            # all routes arriving at PEN
GET /api/routes?origin=KUL&destination=PEN # direct routes from KUL to PEN
```

**Example Response** `200 OK`
```json
[
  {
    "airline": "AK",
    "sourceAirport": "KUL",
    "destAirport": "PEN",
    "codeshare": null,
    "stops": 0,
    "equipment": "320"
  },
  {
    "airline": "MH",
    "sourceAirport": "KUL",
    "destAirport": "PEN",
    "codeshare": null,
    "stops": 0,
    "equipment": "738"
  }
]
```

---

### GET /api/stats/busiest-airports

Returns all Malaysian airports ranked by total number of routes (departing + arriving), highest first.

**Example Request**
```
GET /api/stats/busiest-airports
```

**Example Response** `200 OK`
```json
[
  { "iataCode": "KUL", "routeCount": 514 },
  { "iataCode": "BKI", "routeCount": 104 },
  { "iataCode": "PEN", "routeCount": 84 },
  { "iataCode": "SZB", "routeCount": 58 },
  { "iataCode": "KCH", "routeCount": 52 }
]
```

---

### GET /api/stats/summary

Returns a summary of the entire dataset.

**Example Request**
```
GET /api/stats/summary
```

**Example Response** `200 OK`
```json
{
  "totalAirports": 134,
  "totalRoutes": 874,
  "statesCovered": 15
}
```

---

## Data Sources

### Airports - OurAirports
- **URL:** https://davidmegginson.github.io/ourairports-data/airports.csv
- **Total rows:** ~85,000 airports worldwide
- **Filter applied:** `iso_country = "MY"` -> 134 Malaysian airports
- **Format:** RFC 4180 CSV with header row
- **Fields used:** ident, type, name, latitude_deg, longitude_deg, elevation_ft, iso_country, iso_region, municipality, scheduled_service, icao_code, iata_code, wikipedia_link

### Routes - OpenFlights
- **URL:** https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat
- **Total rows:** ~67,000 routes worldwide
- **Filter applied:** source airport OR destination airport is a Malaysian IATA code -> 874 routes
- **Format:** Comma-delimited, no header row, uses `\N` as null marker
- **Fields used:** airline, source airport, destination airport, codeshare, stops, equipment

---

## Architecture

The project follows a standard layered Spring Boot architecture:

```
HTTP Request
     |
     v
Controller        - receives request, delegates to service, returns ResponseEntity
     |
     v
Service           - business logic, entity-to-DTO mapping, error handling
     |
     v
Repository        - Spring Data JPA interfaces, database queries
     |
     v
MySQL Database    - airports and routes tables
```

**DTO pattern:** API responses use DTO classes (`AirportDTO`, `RouteDTO`, `StatsDTO`) that are separate from JPA entity classes (`Airport`, `Route`). Internal database fields such as primary keys and source IDs are never exposed in API responses.

**DataLoader:** Implements `ApplicationRunner` so it runs once after the Spring context starts. It checks `airportRepository.count() > 0` and skips if data already exists, making restarts fast.

---

## Database Schema

### airports

| Column | Type | Notes |
|---|---|---|
| id | BIGINT | Auto-generated primary key |
| source_id | BIGINT | Original OurAirports record ID |
| ident | VARCHAR(20) | Internal identifier e.g. WMKK |
| type | VARCHAR(50) | large_airport, medium_airport, small_airport, heliport |
| name | VARCHAR(255) | Full airport name |
| latitude | DOUBLE | Decimal degrees |
| longitude | DOUBLE | Decimal degrees |
| elevation_ft | INT | Nullable |
| continent | VARCHAR(10) | Always AS for Malaysia |
| iso_country | VARCHAR(10) | Always MY |
| iso_region | VARCHAR(10) | e.g. MY-10 for Selangor/KL |
| municipality | VARCHAR(100) | City name |
| scheduled_service | VARCHAR(5) | yes or no |
| icao_code | VARCHAR(10) | Indexed, nullable |
| iata_code | VARCHAR(10) | Indexed, nullable, used as business key |
| gps_code | VARCHAR(10) | Nullable |
| local_code | VARCHAR(20) | Nullable |
| home_link | VARCHAR(500) | Nullable |
| wikipedia_link | VARCHAR(500) | Nullable |
| keywords | VARCHAR(1000) | Comma-separated aliases, nullable |

### routes

| Column | Type | Notes |
|---|---|---|
| id | BIGINT | Auto-generated primary key |
| airline | VARCHAR(10) | 2-letter IATA airline code |
| airline_id | VARCHAR(10) | OpenFlights airline ID, nullable |
| source_airport | VARCHAR(10) | Origin IATA code, indexed |
| source_airport_id | VARCHAR(10) | OpenFlights airport ID, nullable |
| dest_airport | VARCHAR(10) | Destination IATA code, indexed |
| dest_airport_id | VARCHAR(10) | OpenFlights airport ID, nullable |
| codeshare | VARCHAR(5) | Y if codeshare flight, otherwise null |
| stops | INT | 0 = non-stop |
| equipment | VARCHAR(100) | Space-separated IATA aircraft type codes |
