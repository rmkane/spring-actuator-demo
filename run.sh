#!/bin/bash

echo "Starting blog database services..."
docker-compose up -d

echo "Waiting for database to be ready..."
sleep 5

echo "Database services started successfully!"
echo "Connect to the database using:"
echo "  Host: localhost"
echo "  Port: 5432"
echo "  Database: blogdb"
echo "  Username: bloguser"
echo "  Password: blogpassword"

echo ""
echo "To stop the services, run: docker-compose down"