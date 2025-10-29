#!/bin/bash

# Base URL of your API
BASE_URL="http://localhost:8080/api/auth/register"

# Optional: path to a test image file
PROFILE_IMAGE="test_profile.jpg"

# Loop to create 10 users
for i in {1..100000}; do
  FIRST_NAME="User"
  LAST_NAME="Test"
  USERNAME="user${i}"
  PASSWORD="bchbch1"
  EMAIL="user${i}@gmail.com"

  echo "Registering $USERNAME ..."

  curl -X POST "$BASE_URL" \
    -H "Content-Type: multipart/form-data" \
    -F "user={\"firstName\":\"$FIRST_NAME\",\"lastName\":\"$LAST_NAME\",\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\",\"email\":\"$EMAIL\"};type=application/json" \

done
