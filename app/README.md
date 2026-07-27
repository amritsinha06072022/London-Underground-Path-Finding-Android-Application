# London Underground Path-Finding Android Application

An Android app that finds the **fastest route** between any two London Underground stations. Enter a start and destination, and the app computes the quickest journey — including line changes and total travel time — using Dijkstra's shortest-path algorithm over a graph of the Tube network.

## Overview

The app models the entire Underground as a weighted graph stored in a local SQLite database. Stations are nodes, journeys between adjacent stations are edges, and edge weights are journey times in minutes. Line changes and station entry/exit are modelled explicitly as walking edges, so the "shortest" route accounts for the real cost of changing lines — not just the number of stops.

Everything runs **fully offline**: the network data ships with the app as SQL insert scripts and is loaded into an on-device database on first launch.

## Features

- **Fastest-route finding** between any two stations using Dijkstra's algorithm, minimising total journey time.
- **Predictive station search** — start and destination fields use autocomplete backed by the full station list.
- **Line-change awareness** — routes show which line to take, where to change, and the time each change adds.
- **Tube map viewer** — displays the standard Underground map within the app.
- **Self-contained offline database** — no network connection or external API required.

## How It Works

### Data model

The network is stored across three SQLite tables, populated from SQL scripts in `app/src/main/assets/`:

- **`Line`** — the 11 Underground lines, plus two pseudo-lines: `FOOT` (walking connections) and `GATE` (station entrances/gates).
- **`Station`** — one row per station *per line* it serves, so an interchange station appears multiple times. A `GATE` entry represents the station as a single searchable entrance.
- **`UnitJourney`** — an edge between two station nodes, with a journey time and an interchange type (`Tube` for on-train legs, `Foot` for changes and gate-to-platform walks).

This design lets a single searchable station name (the `GATE` node) connect via `FOOT` edges to each platform-level line node, so entering the network, changing lines, and exiting are all weighted edges the algorithm can reason about.

### Routing

1. `DBHandler` loads all stations and unit journeys from SQLite.
2. `Map` builds an **adjacency matrix** from the unit journeys, using journey time as the edge weight.
3. `findRoute()` runs **Dijkstra's algorithm** with a `PriorityQueue`, tracing back the shortest path and reconstructing the sequence of legs.
4. `RouteDetailActivity` renders the route: lines to take, change points with their added time, and the total journey time.

## Tech Stack

- **Language:** Java
- **Platform:** Android (min SDK 29, target/compile SDK 34)
- **Storage:** SQLite (`SQLiteOpenHelper`)
- **UI:** AndroidX AppCompat, Material Components, ConstraintLayout, ViewBinding
- **Build:** Gradle (Kotlin DSL)

## Project Structure

```
app/src/main/
├── java/com/example/undergroundpathfindingapplication/
│   ├── MainActivity.java         # Home screen (Show Map / Plan a Route)
│   ├── DisplayTubeMap.java       # Tube map viewer
│   ├── PlanMenu.java             # Route input with autocomplete
│   ├── RouteDetailActivity.java  # Route output and formatting
│   ├── Map.java                  # Graph + Dijkstra's algorithm
│   ├── DBHandler.java            # SQLite setup and queries
│   ├── Station.java              # Station model
│   ├── Line.java                 # Line model
│   └── UnitJourney.java          # Edge model (journey between two stations)
├── assets/
│   ├── LineInsertStatement.txt
│   ├── StationInsertStatement.txt
│   └── UnitJourneyInsertStatement.txt
└── res/                          # Layouts, drawables, values
```

## Getting Started

### Prerequisites

- Android Studio (Hedgehog or newer recommended)
- JDK 8+
- An Android device or emulator running API 29 or higher

### Build and Run

1. Clone the repository:
```bash
   git clone https://github.com/amritsinha06072022/London-Underground-Path-Finding-Android-Application.git
```
2. Open the project in Android Studio and let Gradle sync.
3. Select a device or emulator and press **Run**.

On first launch, the app builds and populates the local database from the bundled SQL scripts.

## Usage

1. From the home screen, tap **Plan a Route**.
2. Start typing a station name and pick from the autocomplete suggestions for both the start and destination.
3. Tap **Plan Route** to see the fastest journey, including lines, changes, and total time.
4. Use **Show Tube Map** from the home screen to view the network map.

## Possible Future Improvements

- Real-time service data (delays, closures) via the TfL API.
- Route options that also minimise the number of changes, not just time.
- Highlighting the computed route directly on the tube map.
- Unit tests around the pathfinding logic.

## Author

Built by [amritsinha06072022](https://github.com/amritsinha06072022) in February 2025.
