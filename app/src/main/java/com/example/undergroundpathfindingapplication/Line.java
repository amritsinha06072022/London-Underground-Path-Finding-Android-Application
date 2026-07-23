package com.example.undergroundpathfindingapplication;

import androidx.annotation.NonNull;

public class Line { // This class defines each Line being used in this application

    // Both of the two attributes of Line below are private
    private String lineName;
    private String lineColour;


    public Line(String lineName, String lineColour) {
        this.lineName = lineName;
        this.lineColour = lineColour;
    }

    @NonNull
    @Override
    public String toString() {
        return "{'" + lineName + "', '" + lineColour + "'}";
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineColour() {
        return lineColour;
    }

    public void setLineColour(String lineColour) {
        this.lineColour = lineColour;
    }
}
