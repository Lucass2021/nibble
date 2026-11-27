# Nibble

[![CI](https://github.com/Lucass2021/nibble/actions/workflows/ci.yml/badge.svg)](https://github.com/Lucass2021/nibble/actions/workflows/ci.yml)

AI study app: type a topic, get a feed of cards and questions. Spring Boot backend (Java 17, Maven, PostgreSQL).

## Setup

```bash
docker compose up -d
git config core.hooksPath .githooks
```

The `pre-commit` hook formats staged Java files with Spotless (Palantir Java Format).

## Tests

```bash
cd backend && ./mvnw verify
```

Runs unit tests (Mockito), `@WebMvcTest` with Spring Security, repository tests against a real PostgreSQL via Testcontainers (Docker required), and `spotless:check`.
