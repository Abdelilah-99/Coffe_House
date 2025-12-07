#!/bin/bash

# Script to test running with different profiles

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Profile Testing Script${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo ""

# Build first
echo -e "${YELLOW}📦 Building application...${NC}"
cd backend
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build failed!${NC}"
    exit 1
fi
cd ..

echo -e "${GREEN}✅ Build successful${NC}"
echo ""

# Get the JAR file name
JAR_FILE=$(ls backend/build/libs/*.jar 2>/dev/null | head -1)

if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}❌ No JAR file found!${NC}"
    exit 1
fi

echo -e "${GREEN}Found JAR: ${NC}$JAR_FILE"
echo ""

# Show options
echo -e "${YELLOW}Select how to run:${NC}"
echo "  1) Default profile (no profile set)"
echo "  2) Production profile (-Dspring.profiles.active=prod)"
echo "  3) Production profile (using environment variable)"
echo ""
read -p "Enter choice (1-3): " CHOICE

case $CHOICE in
    1)
        echo ""
        echo -e "${BLUE}Running with DEFAULT profile:${NC}"
        echo -e "${YELLOW}Command: java -jar $JAR_FILE${NC}"
        echo ""
        echo -e "${GREEN}Press Ctrl+C to stop${NC}"
        echo ""
        java -jar "$JAR_FILE"
        ;;
    2)
        echo ""
        echo -e "${BLUE}Running with PRODUCTION profile:${NC}"
        echo -e "${YELLOW}Command: java -Dspring.profiles.active=prod -jar $JAR_FILE${NC}"
        echo ""
        echo -e "${GREEN}Press Ctrl+C to stop${NC}"
        echo ""
        java -Dspring.profiles.active=prod -jar "$JAR_FILE"
        ;;
    3)
        echo ""
        echo -e "${BLUE}Running with PRODUCTION profile (env var):${NC}"
        echo -e "${YELLOW}Command: SPRING_PROFILES_ACTIVE=prod java -jar $JAR_FILE${NC}"
        echo ""
        echo -e "${GREEN}Press Ctrl+C to stop${NC}"
        echo ""
        SPRING_PROFILES_ACTIVE=prod java -jar "$JAR_FILE"
        ;;
    *)
        echo -e "${RED}Invalid choice${NC}"
        exit 1
        ;;
esac















