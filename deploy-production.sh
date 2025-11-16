#!/bin/bash

# Production Deployment Script for Coffee House Blog
# Server IP: 84.46.244.105

echo "🚀 Starting Production Deployment..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Stop any running instances
echo -e "${YELLOW}📦 Stopping existing services...${NC}"
./stop-app.sh 2>/dev/null || true

# Build Backend
echo -e "${YELLOW}🔨 Building Backend (Spring Boot)...${NC}"
cd backend
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Backend build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Backend build successful${NC}"
cd ..

# Build Frontend for Production
echo -e "${YELLOW}🔨 Building Frontend (Angular) for Production...${NC}"
cd frontend
npm install
npm run build -- --configuration=production
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Frontend build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Frontend build successful${NC}"
cd ..

# Start PostgreSQL
echo -e "${YELLOW}🐘 Starting PostgreSQL...${NC}"
docker-compose up -d postgres
sleep 5

# Start Backend with Production Profile
echo -e "${YELLOW}🌱 Starting Backend (Spring Boot) with production profile...${NC}"
cd backend
nohup java -jar -Dspring.profiles.active=prod build/libs/*.jar > ../backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > ../backend.pid
echo -e "${GREEN}✅ Backend started with PID: $BACKEND_PID${NC}"
cd ..

# Wait for backend to start
echo -e "${YELLOW}⏳ Waiting for backend to start...${NC}"
sleep 10

# Serve Frontend (you can use nginx or node serve)
echo -e "${YELLOW}🌐 Starting Frontend Server...${NC}"
cd frontend
# Install serve if not already installed
npm install -g serve 2>/dev/null || true
nohup serve -s dist/frontend/browser -l 4200 > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../frontend.pid
echo -e "${GREEN}✅ Frontend started with PID: $FRONTEND_PID${NC}"
cd ..

echo ""
echo -e "${GREEN}🎉 Deployment Complete!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "Backend API: ${YELLOW}http://84.46.244.105:8080${NC}"
echo -e "Frontend App: ${YELLOW}http://84.46.244.105:4200${NC}"
echo -e "PostgreSQL: ${YELLOW}localhost:5432${NC}"
echo ""
echo -e "Logs:"
echo -e "  Backend: ${YELLOW}tail -f backend.log${NC}"
echo -e "  Frontend: ${YELLOW}tail -f frontend.log${NC}"
echo ""
echo -e "To stop: ${YELLOW}./stop-production.sh${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"



