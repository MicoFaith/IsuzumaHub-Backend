#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "Testing Diagnostic Lab Management System"
echo "======================================="

# Base URL
BASE_URL="http://localhost:8080/api"

# Function to make API calls
call_api() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    
    if [ -z "$token" ]; then
        if [ -z "$data" ]; then
            curl -s -X $method "$BASE_URL$endpoint"
        else
            curl -s -X $method -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint"
        fi
    else
        if [ -z "$data" ]; then
            curl -s -X $method -H "Authorization: Bearer $token" "$BASE_URL$endpoint"
        else
            curl -s -X $method -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "$data" "$BASE_URL$endpoint"
        fi
    fi
}

# Test 1: Login as admin
echo -e "\n${GREEN}Test 1: Login as admin${NC}"
ADMIN_TOKEN=$(call_api "POST" "/auth/login" '{"username":"admin","password":"password123"}')
echo "Admin Token: $ADMIN_TOKEN"

# Test 2: Login as patient
echo -e "\n${GREEN}Test 2: Login as patient${NC}"
PATIENT_TOKEN=$(call_api "POST" "/auth/login" '{"username":"patient1","password":"password123"}')
echo "Patient Token: $PATIENT_TOKEN"

# Test 3: Get all lab tests
echo -e "\n${GREEN}Test 3: Get all lab tests${NC}"
call_api "GET" "/lab-tests"

# Test 4: Get patient's appointments
echo -e "\n${GREEN}Test 4: Get patient's appointments${NC}"
call_api "GET" "/appointments/my-appointments" "" "$PATIENT_TOKEN"

# Test 5: Get all appointments (admin)
echo -e "\n${GREEN}Test 5: Get all appointments (admin)${NC}"
call_api "GET" "/admin/appointments" "" "$ADMIN_TOKEN"

# Test 6: Create new appointment
echo -e "\n${GREEN}Test 6: Create new appointment${NC}"
call_api "POST" "/appointments" '{"labTestId":1,"appointmentDate":"2024-04-20T10:00:00"}' "$PATIENT_TOKEN"

# Test 7: Upload report (admin)
echo -e "\n${GREEN}Test 7: Upload report (admin)${NC}"
curl -s -X POST -H "Authorization: Bearer $ADMIN_TOKEN" -F "file=@src/main/resources/test-report.txt" "$BASE_URL/admin/appointments/1/report"

# Test 8: Download report (patient)
echo -e "\n${GREEN}Test 8: Download report (patient)${NC}"
curl -s -X GET -H "Authorization: Bearer $PATIENT_TOKEN" "$BASE_URL/appointments/1/report" --output downloaded-report.txt

echo -e "\n${GREEN}Testing completed!${NC}"
echo "Check downloaded-report.txt for the downloaded report content." 