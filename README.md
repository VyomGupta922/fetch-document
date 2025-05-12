# Document Management and Q&A System

A Spring Boot application for uploading, storing, and retrieving documents with Q&A capabilities.

## Features

- User authentication (JWT)
- Document upload and storage
- Document content extraction and storage
- Question answering based on document content
- Keyword search across documents

## Tech Stack

- Java 17
- Spring Boot 3.4.5
- MySQL 8
- Swagger UI for API documentation

## API Documentation

Swagger UI is available at:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Getting Started

### Prerequisites

- Java 17 JDK
- MySQL 8
- Maven

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/VyomGupta922/fetch-document.git
   
### Curl
Register User
curl --location 'http://localhost:8080/api/auth/register' \
--header 'accept: */*' \
--header 'Content-Type: application/json' \
--data-raw '{
  "username": "username",
  "email": "user@example.com",
  "password": "Password@123",
  "roles": ["ROLE_ADMIN"]
}'

Login (Get JWT Token)
bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "username": "your_username",
  "password": "your_password@123"
}'


Upload Document
bash
curl --location 'http://localhost:8080/api/documents/upload' \
--header 'Authorization: Bearer your_jwt_token_here' \
--form 'title="Document Title"' \
--form 'author="Author Name"' \
--form 'type="PDF"' \
--form 'file=@"/path/to/your_file.pdf"'

Search Documents by Keyword
bash
curl --location 'http://localhost:8080/api/documents/search?keyword=search_term&page=0&size=10&sortBy=id&direction=asc' \
--header 'Authorization: Bearer your_jwt_token_here'

Q&A System
Ask Question About Documents
bash
curl --location 'http://localhost:8080/api/qa/ask' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer your_jwt_token_here' \
--data '{
  "question": "your question here",
  "context": "additional context (optional)",
  "exactMatch": true,
  "maxResults": 5
}'

Usage Flow
Register a user (first time only)
1:Login to get JWT token

2:Use the token to:

3:Upload documents

4:Search documents
