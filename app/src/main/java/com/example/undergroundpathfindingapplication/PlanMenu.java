package com.example.undergroundpathfindingapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanMenu extends AppCompatActivity { // This activity provides the menu option to find
    // the fastest route between two stations

    AutoCompleteTextView startStation;
    AutoCompleteTextView destinationStation;
    ArrayAdapter<String> stationData;

    String startStationString;
    String destinationStationString;

    String[] arrayOfStations;

    DBHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_route);

        database = new DBHandler(this);

        arrayOfStations = database.getAllGATEStations().toArray(new String[0]);


        startStation = findViewById(R.id.start_station);
        destinationStation = findViewById(R.id.destination_station);

        stationData = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayOfStations);

        startStation.setAdapter(stationData);
        destinationStation.setAdapter(stationData);

        // Lines 53-61 allow predictive search to be used when searching for stations, satisfying Objective 3.2

        startStation.setOnDismissListener(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        });

        destinationStation.setOnDismissListener(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        });


    }

    public void PlanRoute(View view) {
        startStationString = startStation.getText().toString();
        destinationStationString = destinationStation.getText().toString();



        try {
//            System.out.println("inside try before find route");
//            Map.RouteResult routeData = graph.findRoute(start, end);
//            Log.d("routeData in plan", String.valueOf(routeData.getJourneys()));


            Intent intent = new Intent(this, RouteDetailActivity.class);
            intent.putExtra("startStationName", startStationString);
            intent.putExtra("endStationName", destinationStationString);
//            intent.putExtra("journeys", (Serializable) routeData.getJourneys());
            startActivity(intent);
        } catch (Exception e) {
            Log.d("Error in PlanMenu", e.getMessage());
        }

    }

}

