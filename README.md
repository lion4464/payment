# Payment Service

This project is a RESTful API service designed for transaction management.

## Instructions for launching the project:

### 📌 Requirements
- Java 17
- PostgreSQL 12+
- Gradle

### 🚀 Launching the project

1. Clone the project:
   ```bash
   git clone https://github.com/lion4464/payment.git
2. 
   ```bash 
    cd payment```
3. migrate sql scripts

## 🐘 PostgreSQL SQL script
```sql
CREATE DATABASE test_transaction;

CREATE SEQUENCE transactions_generate_number_seq START 1;

CREATE TABLE transactions (
  id UUID PRIMARY KEY NOT NULL,
  user_id UUID NOT NULL,
  transaction_date TIMESTAMP NOT NULL,
  paid_amount BIGINT NOT NULL,
  payment_type VARCHAR(50) NOT NULL,
  status VARCHAR(20),
  transaction_number VARCHAR(50) NOT NULL,
  created_by UUID NOT NULL,
  created_date BIGINT NOT NULL,
  generate_number BIGINT NOT NULL,
  modified_by UUID,
  modified_date BIGINT NULL,
  amount BIGINT NOT NULL,
  expired_date BIGINT NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT chk_amount_positive CHECK (amount > 0),
  CONSTRAINT chk_paid_amount_positive CHECK (paid_amount > 0),
  CONSTRAINT fk_transactions_user_id
  FOREIGN KEY (user_id)
  REFERENCES users(id)
  ON DELETE RESTRICT
  ON UPDATE CASCADE
);

CREATE UNIQUE INDEX idx_transaction_number ON transactions USING btree (transaction_number);
CREATE INDEX idx_transactions_user_id ON transactions USING btree(user_id);
CREATE INDEX idx_transactions_transaction_date ON transactions USING btree(transaction_date);
CREATE INDEX idx_transactions_user_date ON transactions USING btree(user_id, transaction_date);

CREATE TABLE users (
  id UUID PRIMARY KEY NOT NULL,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255),
  role VARCHAR(100),
  balance BIGINT NOT NULL DEFAULT 0 CHECK (balance >= 0),
  created_by UUID,
  created_date BIGINT,
  modified_by UUID,
  modified_date BIGINT,
  deleted BOOLEAN DEFAULT FALSE
);

CREATE UNIQUE INDEX idx_users_username ON users USING btree (username);

```

## ✅ Build the Application

First, package the application into a JAR file using Gradle

```./gradlew build```

### 🐳 Build Docker Image
```docker image build -t app .```

###  🐳 Run the Docker Container

```docker run -d -p 8080:8080 --name my-payment  app```