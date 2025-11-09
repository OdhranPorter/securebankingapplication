# 💰 Secure Java Banking Application

A **desktop banking application** built with **Java**, focusing on **core banking functionalities** and **secure software development practices**.  

This project implements a **Java Swing GUI** for user interaction, a **MySQL database backend**, and robust **encryption and authentication mechanisms** to ensure data privacy and system integrity.

> 🧠 *Developed by Odhran Porter*  

---

## 🔐 Key Features

### 🧾 Secure User Registration
- New accounts are created using:
  - A **unique cryptographic salt** for each user.
  - A **hashed password** generated via `PBKDF2WithHmacSHA256`.
- Prevents rainbow table and brute-force attacks.

### 🔑 Multi-Factor Authentication (MFA)
- Two-step login process for enhanced security:
  1. **Password Verification:** User-entered password is hashed and validated against the stored hash.
  2. **One-Time Password (OTP):** A 6-digit OTP (simulated) is required for a second layer of authentication.

### 🏦 Account Management
Once authenticated, users gain access to essential banking operations:
- **View Balance:** Securely fetch and display the current account balance.
- **Deposit:** Add funds to the account, validated and logged.
- **Withdraw:** Perform secure withdrawals with checks to prevent negative balances.

### 🧰 Secure Database Operations
- All SQL operations use **`PreparedStatement`** to eliminate **SQL Injection** vulnerabilities.
- Transactions and error handling are carefully managed to maintain data consistency.

---

## 🧱 System Architecture

