#!/bin/bash

##############################################################################
# Coffee House - Project Stop Script
# This script stops the entire Coffee House application
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
    echo "║             Coffee House - Project Stop                     ║"
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

stop_services() {
    print_info "Stopping services..."
    docker-compose stop
    print_success "Services stopped"
}

remove_containers() {
    print_info "Removing containers..."
    docker-compose down --remove-orphans
    print_success "Containers removed"
}

remove_volumes() {
    print_info "Removing volumes (this will delete all data)..."
    docker-compose down -v
    print_success "Volumes removed"
}

show_status() {
    echo ""
    print_info "Current status:"
    docker-compose ps 2>/dev/null || echo "No containers running"
    echo ""
}

# Main execution
main() {
    print_header
    
    # Parse arguments
    REMOVE_VOLUMES=false
    REMOVE_CONTAINERS=true
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --volumes)
                REMOVE_VOLUMES=true
                shift
                ;;
            --keep-containers)
                REMOVE_CONTAINERS=false
                shift
                ;;
            --help)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  --volumes           Also remove volumes (deletes all data)"
                echo "  --keep-containers   Only stop containers, don't remove them"
                echo "  --help              Show this help message"
                echo ""
                echo "Examples:"
                echo "  $0                      # Stop and remove containers"
                echo "  $0 --keep-containers    # Just stop containers"
                echo "  $0 --volumes            # Stop, remove containers and volumes"
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                echo "Use --help for usage information"
                exit 1
                ;;
        esac
    done
    
    # Stop services
    if [ "$REMOVE_VOLUMES" = true ]; then
        remove_volumes
    elif [ "$REMOVE_CONTAINERS" = true ]; then
        remove_containers
    else
        stop_services
    fi
    
    # Show status
    show_status
    
    print_success "Coffee House application stopped"
    echo ""
    
    if [ "$REMOVE_VOLUMES" = false ]; then
        print_info "Data volumes preserved. Use './stop-app.sh --volumes' to remove all data."
    fi
    
    print_info "To start again, run: ./run-app.sh"
    echo ""
}

# Run main function
main "$@"
