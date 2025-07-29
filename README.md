# Online Grocery Ordering System

A full-featured web application for online grocery ordering with a React frontend and Spring Boot backend.

## Project Overview

This application provides a seamless digital platform for customers to browse, search, and order groceries online, and for administrators to manage users, products, and orders efficiently.

## Current Status

✅ **Phase 1: Foundation (Completed)** - Project setup, Git repo, database schema design, and backend/frontend project structure.

✅ **Phase 2: Core Backend (Completed)** - User authentication, JWT handling, and full CRUD APIs for products and users.

✅ **Phase 3: Core Frontend (Completed)** - React components for registration, login, product display, search, and admin dashboard. Connected to backend APIs.

🚧 **Phase 4: E-Commerce Workflow (Next)** - Shopping cart, checkout process (COD), and customer order history pages.

⏳ **Phase 5: Admin & Advanced Features (Planned)** - Enhanced admin dashboard, customer search, product review system, and email notifications.

⏳ **Phase 6: Testing & Deployment (Planned)** - End-to-end testing, performance optimization, security audit, and production deployment.

### Key Features

-   **Customer Features**:

    -   User registration and authentication
    -   Product browsing and search
    -   Shopping cart management
    -   Order placement with Cash on Delivery
    -   Order history tracking
    -   Product reviews and ratings

-   **Admin Features**:
    -   Product management (CRUD operations)
    -   Customer search and management
    -   Order management and tracking
    -   Dashboard with key metrics

## Tech Stack

### Frontend

-   **Framework**: React 18 with Vite
-   **Routing**: React Router
-   **State Management**: Zustand, TanStack Query
-   **HTTP Client**: Axios
-   **Styling**: Tailwind CSS
-   **Testing**: Jest, React Testing Library

### Backend

-   **Framework**: Spring Boot 3.x with Java 17
-   **Security**: Spring Security with JWT
-   **Database**: PostgreSQL with Spring Data JPA
-   **Email**: Mailgun/SendGrid integration
-   **Testing**: JUnit, Mockito, Spring Boot Test

### Infrastructure

-   **Database**: PostgreSQL
-   **Deployment**: Heroku/AWS Elastic Beanstalk
-   **CI/CD**: GitHub Actions

## Prerequisites

Before running this application, ensure you have the following installed:

-   **Java 17** or higher
-   **Node.js 18** or higher
-   **PostgreSQL 13** or higher
-   **Maven 3.8** or higher
-   **Git**

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd online-grocery-ordering-system
```

### 2. Database Setup

1. Install PostgreSQL and create a database:

```sql
CREATE DATABASE grocery_db;
```

2. Update database credentials in `backend/src/main/resources/application.properties`

### 3. Backend Setup

```bash
cd backend
cp .env.example .env
# Edit .env with your configuration
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

```bash
cd frontend
cp .env.example .env
# Edit .env with your configuration
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

## Environment Variables

### Backend (.env)

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/grocery_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-db-password

# Authentication & Security
JWT_SECRET=your-super-secret-key-that-is-long-and-secure
JWT_EXPIRATION_MS=86400000

# Email Service
EMAIL_API_KEY=your-mailgun-api-key
EMAIL_API_URL=https://api.mailgun.net/v3/your-domain/messages
```

### Frontend (.env)

```bash
# Application Configuration
VITE_API_BASE_URL=http://localhost:8080/api
```

## Running the Project

1. Start PostgreSQL database
2. Run the backend: `cd backend && mvn spring-boot:run`
3. Run the frontend: `cd frontend && npm run dev`
4. Access the application at `http://localhost:5173`

## API Documentation

The REST API provides the following endpoints:

### Authentication

-   `POST /api/auth/register` - Register a new customer
-   `POST /api/auth/login` - Authenticate user and return JWT

### Products

-   `GET /api/products` - Get all products (public)
-   `GET /api/products/{id}` - Get product by ID (public)
-   `POST /api/products` - Create product (admin only)
-   `PUT /api/products/{id}` - Update product (admin only)
-   `DELETE /api/products/{id}` - Delete product (admin only)

### Orders

-   `GET /api/orders` - Get customer order history
-   `POST /api/orders` - Create new order

### Reviews

-   `POST /api/products/{id}/reviews` - Add product review
-   `GET /api/products/{id}/reviews` - Get product reviews

### Admin

-   `GET /admin/users` - Search users by name
-   `GET /admin/orders` - Get all orders
-   `GET /admin/dashboard` - Get dashboard metrics

## Project Structure

```
project-root/
├── README.md
├── CHANGELOG.md
├── .gitignore
├── backend/
│   ├── src/main/java/com/grocerystore/
│   │   ├── config/       # Security, JWT, CORS configurations
│   │   ├── controller/   # REST API controllers
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── entity/       # JPA entities
│   │   ├── repository/   # Spring Data JPA repositories
│   │   ├── service/      # Business logic
│   │   └── exception/    # Custom exception handlers
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
└── frontend/
    ├── public/
    ├── src/
    │   ├── assets/
    │   ├── components/   # Reusable UI components
    │   ├── hooks/        # Custom React hooks
    │   ├── lib/          # API client
    │   ├── pages/        # Page components
    │   ├── router/       # React Router setup
    │   ├── services/     # API service functions
    │   └── stores/       # Zustand state stores
    ├── .env.example
    └── package.json
```

## Testing

### Backend Tests

```bash
cd backend
mvn test
```

### Frontend Tests

```bash
cd frontend
npm test
```

### End-to-End Tests

```bash
cd frontend
npm run test:e2e
```

## Deployment

The application is designed for deployment on cloud platforms:

-   **Frontend**: Static hosting (Netlify, Vercel, AWS S3/CloudFront)
-   **Backend**: Container deployment (Heroku, AWS Elastic Beanstalk)
-   **Database**: Managed PostgreSQL (AWS RDS, Heroku Postgres)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Chirag Singhal** ([@chirag127](https://github.com/chirag127))

## Last Updated

2025-07-29T15:52:16.391Z
