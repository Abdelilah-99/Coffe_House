#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Starting Blog Application${NC}"
echo -e "${BLUE}========================================${NC}"

echo -e "\n${YELLOW}[1/4] Starting PostgreSQL in Docker...${NC}"
docker-compose up -d

if [ $? -ne 0 ]; then
    echo -e "${RED}Failed to start Docker Compose${NC}"
    exit 1
fi

echo -e "\n${YELLOW}[2/4] Waiting for PostgreSQL to be ready...${NC}"
attempt=0
max_attempts=30

while [ $attempt -lt $max_attempts ]; do
    if docker-compose exec -T postgres pg_isready -U abdelilah -d blogdb > /dev/null 2>&1; then
        echo -e "${GREEN}PostgreSQL is ready!${NC}"
        break
    fi
    attempt=$((attempt+1))
    echo -e "Waiting... (${attempt}/${max_attempts})"
    sleep 1
done

if [ $attempt -eq $max_attempts ]; then
    echo -e "${RED}PostgreSQL failed to start in time${NC}"
    exit 1
fi
