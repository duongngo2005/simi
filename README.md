# Simi

Simi is a consignment-sales project with a Spring Boot backend (`simi-be`) and a React/Vite frontend (`simi-fe`).

## Prerequisites

- Java 21
- Node.js 20 or newer
- MySQL 8
- Internet access on the first Maven Wrapper run, so Maven can download its configured distribution

## Start from a fresh local database

Create an empty database named `simi_db` in MySQL. Do not manually run individual migration files: Flyway applies every migration automatically when the backend starts.

```sql
CREATE DATABASE simi_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Configure the backend:

```bash
cp simi-be/.env.example simi-be/.env
```

Fill the required values in `simi-be/.env`. `DB_URL` is optional and defaults to `jdbc:mysql://localhost:3306/simi_db`.

Run the backend:

```bash
cd simi-be
./mvnw test
./mvnw spring-boot:run
```

Run the frontend in a second terminal:

```bash
cd simi-fe
npm ci
npm run build
npm run dev
```

The frontend runs on Vite's local URL (normally `http://localhost:5173`) and the backend uses the `/api/v1` context path.

## Migration rule

Never edit a Flyway migration that has already been applied to a shared or demo database. Add a new versioned migration instead. If a local demo database reports a checksum mismatch, recreate that disposable database rather than changing migration history.

## Demo scope and current behaviour

- The customer catalog only lists products in `AVAILABLE` status.
- Online checkout supports `COD` and VNPAY (`ONLINE`). The staff POS flow supports `CASH` only.
- Shipping is a built-in fee table based on the selected province and order subtotal; it is not connected to a real delivery provider.
- Order confirmation emails are sent to the email of the logged-in account after the relevant order/payment event. A valid mail configuration is required; email delivery failure is logged and does not undo a successfully created order.
- The AI widget is an optional product-search assistant using Gemini. It does not decide payment, delivery, cancellation, refund, or consignment policies.
- This project has no gateway-refund flow. A paid online order cannot be cancelled directly.
