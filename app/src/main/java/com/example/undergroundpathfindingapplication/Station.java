package com.example.undergroundpathfindingapplication;

import androidx.annotation.NonNull;

import java.util.Objects;


// This class defines the object Station with attributes stationName and line
public class Station {

    private String stationName;
    private Line line;

    public Station(String stationName, Line line) {
        this.stationName = stationName;
        this.line = line;
    }

    @NonNull
    @Override
    public String toString() {
        return "{'" + stationName + "', '" + line.getLineName() + "'}";
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }



    // This override method allows one to check if two Stations are equal.
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Station station = (Station) object;
        return Objects.equals(stationName, station.stationName) &&
                Objects.equals(line.getLineName(), station.line.getLineName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName, line.getLineName());
    }


    // The method below formats a Station into a StationName (LineName) format, making it easier for debugging.
    public String formatStation() {
        String formattedStation = "";
        String name = getStationName();
        String lineName = getLine().getLineName();
        formattedStation += name + " (" + lineName + ")";
        return formattedStation;
    }


}
