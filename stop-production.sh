#!/bin/bash

# Stop Production Services Script

echo "🛑 Stopping Production Services..."

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Stop Backend
if [ -f backend.pid ]; then
    BACKEND_PID=$(cat backend.pid)
    echo -e "${YELLOW}Stopping Backend (PID: $BACKEND_PID)...${NC}"
    kill $BACKEND_PID 2>/dev/null || true
    rm backend.pid
    echo -e "${GREEN}✅ Backend stopped${NC}"
else
    echo -e "${YELLOW}⚠️  No backend PID file found${NC}"
fi

# Stop Frontend
if [ -f frontend.pid ]; then
    FRONTEND_PID=$(cat frontend.pid)
    echo -e "${YELLOW}Stopping Frontend (PID: $FRONTEND_PID)...${NC}"
    kill $FRONTEND_PID 2>/dev/null || true
    rm frontend.pid
    echo -e "${GREEN}✅ Frontend stopped${NC}"
else
    echo -e "${YELLOW}⚠️  No frontend PID file found${NC}"
fi

# Stop PostgreSQL
echo -e "${YELLOW}Stopping PostgreSQL...${NC}"
docker-compose down
echo -e "${GREEN}✅ PostgreSQL stopped${NC}"

echo -e "${GREEN}🎉 All services stopped${NC}"















