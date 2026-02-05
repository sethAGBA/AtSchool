#!/bin/bash

BASE_URL="http://localhost:8080"
EMAIL="seth@atschool.com"
PASSWORD="superadmin"
SCHOOL_CODE="DEMO"

echo "1. Logging in..."
LOGIN_RES=$(curl -s -X POST "$BASE_URL/auth/login" \
     -H "Content-Type: application/json" \
     -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\", \"schoolCode\": \"$SCHOOL_CODE\"}")

TOKEN=$(echo $LOGIN_RES | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "Login failed. Response: $LOGIN_RES"
    exit 1
fi

echo "Token received."

echo "2. Getting School Years..."
YEARS_RES=$(curl -s -X GET "$BASE_URL/school-years" \
    -H "Authorization: Bearer $TOKEN")

# Extract first year ID (simple grep/cut for now, assuming JSON response is a list)
YEAR_ID=$(echo $YEARS_RES | grep -o '"id":[^,]*' | head -n 1 | cut -d':' -f2 | tr -d ' ')

if [ -z "$YEAR_ID" ]; then
    echo "No school years found or failed to parse. Response: $YEARS_RES"
    exit 1
fi

echo "Found Year ID: $YEAR_ID"

echo "3. Getting Periods for Year $YEAR_ID..."
PERIODS_RES=$(curl -s -X GET "$BASE_URL/school-years/$YEAR_ID/periods" \
    -H "Authorization: Bearer $TOKEN")

# Extract first period ID
PERIOD_ID=$(echo $PERIODS_RES | grep -o '"id":[^,]*' | head -n 1 | cut -d':' -f2 | tr -d ' ')

if [ -z "$PERIOD_ID" ]; then
    echo "No periods found in year $YEAR_ID."
    echo "Response: $PERIODS_RES"
    
    # Try creating one?
    exit 1
fi

echo "Found Period ID: $PERIOD_ID"

echo "4. Attempting to set status to ACTIVE for Period $PERIOD_ID..."
STATUS_RES=$(curl -s -v -X POST "$BASE_URL/academic-periods/$PERIOD_ID/set-status" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"status": "ACTIVE"}')

echo "Status Update Response: $STATUS_RES"

echo "5. Verifying status..."
PERIODS_RES_2=$(curl -s -X GET "$BASE_URL/school-years/$YEAR_ID/periods" \
    -H "Authorization: Bearer $TOKEN")

# Simple check if status is ACTIVE for that ID
echo "Refetched Periods: $PERIODS_RES_2"
