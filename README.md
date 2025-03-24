# Activity Log Service

## Overview

The Activity Log Service is a microservice responsible for logging user activities in the Recipe Sharing Platform. It
receives event-based logs from the main application via a Feign client and stores them in a separate database.

## Features

* Exposes REST API endpoints to store and retrieve activity logs

* Uses a dedicated database for logging

## Technologies Used

* Spring Boot (REST API)

* Spring Data JPA (Persistence layer)

* MySQL (Database)

* Lombok (Code simplification)

* JUnit (Testing)

## API Endpoints

1. Log Activity

Endpoint: ```POST``` /api/v1/activity-log

Response:

```
{
"userId": "123e4567-e89b-12d3-a456-426614174000",
"action": "User registered with username: johndoe",
"createdOn": "2007-12-03T10:15:30:55.000000"
}
```

2. Get All Logs For User

Endpoint: ```GET``` /api/v1/activity-log

Query Parameter: ```userId``` (UUID of the user)

Example Request:
```
GET /api/activity-log?userId=123e4567-e89b-12d3-a456-426614174000
```

Response:
```
[{
"userId": "123e4567-e89b-12d3-a456-426614174000",
"action": "User registered",
"createdOn": "2007-12-03T10:15:30:55.000000."
}]
```

3. Delete Activity Log by User ID

Endpoint: ```DELETE``` /api/v1/activity-log

Query Parameter: ```userId``` (UUID of the user)

Example Request:
```
DELETE /api/activity-log?userId=123e4567-e89b-12d3-a456-426614174000
```

Response:

```200 OK``` - Activity logs deleted successfully

```404 Not Found``` - No logs found for the given user
## Setup & Run Locally

1. Clone the repository:
```
git clone https://github.com/tsekovTriesCoding/activity-log-service.git
cd activity-log-service
```

Configure the database:
Update application.yml:
```
spring:
datasource:
url: jdbc:mysql://localhost:3306/activity_log_db
username: root
password: yourpassword
```

Build and run the application:
```
mvn clean install
mvn spring-boot:run
```
Access the API at http://localhost:8081/api/v1/activity-log
 
