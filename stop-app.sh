#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Stopping Blog Application${NC}"
echo -e "${BLUE}========================================${NC}"

echo -e "\n${GREEN}Stopping PostgreSQL Docker container...${NC}"
docker-compose down

echo -e "\n${GREEN}Application stopped successfully!${NC}"
echo -e "${BLUE}========================================${NC}"
