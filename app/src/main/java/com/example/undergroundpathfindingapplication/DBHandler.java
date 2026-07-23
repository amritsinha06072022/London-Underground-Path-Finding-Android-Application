package com.example.undergroundpathfindingapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class DBHandler extends SQLiteOpenHelper { // This class creates the database which is vital
    // to this application.

    private static final String DB_NAME = "Trains";

    private static final int DB_VERSION = 1;

    private SQLiteDatabase database;

    private Context context;

    public DBHandler(Context context) { // This constructor creates an instance of a database.
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.database = getWritableDatabase();

        // These queries were used to troubleshoot the data
//        String query1 = "DELETE FROM Station WHERE LineID NOT IN ('METR', 'GATE'); ";
//        String query2 = "DELETE FROM UnitJourney WHERE LineID NOT IN ('METR', 'FOOT'); ";
//        String query3 = "DELETE FROM Line WHERE LineID NOT IN ('METR', 'GATE', 'FOOT'); ";
//
//        database.execSQL(query1);
//        database.execSQL(query2);
//        database.execSQL(query3);

        // The following block of code checks if the database has been loaded by running a test
        // query. If it has not been loaded, then the insertData method is called.
        try {
            String query = "SELECT * FROM Line;";
            Cursor cursor = database.rawQuery(query, null);
        } catch (SQLException e) {
            insertData();

        }
    }

    private void insertData() { // This private method creates and populates the entities.
        Log.d("status", "Running insertData() method");

        // The queries below drop the tables if they exist.
        database.execSQL("DROP TABLE IF EXISTS Line");
        database.execSQL("DROP TABLE IF EXISTS Station");
        database.execSQL("DROP TABLE IF EXISTS UnitJourney");

        // The following are Create statements for the Line, Station and UnitJourney entities.
        String LineDDLCreateStatement =
                "CREATE TABLE Line (\n" +
                        "\tLineID VARCHAR(5) NOT NULL PRIMARY KEY,\n" +
                        "\tLineName TEXT NOT NULL,\n" +
                        "\tLineColour TEXT NOT NULL\n" +
                        ");\n";

        database.execSQL(LineDDLCreateStatement);

        String StationDDLCreateStatement =
                "CREATE TABLE Station (\n" +
                        "\tStationID VARCHAR(5) NOT NULL,\n" +
                        "\tStationName TEXT NOT NULL,\n" +
                        "\tLineID VARCHAR(5) NOT NULL,\n" +
                        "\tFOREIGN KEY (LineID) REFERENCES Line (LineID),\n" +
                        "\tPRIMARY KEY(StationID, LineID)\n" +
                        ");\n";

        database.execSQL(StationDDLCreateStatement);

        String UnitJourneyDDLCreateStatement =
                "CREATE TABLE UnitJourney (\n" +
                        "\tStationFromID VARCHAR(30) NOT NULL,\n" +
                        "\tStationToID VARCHAR(30) NOT NULL,\n" +
                        "\tLineID CHAR(4) NOT NULL,\n" +
                        "\tJourneyTime REAL NOT NULL,\n" +
                        "\tInterchangeType VARCHAR(6),\n" +
                        "\tPRIMARY KEY(StationFromID, StationToID, LineID)\n" +
                        ");\n";

        database.execSQL(UnitJourneyDDLCreateStatement);

        ExecuteDML("LineInsertStatement.txt");
        ExecuteDML("StationInsertStatement.txt");
        ExecuteDML("UnitJourneyInsertStatement.txt");


        // Lines 86 to 100 were used to test if the data has been entered. Hence they are now commented
//        String lineQuery = "SELECT LineName FROM Line;";
//
//        Cursor cursorLine = database.rawQuery(lineQuery, null);
//
//        Log.d("Count", String.valueOf(cursorLine.getCount()));
//
//        cursorLine.close();
//
//        String stationQuery = "SELECT StationName FROM Station;";
//
//        Cursor cursorStation = database.rawQuery(stationQuery, null);
//
//        Log.d("Count", String.valueOf(cursorStation.getCount()));
//
//        cursorStation.close();


    }

    public ArrayList<String> getAllGATEStations() { // This method gets all the stations with the
        // LineID 'GATE' and puts them in an ArrayList of String

        ArrayList<String> stations = new ArrayList<>();
        String query = // Query to retrieve all StationNames with LineID Gate
                "SELECT StationName " +
                "FROM Station " +
                "WHERE LineID = 'GATE';";

        Cursor cursor = database.rawQuery(query, null);

        int count = cursor.getCount();

        Log.d("Count", String.valueOf(count));

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            String station = cursor.getString(0);
            stations.add(station);
            // Log.d("Added station", station);
        }

        cursor.close();

        return stations;

    }

    public ArrayList<Station> getAllStations() { // This method retrieves all the stations and
        // places them in an ArrayList of Station
        ArrayList<Station> stations = new ArrayList<>();
        String query = // Query to retrieve all the Stations
                "SELECT StationName, LineName, LineColour " +
                "FROM Station, Line " +
                "WHERE Station.LineID = Line.LineID;";

        Cursor cursor = database.rawQuery(query, null);

        int count = cursor.getCount();

        Log.d("Count", String.valueOf(count));

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            String lineName = cursor.getString(1);
            String lineColour = cursor.getString(2);
            String stationName = cursor.getString(0);
            Line line = new Line(lineName, lineColour);
            Station station = new Station(stationName, line);
            stations.add(station);
        }

        Log.d("Size", String.valueOf(stations.size()));

        cursor.close();

        return stations;
    }

    public ArrayList<Line> getAllLines() { // This method retrieves all Lines
        ArrayList<Line> lines = new ArrayList<Line>();

        String query = "SELECT L.LineName, L.LineColour FROM Line L ORDER BY L.LineName;";

        Cursor cursor = database.rawQuery(query, null);

        int count = cursor.getCount();

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            Line line = new Line(cursor.getString(0), cursor.getString(1));
            lines.add(line);
        }

        cursor.close();

        return lines;


    }

    public ArrayList<UnitJourney> getAllUnitJourneys() {
        ArrayList<UnitJourney> unitJourneys = new ArrayList<>();

        String gateQuery = "SELECT startgate.StationName AS StartStationName, " +
                "startgateline.LineName AS StartStationLineName, " +
                "startgateline.LineColour AS StartStationLineColour, " +
                "startplatform.StationName AS EndStationName, " +
                "startplatformline.LineName AS EndStationLineName, " +
                "startplatformline.LineColour AS EndStationLineColour, " +
                "UJLine.LineName AS JourneyLineName, " +
                "UJLine.LineColour AS JourneyLineColour, " +
                "UnitJourney.JourneyTime AS JourneyTime, " +
                "UnitJourney.InterchangeType AS InterchangeType\n" +
                "FROM UnitJourney AS UnitJourney, Station AS startgate, Station AS startplatform, Line AS startgateline, Line AS startplatformline, Line AS UJLine\n" +
                "WHERE UnitJourney.LineID='FOOT'\n" +
                "AND startgate.StationID = UnitJourney.StationFromID\n" +
                "AND startplatform.StationID = UnitJourney.StationToID\n" +
                "AND startgate.LineID = startgateline.LineID\n" +
                "AND startplatform.LineID = startplatformline.LineID\n" +
                "AND UJLine.LineID = UnitJourney.LineID\n" +
                "AND startgate.LineID = 'GATE';";

        String journeyQuery = "SELECT startgate.StationName AS StartStationName, " +
                "startgateline.LineName AS StartStationLineName, " +
                "startgateline.LineColour AS StartStationLineColour, " +
                "startplatform.StationName AS EndStationName, " +
                "startplatformline.LineName AS EndStationLineName, " +
                "startplatformline.LineColour AS EndStationLineColour, " +
                "UJLine.LineName AS JourneyLineName, " +
                "UJLine.LineColour AS JourneyLineColour, " +
                "UnitJourney.JourneyTime AS JourneyTime, " +
                "UnitJourney.InterchangeType AS InterchangeType\n" +
                "FROM UnitJourney AS UnitJourney, Station AS startgate, Station AS startplatform, Line AS startgateline, Line AS startplatformline, Line AS UJLine\n" +
                "WHERE startgate.StationID = UnitJourney.StationFromID\n" +
                "AND startplatform.StationID = UnitJourney.StationToID\n" +
                "AND startgate.LineID = startgateline.LineID\n" +
                "AND startplatform.LineID = startplatformline.LineID\n" +
                "AND UJLine.LineID = UnitJourney.LineID\n" +
                "AND startgate.LineID <> 'GATE'" +
                ";\n";

        String interchangesQuery = "SELECT startgate.StationName AS StartStationName, " +
                "startgateline.LineName AS StartStationLineName, " +
                "startgateline.LineColour AS StartStationLineColour, " +
                "startplatform.StationName AS EndStationName, " +
                "startplatformline.LineName AS EndStationLineName, " +
                "startplatformline.LineColour AS EndStationLineColour, " +
                "UJLine.LineName AS JourneyLineName, " +
                "UJLine.LineColour AS JourneyLineColour, " +
                "UnitJourney.JourneyTime AS JourneyTime, " +
                "UnitJourney.InterchangeType AS InterchangeType\n" +
                "FROM UnitJourney AS UnitJourney, Station AS startgate, Station AS startplatform, Line AS startgateline, Line AS startplatformline, Line AS UJLine\n" +
                "WHERE UnitJourney.LineID='FOOT'\n" +
                "AND startgate.StationID = UnitJourney.StationFromID\n" +
                "AND startplatform.StationID = UnitJourney.StationToID\n" +
                "AND startgate.LineID = startgateline.LineID\n" +
                "AND startplatform.LineID = startplatformline.LineID\n" +
                "AND UJLine.LineID = UnitJourney.LineID\n" +
                "AND startgate.LineID <> 'GATE';";

        Cursor gateCursor = database.rawQuery(gateQuery, null);

        Cursor journeyCursor = database.rawQuery(journeyQuery, null);

        Cursor interchangeCursor = database.rawQuery(interchangesQuery, null);

        int gateCount = gateCursor.getCount();
        Log.d("Gate Count", String.valueOf(gateCount));

        int journeyCount = journeyCursor.getCount();
        int interchangeCount = interchangeCursor.getCount();

        for (int i = 0; i < gateCount; i++) {
            gateCursor.moveToPosition(i);
            Line startLine = new Line (gateCursor.getString(1), gateCursor.getString(2));
            Station stationFrom = new Station(gateCursor.getString(0), startLine);
            Line endLine = new Line (gateCursor.getString(4), gateCursor.getString(5));
            Station stationTo = new Station(gateCursor.getString(3), endLine);
            Line journeyLine = new Line (gateCursor.getString(6), gateCursor.getString(7));
            double time = gateCursor.getDouble(8);
            String interchangeType = gateCursor.getString(9);
            UnitJourney unitJourney = new UnitJourney(stationFrom, stationTo, journeyLine, time, interchangeType);
            Log.d("Unit Journey", String.valueOf(unitJourney));
            unitJourneys.add(unitJourney);
        }

        gateCursor.close();

        for (int i = 0; i < journeyCount; i++) {
            journeyCursor.moveToPosition(i);
            Line startLine = new Line (journeyCursor.getString(1), journeyCursor.getString(2));
            Station stationFrom = new Station(journeyCursor.getString(0), startLine);
            Line endLine = new Line (journeyCursor.getString(4), journeyCursor.getString(5));
            Station stationTo = new Station(journeyCursor.getString(3), endLine);
            Line journeyLine = new Line (journeyCursor.getString(6), journeyCursor.getString(7));
            double time = journeyCursor.getDouble(8);
            String interchangeType = journeyCursor.getString(9);
            UnitJourney unitJourney = new UnitJourney(stationFrom, stationTo, journeyLine, time, interchangeType);
            Log.d("Unit Journey Journey", String.valueOf(unitJourney));
            unitJourneys.add(unitJourney);
        }

        journeyCursor.close();

        for (int i = 0; i < interchangeCount; i++) {
            interchangeCursor.moveToPosition(i);
            Line startLine = new Line (interchangeCursor.getString(1), interchangeCursor.getString(2));
            Station stationFrom = new Station(interchangeCursor.getString(0), startLine);
            Line endLine = new Line (interchangeCursor.getString(4), interchangeCursor.getString(5));
            Station stationTo = new Station(interchangeCursor.getString(3), endLine);
            Line journeyLine = new Line (interchangeCursor.getString(6), interchangeCursor.getString(7));
            double time = interchangeCursor.getDouble(8);
            String interchangeType = interchangeCursor.getString(9);
            UnitJourney unitJourney = new UnitJourney(stationFrom, stationTo, journeyLine, time, interchangeType);
            Log.d("Unit Journey Interchange", String.valueOf(unitJourney));
            unitJourneys.add(unitJourney);
        }
//
        interchangeCursor.close();


        return unitJourneys;

    }



    public Station getStation(String name) {
        String query = "SELECT StationName, LineName, LineColour " +
                "FROM Station, Line " +
                "WHERE Line.LineID = Station.StationID " +
                "AND Line.LineID = 'GATE' " +
                "AND StationName = '?';";

        Cursor cursor = database.rawQuery(query, new String[]{name});

        cursor.moveToPosition(0);

        Line line = new Line(cursor.getString(1), cursor.getString(2));
        Station station = new Station(cursor.getColumnName(0), line);

        cursor.close();

        return station;

    }

    private void ExecuteDML(String fileName) { // This private method executes the insert statements
        // in the text file.
        Log.d("Status", "In ExecuteDML Method");

        try { // This try-block reads the file
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));

            StringBuilder fullQuery = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                fullQuery.append(line);

                if (line.trim().endsWith(";")) {
                    String completeQuery = fullQuery.toString();
                    // Log.d("Query", completeQuery);
                    try {
                        database.execSQL(completeQuery);
                    } catch (Exception e) {
                        Log.d("Query with error", completeQuery);
                        Log.e("SQL Error", "Error executing query: " + completeQuery, e);
                    }
                    fullQuery = new StringBuilder();
                }
            }
            reader.close();
            Log.d("Status", "Data inserted from " + fileName);
        } catch (IOException e) {
            Log.e("Error", "Error reading file: " + fileName, e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
