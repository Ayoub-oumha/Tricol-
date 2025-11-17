# Tricol Supply Chain Management System

A comprehensive supply chain management system for **Tricol**, a company specializing in professional clothing design and manufacturing. This system manages suppliers, products, purchase orders, inventory with FIFO valuation, and stock outbound slips.

## 📋 Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

## ✨ Features

### 1. Supplier Management
- Complete CRUD operations
- Search and filter suppliers
- Track supplier information (company name, address, contact person, email, phone, city, ICE)

### 2. Product Management
- Complete CRUD operations for products
- View available stock per product
- Alert system for minimum stock thresholds
- Track product reference, name, description, unit price, category, current stock, reorder point, and unit of measure

### 3. Purchase Order Management
- Create, modify, and cancel purchase orders
- View all orders and order details
- Filter by supplier, status, and period
- Automatic total amount calculation
- Order statuses: **EN_ATTENTE** (Pending), **VALIDÉE** (Validated), **LIVRÉE** (Delivered), **ANNULÉE** (Cancelled)
- Order reception functionality

### 4. Inventory Management (FIFO Method)
- **Stock entry**: Automatic recording when receiving supplier orders
- **Stock exit**: FIFO-based consumption (oldest lots used first)
- **Lot traceability**: Each stock entry identified by:
  - Unique lot number
  - Entry date
  - Quantity
  - Unit purchase price
  - Origin supplier order
- **Stock consultation**:
  - Available stock per product
  - FIFO stock valuation
  - Movement history
- **Alerts**: Notifications when product stock falls below minimum threshold

### 5. Stock Outbound Slip Management
- Create outbound slips for production workshops
- Multi-product addition with quantities
- Validation triggers automatic FIFO stock exits
- Cancellation available
- Full consultation and traceability

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **MySQL** (Database)
- **Liquibase** (Database migration)
- **Lombok** (Reduce boilerplate code)
- **MapStruct 1.6.3** (Object mapping)
- **Maven** (Build tool)
- **Spring Boot Validation**

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher ([Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html))
- **MySQL 8.0** or higher ([Download](https://dev.mysql.com/downloads/mysql/))
- **Maven 3.6+** (or use the included Maven wrapper)
- **Git** ([Download](https://git-scm.com/downloads))

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Ayoub-oumha/Tricol-.git
cd Tricol-Tests-Unitaires-et-Recherche-Avanc-e
```

### 2. Set Up MySQL Database

Open MySQL and create a new database (or let the application create it automatically):

```sql
CREATE DATABASE tricol_supplier_chain;
```

**Note:** The application is configured to create the database automatically if it doesn't exist.

## ⚙️ Configuration

### Database Configuration

Edit the `src/main/resources/application.properties` file to match your MySQL configuration:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/tricol_supplier_chain?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Important:** Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL password. If you don't have a password, leave it empty.

### Liquibase Configuration

The project uses Liquibase for database migrations. Configuration is already set in:
- `src/main/resources/application.properties`
- `src/main/resources/liquibase.properties`

Migrations are located in `src/main/resources/db/changelog/`.

## 🏃 Running the Application

### Option 1: Using Maven Wrapper (Recommended)

**Windows (PowerShell/CMD):**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Option 2: Using Maven

If you have Maven installed globally:

```bash
mvn spring-boot:run
```

### Option 3: Run the JAR

Build the project:
```bash
.\mvnw.cmd clean package
```

Run the generated JAR:
```bash
java -jar target/supplierchain-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Supplier Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/fournisseurs` | Get all suppliers |
| GET | `/fournisseurs/{id}` | Get supplier by ID |
| POST | `/fournisseurs` | Create new supplier |
| PUT | `/fournisseurs/{id}` | Update supplier |
| DELETE | `/fournisseurs/{id}` | Delete supplier |

### Product Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/produits` | Get all products |
| GET | `/produits/{id}` | Get product by ID |
| POST | `/produits` | Create new product |
| PUT | `/produits/{id}` | Update product |
| DELETE | `/produits/{id}` | Delete product |
| GET | `/produits/{id}/stock` | Get product stock details |

### Purchase Order Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/commandes` | Get all orders |
| GET | `/commandes/{id}` | Get order details |
| POST | `/commandes` | Create new order |
| PUT | `/commandes/{id}` | Update order |
| DELETE | `/commandes/{id}` | Delete order |
| GET | `/commandes/fournisseur/{id}` | Get orders by supplier |
| PUT | `/commandes/{id}/reception` | Receive order (generates stock entries) |

### Stock Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/stock` | Get global stock status |
| GET | `/stock/produit/{id}` | Get stock details by product with FIFO lots |
| GET | `/stock/mouvements` | Get all stock movements |
| GET | `/stock/mouvements/produit/{id}` | Get movements for specific product |
| GET | `/stock/alertes` | Get products below minimum threshold |
| GET | `/stock/valorisation` | Get total stock valuation (FIFO method) |

### Stock Outbound Slip Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/bons-sortie` | Get all outbound slips |
| GET | `/bons-sortie/{id}` | Get outbound slip details |
| POST | `/bons-sortie` | Create new slip (DRAFT status) |
| PUT | `/bons-sortie/{id}` | Update draft slip |
| PUT | `/bons-sortie/{id}/valider` | Validate slip (triggers FIFO exits) |
| PUT | `/bons-sortie/{id}/annuler` | Cancel draft slip |
| GET | `/bons-sortie/atelier/{atelier}` | Get slips by workshop |

### Example Request: Create a Supplier

```bash
curl -X POST http://localhost:8080/api/v1/fournisseurs \
  -H "Content-Type: application/json" \
  -d '{
    "raisonSociale": "ABC Textiles",
    "adresse": "123 Rue Exemple",
    "ville": "Casablanca",
    "personneContact": "John Doe",
    "email": "contact@abctextiles.ma",
    "telephone": "0522123456",
    "ice": "000123456789012"
  }'
```

## 🧪 Testing

### Run All Tests

```bash
.\mvnw.cmd test
```

### Run Specific Test Class

```bash
.\mvnw.cmd test -Dtest=Brief6TricolSupplierChainApplicationTests
```

### Generate Test Coverage Report

```bash
.\mvnw.cmd clean test jacoco:report
```

The coverage report will be available at `target/site/jacoco/index.html`

## 📁 Project Structure

```
src/
├── main/
│   ├── java/org/tricol/supplierchain/
│   │   ├── Brief6TricolSupplierChainApplication.java  # Main application class
│   │   ├── config/                                    # Configuration classes
│   │   ├── controller/                                # REST controllers
│   │   ├── dto/                                       # Data Transfer Objects
│   │   │   ├── request/                               # Request DTOs
│   │   │   └── response/                              # Response DTOs
│   │   ├── entity/                                    # JPA entities
│   │   ├── enums/                                     # Enumerations
│   │   ├── exception/                                 # Custom exceptions
│   │   ├── mapper/                                    # MapStruct mappers
│   │   ├── repository/                                # JPA repositories
│   │   └── service/                                   # Business logic services
│   └── resources/
│       ├── application.properties                     # Application configuration
│       ├── liquibase.properties                       # Liquibase configuration
│       └── db/changelog/                              # Database migrations
│           ├── master.xml
│           └── changes/
│               ├── 001-initial-schema.sql
│               ├── 002-create-bon-sortie.sql
│               ├── 003-add-atelier-to-bon-sortie.sql
│               ├── 004-add-montant-total-to-bon-sortie.sql
│               └── 101-data-initializer.sql
└── test/                                              # Test classes
    └── java/org/tricol/supplierchain/
```

## 🔧 Troubleshooting

### Database Connection Issues

If you encounter connection errors:
1. Verify MySQL is running
2. Check your database credentials in `application.properties`
3. Ensure the database exists or the application has permission to create it

### Port Already in Use

If port 8080 is already in use, you can change it in `application.properties`:
```properties
server.port=8081
```

### Maven Build Issues

Clean the project and rebuild:
```bash
.\mvnw.cmd clean install
```


## 👥 Contributing

project developed by:
- **Ayoub Oumha**





**Happy Coding! 🚀**
