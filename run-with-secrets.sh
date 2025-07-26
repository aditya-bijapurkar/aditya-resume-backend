#!/bin/bash

set -e

SECRET_NAME=${AWS_SECRET_NAME:-"aditya-resume-backend-secrets"}
AWS_REGION=${AWS_REGION:-"ap-south-1"}

echo "Fetching secrets from AWS Secrets Manager: $SECRET_NAME in region: $AWS_REGION"

SECRETS_JSON=$(aws secretsmanager get-secret-value \
    --secret-id "$SECRET_NAME" \
    --region "$AWS_REGION" \
    --query 'SecretString' \
    --output text)

if [ $? -ne 0 ]; then
    echo "Failed to fetch secrets from AWS Secrets Manager"
    exit 1
fi

echo "Successfully fetched secrets from AWS Secrets Manager"

export DB_PASSWORD=$(echo "$SECRETS_JSON" | jq -r '.DB_PASSWORD // empty')
export SMTP_PASSWORD=$(echo "$SECRETS_JSON" | jq -r '.SMTP_PASSWORD // empty')
export GOOGLE_ACCESS_KEY_BASE64=$(echo "$SECRETS_JSON" | jq -r '.GOOGLE_ACCESS_KEY_BASE64 // empty')
export STORED_CREDENTIAL_BASE64=$(echo "$SECRETS_JSON" | jq -r '.STORED_CREDENTIAL_BASE64 // empty')


if [ -z "$DB_PASSWORD" ]; then
    echo "Error: DB_PASSWORD not found in secrets"
    exit 1
fi

if [ -z "$SMTP_PASSWORD" ]; then
    echo "Error: SMTP_PASSWORD not found in secrets"
    exit 1
fi

if [ -z "$GOOGLE_ACCESS_KEY_BASE64" ]; then
    echo "Error: GOOGLE_ACCESS_KEY_BASE64 not found in secrets"
    exit 1
fi

if [ -z "$STORED_CREDENTIAL_BASE64" ]; then
    echo "Error: STORED_CREDENTIAL_BASE64 not found in secrets"
    exit 1
fi

echo "All required secrets found and exported as environment variables"

echo "Starting application with Docker Compose..."
docker-compose up -d

echo "Application started successfully!"
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"