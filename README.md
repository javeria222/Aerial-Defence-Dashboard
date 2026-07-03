# FalconOps — Aerial Defence Dashboard

A JavaFX desktop application that simulates a military aerial defense command system. It features a live animated radar sweep, automated aircraft identification against a MySQL database of authorized aircraft, pilot/aircraft fleet management, and an alert workflow for unauthorized airspace incursions.

Built as a portfolio project to practice JavaFX UI development, event-driven animation, and JDBC database integration in a single desktop application.

![Java](https://img.shields.io/badge/Java-24-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.6-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.x-lightgrey)
![Maven](https://img.shields.io/badge/Build-Maven-red)

## Demo

<!--
Add screenshots or a short GIF here. Recommended shots:
  1. Login screen
  2. Radar view mid-sweep, showing blips
  3. The unauthorized-aircraft alert popup
  4. Aircraft/Pilot entry panels

Save images to a `screenshots/` folder in the repo root, then reference them like:
  ![Radar view](screenshots/radar-view.png)

A short GIF of the radar sweep detecting an unauthorized blip is the single
most compelling thing you can add here — it's the feature that's hardest to
convey in text and looks the most impressive in motion.
-->

![Radar view](screenshots/radar-view.png)
![Login screen](screenshots/login.png)

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
        └── Sounds/                  # Buzz, match success, mission failed, victory
```

## Getting Started

### Prerequisites

- **JDK 24** or later
- **MySQL** server running locally

Maven itself doesn't need to be installed separately — this project includes the Maven Wrapper (`mvnw`), which downloads and manages the correct Maven version automatically.

### 1. Set up the database

Run [`schema.sql`](schema.sql) against your local MySQL instance:

```bash
mysql -u root -p < schema.sql
```

This creates the `falconops` database, the `Aircraft` and `Pilot` tables, and the `falcon_admin` DB user the app connects as.

### 2. Configure the connection (if needed)

Connection details are currently hardcoded in `DB_GUI.getConnection()`:

```java
String url = "jdbc:mysql://localhost:3306/falconops";
String username = "falcon_admin";
String password = "1234";
```

Update these if your local MySQL setup differs.

### 3. Build and run

```bash
./mvnw clean javafx:run
```

> **Note:** Running via `mvnw` is the reliable way to launch this app. If you try to run `LoginController` directly from your IDE's Run button instead, you'll likely hit `Error: JavaFX runtime components are missing`. This happens because `LoginController` extends `javafx.application.Application` directly, and launching an `Application` subclass straight from the classpath (without a Java module system) triggers a JavaFX bootstrap check that fails. To make the IDE Run button work too, add a small launcher class that doesn't extend `Application`:
>
> ```java
> // src/main/java/falconops/Launcher.java
> package falconops;
>
> public class Launcher {
>     public static void main(String[] args) {
>         LoginController.main(args);
>     }
> }
> ```
>
> Then run `Launcher` instead of `LoginController` from your IDE.

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
