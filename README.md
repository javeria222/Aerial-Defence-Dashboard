# FalconOps — Aerial Defence Dashboard

A JavaFX desktop application that simulates a military aerial defense command system. It features a live animated radar sweep, automated aircraft identification against a MySQL database of authorized aircraft, pilot/aircraft fleet management, and an alert workflow for unauthorized airspace incursions.

Built as a portfolio project to practice JavaFX UI development, event-driven animation, and JDBC database integration in a single desktop application.

![Java](https://img.shields.io/badge/Java-24-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.6-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.x-lightgrey)
![Maven](https://img.shields.io/badge/Build-Maven-red)

## Demo
![Demo Video]([https://www.linkedin.com/posts/javeria-razzaq-782765312_java-javafx-oop-ugcPost-7355999862007488513-H8kS/?utm_source=share&utm_medium=member_desktop&rcm=ACoAAE99gBIBSf4OuVvduR-44fG7YNPJ_5jgfj4](https://www.linkedin.com/feed/update/urn:li:activity:7355999885348810752/))


## Features

- **Login screen** with session authentication gate
- **Animated radar display** — a rotating sweep line with concentric range rings, rendered entirely with JavaFX shapes and transforms
- **Live aircraft simulation** — blips spawn at random headings and travel toward the radar center over time
- **Automated ID verification** — when a blip crosses the sweep line, its ID is checked against authorized aircraft in the database in real time
- **Unauthorized incursion alerts** — unrecognized aircraft trigger an audible alarm and an on-screen alert, then route the operator to a pilot deployment panel
- **Aircraft & Pilot management** — add/remove aircraft and pilots through dedicated CRUD panels backed by MySQL
- **Pilot-to-aircraft linking** — assign an available pilot to a deployed aircraft
- **Sound design** — distinct audio cues for detection, successful pilot linking, mission failure, and victory
- **Hidden mini-game** — a Space Invaders-style arena unlocks after a successful pilot/aircraft link (easter egg, not core functionality)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 24 |
| UI Framework | JavaFX 17 (Controls, FXML, Media, Web, Swing interop) |
| Build Tool | Maven (with Maven Wrapper) |
| Database | MySQL, via JDBC (`mysql-connector-j`) |
| UI Extensions | ControlsFX, FormsFX, ValidatorFX, Ikonli, BootstrapFX, TilesFX |
| Game Engine | FXGL (used for the Space Invaders mini-game) |
| Testing | JUnit 5 |

This project runs on the classpath (not as a Java Platform Module — there's no `module-info.java`), since several of the dependencies above aren't fully modularized. This keeps the build simpler and avoids Java module-path conflicts.

## Project Structure

```
AerialDefenceDashboard/
├── pom.xml
├── schema.sql
├── mvnw, mvnw.cmd
└── src/main/
    ├── java/falconops/
    │   ├── LoginController.java     # App entry point (main), auth flow, screen routing
    │   ├── LoginView.java           # Login screen UI
    │   ├── SessionManager.java      # Login credential check
    │   ├── DB_GUI.java              # Main dashboard: sidebar, radar panel, aircraft/pilot CRUD panels
    │   ├── RadarSimulation.java     # Radar rings + rotating sweep line animation
    │   ├── UserGenerate.java        # Spawns simulated aircraft blips at intervals
    │   ├── AirCraftDetection.java   # Detects sweep/blip collisions, triggers ID checks
    │   ├── CompareDB.java           # Queries authorized aircraft IDs from MySQL
    │   ├── Linking.java             # Pilot-to-aircraft assignment panel
    │   ├── SoundPlayer.java         # WAV playback (looping + one-shot)
    │   └── SpaceInvaders.java       # Bonus mini-game
    └── resources/
        ├── falconOpsIcon.jpeg
        ├── falconOpsIconN.jpeg
        └── Sounds/                  # Buzz, match success, mission failed, victory
```

## Getting Started

### Prerequisites

- **JDK 24** or later
- **MySQL** server running locally

Maven itself doesn't need to be installed separately — this project includes the Maven Wrapper (`mvnw`), which downloads and manages the correct Maven version automatically.

### 2. Configure the connection (if needed)

Connection details are currently hardcoded in `DB_GUI.getConnection()`:

```java
String url = "jdbc:mysql://localhost:3306/falconops";
String username = "falcon_admin";
String password = "1234";
```

### 3. Build and run

```bash
./mvnw clean javafx:run
```


### 4. Log in

Default credentials (hardcoded, in `SessionManager.java`):

```
Username: admin
Password: password
```

## Known Limitations & Next Steps

Built as a learning project, not production software:

- DB and login credentials are currently hardcoded rather than externalized to env vars/config
- No password hashing yet
- No automated test coverage
- Detected-ID history isn't persisted between sessions

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE) for details.
