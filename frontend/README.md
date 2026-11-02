# Smart Parking frontend

React (Vite) app for signing in, viewing live spot availability, and reserving a spot.

## Run it

Start auth-service (8081), parking-service (8082) and reservation-service (8083), then:

```
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. Vite forwards API calls to the services (see `vite.config.js`). To point at other addresses, set `AUTH_SERVICE_URL`, `PARKING_SERVICE_URL` or `RESERVATION_SERVICE_URL`.

With Docker, `docker compose up --build` from the project root serves the built app on the same port, using nginx to forward the same paths (see `nginx.conf`).

## Spot colours

| Colour | Status | Meaning |
|---|---|---|
| Green | `AVAILABLE` | Free to book |
| Amber | `RESERVED` | Held for a driver who has not arrived |
| Red | `OCCUPIED` | A vehicle is parked there |

## Booking flow

1. Pick a green spot and book it. This starts a 15-minute hold.
2. Pay to keep the spot.
3. Press **I've arrived** when you park. The spot turns red.
4. Press **Leave spot** when you go. The spot turns green.

Admin accounts can view everything and add lots and spots from **Management**, but they cannot book.
