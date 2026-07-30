# GymCore: Multi-Tenant SaaS Platform for Gym Franchises

![CI](https://github.com/RokoPecotic/gymcore-api/actions/workflows/ci.yml/badge.svg)

GymCore is a B2B SaaS backend for managing fitness franchises. It solves three concrete business problems: uneven member distribution across peak hours, lack of visibility into location performance, and data-driven decision-making for new location planning.

## The Problem

Gym franchises struggle with:
- Members clustering at the same peak hours, creating overcrowding while off-peak hours sit empty
- No centralized way to compare performance across multiple locations
- Guessing which equipment to buy when opening a new location instead of using real usage data

## What GymCore Does

- **Multi-tenant architecture** - each franchise is an isolated tenant with its own locations, staff, and members
- **Real-time occupancy tracking** - check-in/check-out system shows live gym capacity
- **Analytics engine** - peak hours, busiest days, average visit duration, per-location comparison
- **Waiting list** - members get notified when a crowded gym frees up
- **Role-based access** - five roles (Super Admin, Franchise Owner, Gym Manager, Trainer, Member) with JWT authentication

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Database | PostgreSQL 16 |
| Auth | Spring Security + JWT |
| Testing | JUnit 5, Mockito, Testcontainers |
| CI/CD | GitHub Actions |
| Containerization | Docker, Docker Compose |
| Frontend (demo) | HTML + Chart.js |

## Architecture

**Entity hierarchy:**

- Tenant (Franchise)
- Location (Physical Gym)
- Zone (Cardio, Free Weights, etc.)
- Equipment
- Member / Trainer
- CheckIn (occupancy tracking)
  Each level belongs to the one above it - a Tenant has many Locations, each Location has many Zones, each Zone has many Equipment items, and Members/Trainers belong to a Location.

**Code structure:** Controller → Service → Repository, with DTOs separating API contracts from JPA entities, and a global exception handler returning consistent JSON error responses.

## Running Locally

### Option 1 — Docker Compose (recommended)

```bash
docker-compose up
```

This starts PostgreSQL and the application together. API available at `http://localhost:8080`.

### Option 2 — Manual

```bash
docker run --name gymcore-db -e POSTGRES_DB=gymcore -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -p 5432:5432 -d postgres:16
./mvnw spring-boot:run
```

## API Overview

| Endpoint | Description |
|---|---|
| `POST /api/auth/register` | Register a new user |
| `POST /api/auth/login` | Login, returns JWT token |
| `GET/POST/PUT/DELETE /api/locations` | Location CRUD |
| `GET/POST/PUT/DELETE /api/zones` | Zone CRUD |
| `GET/POST/PUT/DELETE /api/equipments` | Equipment CRUD |
| `POST /api/checkins` | Member check-in |
| `PUT /api/checkins/checkout/{memberId}` | Member check-out |
| `GET /api/checkins/occupancy/{locationId}` | Real-time occupancy |
| `GET /api/checkins/heatmap/{locationId}` | Check-ins by hour |
| `GET /api/analytics/location/{locationId}` | Location analytics |
| `GET /api/analytics/franchise/{tenantId}` | Franchise-wide comparison |

## Testing

```bash
./mvnw test
```

43 tests: 39 unit tests (JUnit 5 + Mockito) covering all service layer business logic, and 2 integration tests (Testcontainers) verifying JWT auth flow and multi-tenant data isolation.

## Dashboard

A lightweight HTML dashboard (`/dashboard/index.html`) visualizes occupancy, hourly check-in heatmaps, and franchise-wide location comparison using Chart.js.

## Author

Roko Pecotić