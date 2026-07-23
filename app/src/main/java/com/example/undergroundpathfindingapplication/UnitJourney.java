package com.example.undergroundpathfindingapplication;

import androidx.annotation.NonNull;

public class UnitJourney { // This class defines the UnitJourney object

    private Station startStation;
    private Station endStation;
    private Line line;
    private double time;
    private String interchangeType;

    private String changeTimeMessage;

    public UnitJourney(Station startStation, Station endStation, Line line, double time, String interchangeType) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.line = line;
        this.time = time;
        this.interchangeType = interchangeType;
    }

    @NonNull
    @Override
    public String toString() {
        return "{'" + startStation.getStationName() + "', '" + endStation.getStationName() + "', '"
                + line.getLineName() + "', '" + time + "', '" + interchangeType + "', '" + changeTimeMessage + "'}";
    }

    public String getChangeTimeMessage() {
        return changeTimeMessage;
    }
    public void setChangeTimeMessage(String changeTimeMessage) {
        this.changeTimeMessage = changeTimeMessage;
    }

    public Station getStartStation() {
        return startStation;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void setEndStation(Station endStation) {
        this.endStation = endStation;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getInterchangeType() {
        return interchangeType;
    }

    public void setInterchangeType(String interchangeType) {
        this.interchangeType = interchangeType;
    }

}
