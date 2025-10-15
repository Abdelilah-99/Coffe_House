#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${RED}========================================${NC}"
echo -e "${RED}  Destroying PostgreSQL Setup${NC}"
echo -e "${RED}========================================${NC}"
echo -e "${YELLOW}WARNING: This will delete all database data!${NC}\n"

echo -e "${BLUE}[1/4] Stopping and removing containers...${NC}"
docker-compose down
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Containers stopped and removed${NC}"
else
    echo -e "${YELLOW}No containers to stop or already stopped${NC}"
fi

echo -e "\n${BLUE}[2/4] Removing PostgreSQL image...${NC}"
docker rmi postgres:15-alpine
if [ $? -eq 0 ]; then
    echo -e "${GREEN}PostgreSQL image removed${NC}"
else
    echo -e "${YELLOW}Image not found or already removed${NC}"
fi

echo -e "\n${BLUE}[3/4] Removing PostgreSQL volume...${NC}"
docker volume rm 01-blog_postgres_data
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Volume removed${NC}"
else
    echo -e "${YELLOW}Volume not found or already removed${NC}"
fi

echo -e "\n${BLUE}[4/4] Cleaning up orphaned volumes...${NC}"
docker volume prune -f
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Orphaned volumes cleaned${NC}"
fi

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}  PostgreSQL Destruction Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "${BLUE}All PostgreSQL containers, images, and data have been removed.${NC}\n"
