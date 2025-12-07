#!/bin/bash

##############################################################################
# Coffee House - Complete Project Startup Script
# This script starts the entire Coffee House application using Docker Compose
##############################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}"
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║             Coffee House - Project Startup                  ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

check_dependencies() {
    print_info "Checking dependencies..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    print_success "Docker is installed"
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    print_success "Docker Compose is installed"
}

cleanup_old_containers() {
    print_info "Cleaning up old containers..."
    docker-compose down --remove-orphans 2>/dev/null || true
    print_success "Cleanup complete"
}

build_images() {
    print_info "Building Docker images..."
    docker-compose build --no-cache
    print_success "Images built successfully"
}

start_services() {
    print_info "Starting services..."
    docker-compose up -d
    print_success "Services started"
}

wait_for_services() {
    print_info "Waiting for services to be healthy..."
    
    # Wait for PostgreSQL
    echo -n "  Waiting for PostgreSQL..."
    local count=0
    while [ $count -lt 30 ]; do
        if docker-compose exec -T postgres pg_isready -U abdelilah -d blogdb &> /dev/null; then
            echo -e " ${GREEN}✓${NC}"
            break
        fi
        sleep 2
        count=$((count + 1))
        echo -n "."
    done
    
    # Wait for Backend
    echo -n "  Waiting for Backend API..."
    count=0
    while [ $count -lt 60 ]; do
        if curl -s http://localhost:8080/actuator/health &> /dev/null; then
            echo -e " ${GREEN}✓${NC}"
            break
        fi
        sleep 2
        count=$((count + 1))
        echo -n "."
    done
    
    # Wait for Frontend
    echo -n "  Waiting for Frontend..."
    count=0
    while [ $count -lt 30 ]; do
        if curl -s http://localhost:4200 &> /dev/null; then
            echo -e " ${GREEN}✓${NC}"
            break
        fi
        sleep 2
        count=$((count + 1))
        echo -n "."
    done
    
    print_success "All services are healthy"
}

show_status() {
    echo ""
    print_info "Service Status:"
    docker-compose ps
    echo ""
}

show_urls() {
    echo -e "${GREEN}"
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                   Application URLs                          ║"
    echo "╠══════════════════════════════════════════════════════════════╣"
    echo "║  Frontend:   http://localhost:4200                          ║"
    echo "║  Backend:    http://localhost:8080                          ║"
    echo "║  Database:   localhost:5432                                 ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

show_logs() {
    echo ""
    print_info "To view logs, use:"
    echo "  docker-compose logs -f              # All services"
    echo "  docker-compose logs -f backend      # Backend only"
    echo "  docker-compose logs -f frontend     # Frontend only"
    echo "  docker-compose logs -f postgres     # Database only"
    echo ""
}

show_commands() {
    echo ""
    print_info "Useful commands:"
    echo "  docker-compose stop               # Stop all services"
    echo "  docker-compose restart            # Restart all services"
    echo "  docker-compose down               # Stop and remove containers"
    echo "  docker-compose down -v            # Stop and remove containers + volumes"
    echo "  ./stop-app.sh                     # Stop the application"
    echo ""
}

# Main execution
main() {
    print_header
    
    # Parse arguments
    REBUILD=false
    CLEAN=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --rebuild)
                REBUILD=true
                shift
                ;;
            --clean)
                CLEAN=true
                shift
                ;;
            --help)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  --rebuild    Rebuild Docker images before starting"
                echo "  --clean      Remove all containers, volumes, and rebuild"
                echo "  --help       Show this help message"
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                echo "Use --help for usage information"
                exit 1
                ;;
        esac
    done
    
    # Check dependencies
    check_dependencies
    
    # Clean if requested
    if [ "$CLEAN" = true ]; then
        print_info "Performing clean start..."
        docker-compose down -v
    else
        cleanup_old_containers
    fi
    
    # Build if requested
    if [ "$REBUILD" = true ] || [ "$CLEAN" = true ]; then
        build_images
    fi
    
    # Start services
    start_services
    
    # Wait for services to be ready
    wait_for_services
    
    # Show status
    show_status
    
    # Show URLs
    show_urls
    
    # Show additional info
    show_logs
    show_commands
    
    print_success "Coffee House application is running!"
    echo ""
}

# Run main function
main "$@"

