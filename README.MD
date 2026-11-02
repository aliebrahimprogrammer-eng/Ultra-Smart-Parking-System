# Smart Parking System

A microservices app where drivers find a free parking spot, reserve it for 15 minutes, pay, and park.

## Problem statement

Drivers waste time looking for parking, and two people can end up going to the same spot. This project lets users see live spot availability and reserve a spot, and it guarantees that **only one user can reserve a given spot**, even when many requests arrive at the same time.

## Team

| Name  | Owns |
|-------|---|
| Hasan | auth-service (authentication and JWT security) |
| Ali   | parking-service (lots, spots, locking) |
| Sara  | reservation-service (reservations, fees) |
| Alya  | Docker, MongoDB Atlas setup, integration testing, documentation |

## Technologies

- Java 17 and Spring Boot
- Spring Security with JWT
- MongoDB Atlas
- Docker and Docker Compose
- React (frontend)
- Postman (API testing)

## Architecture

Three independent Spring Boot services, each with its own database on one Atlas cluster. Services talk to each other over HTTP and never read each other's collections.

| Service | Port | Database | Collections |
|---|---|---|---|
| auth-service | 8081 | `auth_db` | `users` |
| parking-service | 8082 | `parking_db` | `parking_lots`, `parking_spots` |
| reservation-service | 8083 | `reservation_db` | `vehicles`, `reservations`, `payments` |
| frontend | 5173 | none | none |

reservation-service calls parking-service to reserve a spot when a booking is created, and to free it when a booking is cancelled or expires. All services validate the same JWT using a shared secret. The frontend sends each request to the service that owns it.

## ERD Diagram

![ERD](ERD.png)

## Booking flow

1. A user books a spot. The spot becomes `RESERVED` and the reservation is `PENDING`.
2. The user has 15 minutes to pay, which makes the reservation `CONFIRMED`. If they don't pay, it becomes `EXPIRED` and the spot is freed.
3. The user arrives, and the spot becomes `OCCUPIED`.
4. The user leaves, and the spot becomes `AVAILABLE` again.

## Concurrency

When two users try to reserve the same spot, parking-service protects it in two layers:

1. A `ReentrantLock` per spot id, so only one thread at a time handles that spot.
2. An atomic MongoDB update that only succeeds if the spot is still `AVAILABLE`. If it is already taken, the API returns **409 Conflict**.

## Running the app

### Requirements

- Docker Desktop
- A MongoDB Atlas cluster (free M0 tier)
- Java 17, only if you run a service without Docker

### 1. Set up MongoDB Atlas

1. Create a free M0 cluster at cloud.mongodb.com.
2. Under **Security → Database Access**, create a database user with read/write access. Use a password with only letters and numbers.
3. Under **Security → Network Access**, add `0.0.0.0/0` (allow from anywhere) for development.
4. Click **Connect → Drivers → Java** and copy the connection string.

The databases and collections are created automatically the first time each service saves data.

### 2. Set environment variables

Copy the example file and fill in your values:

```
cp .env.example .env
```

| Variable | Description |
|---|---|
| `MONGODB_URI` | Atlas connection string, with your real password in place of the placeholder |
| `JWT_SECRET` | Valid base64 of at least 32 characters, the same for every service. Letters and numbers only is safest |
| `JWT_EXPIRATION_MS` | How long a login token lasts, in milliseconds |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Optional. Creates this admin account the first time auth-service starts. Registration can only create normal users |

Never commit `.env`.

### 3. Run with Docker

```
docker compose up --build
```

Then open the app at http://localhost:5173.

The services are also available directly:

- auth-service: http://localhost:8081
- parking-service: http://localhost:8082 (Swagger UI at `/swagger-ui.html`)
- reservation-service: http://localhost:8083

Stop everything with `docker compose down`.

### Run a service without Docker

Run each service with the repo root as its working directory, so it can find `.env`. In IntelliJ, set this in the run configuration.

## API endpoints

Send the token as `Authorization: Bearer <token>`. "Owner" means the user the record belongs to, or an admin.

### auth-service

| Endpoint | Access |
|---|---|
| `POST /auth/users/register` | public |
| `POST /auth/users/login` (send `email` and `password`, the token comes back in `message`) | public |

### parking-service

| Endpoint | Access |
|---|---|
| `GET /lots`, `GET /lots/{id}` | logged in |
| `GET /spots/lot/{lotId}`, `GET /spots/lot/{lotId}/available` | logged in |
| `POST /lots`, `POST /spots` | ADMIN |
| `DELETE /lots/{id}`, `DELETE /spots/{id}` | ADMIN |
| `PUT /lots/{id}`, `PUT /spots/{id}` | logged in |
| `PATCH /spots/{id}/reserve` | reservation-service only |
| `PATCH /spots/{id}/occupy`, `/release`, `/cancel-reservation` | logged in |

### reservation-service

| Endpoint | Access |
|---|---|
| `POST /api/reservations` | USER |
| `GET /api/reservations` | ADMIN |
| `GET /api/reservations/user/{email}` | owner |
| `GET /api/reservations/{id}`, `PUT /api/reservations/{id}/cancel` | owner |
| `GET /api/payments/reservation/{reservationId}` | owner |
| `POST /api/payments/{id}/confirm` | owner |
| `POST /api/vehicles` | logged in |
| `GET /api/vehicles` | ADMIN |
| `GET /api/vehicles/user/{email}` | owner |
| `GET`, `PUT`, `DELETE /api/vehicles/{id}` | owner |

## Status codes

| Code | Meaning |
|---|---|
| 200 / 201 / 204 | Success / created / deleted |
| 400 | Invalid input |
| 401 | Missing or invalid token, or a failed login |
| 403 | Not allowed for your role, or not your record |
| 404 | Not found |
| 409 | Conflict, such as a spot already taken or an email or plate already used |
| 503 | reservation-service could not reach parking-service |

## Known limitations

- There are no check-in or check-out endpoints yet, so the frontend updates the spot directly and records a finished stay as `CANCELLED`.
- The booking amount must be above 0, but the client sets it instead of it being calculated from the lot's hourly rate.
- Any logged-in user can update lots and spots, and can occupy, release or cancel the reservation on any spot.
- Some errors, such as a reservation or vehicle that doesn't exist, still return 500 instead of 404.

## Screenshots

Add images to `docs/screenshots/` and link them here.

## Project structure

```
Ultra-Smart-Parking-System/
├── auth-service/
├── parking-service/
├── reservation-service/
├── frontend/
├── docker-compose.yml
├── .env.example
├── ERD.png
└── README.MD
```
