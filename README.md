# Blog Application with Spring Boot and Flyway

This is a Spring Boot application with integrated Flyway database migrations for a simple blog system.

## Features

- **Spring Boot 3.5.14** with Java 21
- **PostgreSQL 17** database
- **Flyway** for database migrations
- **JPA/Hibernate** for data access
- **Sample blog data** pre-loaded through migrations

## Database Structure

The application includes three main entities:

- `User`: Blog authors
- `Blog`: Blog posts
- `Comment`: Comments on blog posts

## Getting Started

### Prerequisites

- Docker (for database setup)
- Java 21
- Maven

### Setup

1. Start the PostgreSQL database:
