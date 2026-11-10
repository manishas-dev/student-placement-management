# Student Placement Management System

A desktop-based Student Placement Management System developed using Java Swing, JDBC, MySQL, and Maven.

## Overview

This application helps manage student placement activities through a simple graphical user interface.

## Features

- **Dashboard:** Overview of placement statistics.
- **Student Management:** Add, view, edit, and delete student records.
- **Company Management:** Manage recruiting companies and job opportunities.
- **Application Management:** Track student applications and their statuses.
- **Database Integration:** Store and retrieve information using MySQL.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Core programming language |
| Java Swing | Graphical user interface |
| JDBC | Database connectivity |
| MySQL | Relational database |
| Maven | Project and dependency management |
| VS Code | Development environment |

## Database

Database name: `placement_management`

Main tables:
- `students`
- `companies`
- `applications`

## Setup Instructions

1. Clone or download this repository.
2. Install Java and Maven.
3. Install MySQL.
4. Create a database named `placement_management`.
5. Create the required database tables.
6. Create a `config.properties` file in the project root.

Add your local database credentials:

```properties
db.url=jdbc:mysql://localhost:3306/placement_management
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

7. Build the project:

```bash
mvn clean compile
```

8. Run the `Main.java` file from your IDE.

## Security

Database credentials are stored locally in `config.properties`. This file is excluded from version control through `.gitignore`.

## Author

Manisha S

B.E Computer Science and Engineering

---

*An academic project exploring Java desktop application development, database connectivity, and CRUD operations.*
