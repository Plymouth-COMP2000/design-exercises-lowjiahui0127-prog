# Restaurant Management Application

Author: Low Jia Hui  
Student ID: BSSE2509252

## Project Overview
This Android mobile application was developed as part of the MAL2017 Software Engineering 2 coursework. The application serves as a comprehensive restaurant management system with dual-role functionality:
- Guest Role: Browse menu items, make/edit/cancel reservations, manage notification preferences
- Staff Role: Manage menu items (CRUD operations), handle customer reservations, manage notification preferences

## The application integrates
- A remote RESTful API for user authentication
- A local SQLite database for menu and reservation management
- Role-based access control
- Notification system with user preferences

## Third-Party Libraries
- **Volley (v1.2.1):** Used for managing network requests and API communication with the central server.
- **Material Design Components:** Used for implementing modern UI elements like Bottom Navigation, CardViews, and Floating Action Buttons.
- **SQLite OpenHelper:** Used for local relational database management.

## Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or physical device (API 24+)
