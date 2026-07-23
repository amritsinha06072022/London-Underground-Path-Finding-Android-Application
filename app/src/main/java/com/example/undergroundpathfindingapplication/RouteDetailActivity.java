package com.example.undergroundpathfindingapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.view.Gravity;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteDetailActivity  extends AppCompatActivity {
    private Map.RouteResult routeData;
    private TextView routeTextView;

//    private LinearLayout routeElementsContainer;
//    private TextView routeHeaderText;
//    private TextView totalTimeText;

    DBHandler database;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail);
        routeTextView = findViewById(R.id.routeTextView);
        database = new DBHandler(this);
        String startName = getIntent().getStringExtra("startStationName");
        String endName = getIntent().getStringExtra("endStationName");
        Line gateLine = new Line ("Gate", "Emerald");

        Station start = new Station(startName, gateLine);
        Station end = new Station(endName, gateLine);

        ArrayList<Station> stations = database.getAllStations();
        ArrayList<UnitJourney> unitJourneys = database.getAllUnitJourneys();

        Map graph = new Map (stations, unitJourneys);
        try {
            Map.RouteResult routeData = graph.findRoute(start, end);
            Log.d("Total time", String.valueOf(routeData.getTotalTime()));
            Log.d("routeData in plan", String.valueOf(routeData.getJourneys()));
            String route = getRoute(routeData, start.getStationName(), end.getStationName());

        } catch (Exception e) {
//

            Log.d("Error", e.getMessage());
            routeTextView.setText("Invalid Station Entered");

            return;
        }


        System.out.println("route data in detail: " + start);


    }

    private String getRoute(Map.RouteResult routeData, String start, String end) {
        StringBuilder routeText = new StringBuilder();
        routeText.append("<b>Route between " + start + " and " + end + ":</b><br>");
        boolean flag = false;
        boolean change = true;

        for (UnitJourney journey: routeData.getJourneys()) {

            if (journey.getInterchangeType().equals("Tube")) {
                if (change != flag) {

                    routeText.append("<b><font color='#FF0000'>")
                            .append(journey.getLine().getLineName())
                            .append("</font></b><br>");
                    routeText.append(journey.getStartStation().formatStation()).append("<br>");
                }
                change = false;

                Log.d("Journey type", journey.getInterchangeType());
            } else if (journey.getInterchangeType().equals("Foot") && journey.getChangeTimeMessage() != null) {
                change = true;
                routeText.append(journey.getStartStation().formatStation()).append("<br>");
                // Change time message in bold
                routeText.append("\n<b>")
                        .append(journey.getChangeTimeMessage())
                        .append("</b><br><br>");
            }
        }

        int last = routeData.getJourneys().size();

        UnitJourney journey = routeData.getJourneys().get(last - 1);

        Station endStation = journey.getEndStation();

        routeText.append(endStation.formatStation()).append("<br>");
        
        routeText.append("<br>").append("<b>Total time: " + Math.round(routeData.getTotalTime()) + " minutes</b>");

        Log.d("Route text new", String.valueOf(routeText));

        Spanned formattedText = Html.fromHtml(routeText.toString(), Html.FROM_HTML_MODE_COMPACT);
        routeTextView.setText(formattedText);



        return routeText.toString();
    }


}
