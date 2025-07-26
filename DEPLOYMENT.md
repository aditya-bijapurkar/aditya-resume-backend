# Deployment Guide for EC2 with AWS Secrets Manager

## Prerequisites

1. AWS Account with appropriate permissions
2. EC2 instance with Docker installed
3. RDS PostgreSQL instance
4. AWS Secrets Manager configured
5. IAM role with Secrets Manager permissions attached to EC2

## Setup Steps

### 1. Create AWS Secrets Manager Secret

1. Go to AWS Secrets Manager console
2. Create a new secret with type "Other type of secret"
3. Use the JSON structure from `aws-secrets-example.json`
4. Name the secret: `aditya-resume-backend-secrets`
5. Store the secret

**Important**: For the `STORED_CREDENTIAL_BASE64` field, you need to encode your `tokens/StoredCredential` file:
```bash
# Run the encoding script
chmod +x encode-tokens.sh
./encode-tokens.sh
```
Copy the base64 output and add it to your secret as `STORED_CREDENTIAL_BASE64`.

### 2. Configure IAM Role for EC2

Create an IAM role with the following policy:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue"
            ],
            "Resource": "arn:aws:secretsmanager:*:*:secret:aditya-resume-backend-secrets*"
        }
    ]
}
```

Attach this role to your EC2 instance.

### 3. RDS Configuration

1. Create a PostgreSQL RDS instance
2. Configure security groups to allow traffic from EC2
3. Update the secret with RDS connection details
4. Ensure SSL is enabled for RDS connections

### 4. Build and Deploy Docker Image

```bash
# Build the Docker image
docker build -t aditya-resume-backend:latest .

# Run the container
docker run -d \
  --name aditya-resume-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e AWS_REGION=ap-south-1 \
  -e AWS_SECRET_NAME=aditya-resume-backend-secrets \
  aditya-resume-backend:latest
```

### 5. Environment Variables for Docker

The following environment variables can be set when running the container:

- `SPRING_PROFILES_ACTIVE`: Set to `production` for EC2 deployment
- `AWS_REGION`: Your AWS region (default: ap-south-1)
- `AWS_SECRET_NAME`: Your secret name (default: aditya-resume-backend-secrets)

### 6. Health Check

The application includes a health check endpoint at `/actuator/health` which is used by Docker's health check.

## Security Considerations

1. **IAM Roles**: Use IAM roles instead of access keys
2. **Security Groups**: Restrict access to necessary ports only
3. **SSL**: Always use SSL for database connections
4. **Secrets**: Never commit secrets to version control
5. **Container Security**: Run container as non-root user (already configured)

## Troubleshooting

### Common Issues

1. **Secrets Manager Access Denied**
   - Verify IAM role is attached to EC2
   - Check IAM policy permissions

2. **Database Connection Issues**
   - Verify RDS security group allows EC2 traffic
   - Check SSL configuration
   - Verify database credentials in secrets

3. **Application Startup Issues**
   - Check application logs: `docker logs aditya-resume-backend`
   - Verify all required environment variables are in secrets

### Logs

View application logs:
```bash
docker logs -f aditya-resume-backend
```

### Health Check

Test the health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

## Production Considerations

1. **Load Balancer**: Use Application Load Balancer for high availability
2. **Auto Scaling**: Configure auto scaling groups
3. **Monitoring**: Set up CloudWatch monitoring
4. **Backup**: Configure automated backups for RDS
5. **SSL/TLS**: Use HTTPS for external access
6. **Container Registry**: Use ECR for storing Docker images

## Docker Compose (Future)

When you're ready to add the frontend, you can create a `docker-compose.yml` file:

```yaml
version: '3.8'
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - AWS_REGION=us-east-1
    depends_on:
      - db
    networks:
      - app-network

  frontend:
    build: ../frontend-directory
    ports:
      - "3000:3000"
    depends_on:
      - backend
    networks:
      - app-network

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: aditya_resume_db
      POSTGRES_USER: your_user
      POSTGRES_PASSWORD: your_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  postgres_data:
``` 