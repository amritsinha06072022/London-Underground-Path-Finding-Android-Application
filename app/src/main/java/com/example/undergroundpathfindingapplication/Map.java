package com.example.undergroundpathfindingapplication;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.HashMap;

import java.io.Serializable;

public class Map {

    private ArrayList<Station> stations;
    private ArrayList<UnitJourney> unitJourneys;
    private double[][] adjacencyMatrix;




    public Map(ArrayList<Station> stations, ArrayList<UnitJourney> unitJourneys) {
        this.stations = stations;

        this.unitJourneys = unitJourneys;

        this.adjacencyMatrix = new double[stations.size()][stations.size()];
        for (int i = 0; i < stations.size(); i++) {
            for (int j = 0; j < stations.size(); j++) {
                adjacencyMatrix[i][j] = Double.POSITIVE_INFINITY;
            }
        }



        Log.d("List of Stations", String.valueOf(stations));

        populateAdjacencyMatrix();

        Log.d("Adjacency Matrix", Arrays.deepToString(adjacencyMatrix));

    }
    private void populateAdjacencyMatrix() {
        Log.d("Total stations: ", String.valueOf(stations.size()));
        Log.d("Total unit journeys: " , String.valueOf(unitJourneys.size()));

        for (UnitJourney journey : unitJourneys) {
            Station startStation = journey.getStartStation();
            Station endStation = journey.getEndStation();


            Log.d("Journey:", "");
            Log.d("Start Station: ", String.valueOf(startStation));
            Log.d("End Station: ", String.valueOf(endStation) + "\n");

            int startIndex = stations.indexOf(startStation);
            int endIndex = stations.indexOf(endStation);

            System.out.println("Start Index: " + startIndex);
            System.out.println("End Index: " + endIndex);



            if (startIndex != -1 && endIndex != -1) {
                System.out.println("startIndex " + startIndex + ", endIndex" + endIndex + "Time: " + journey.getTime());
                adjacencyMatrix[startIndex][endIndex] = journey.getTime();
                adjacencyMatrix[endIndex][startIndex] = journey.getTime();
            }
        }
    }

    private void printAdjacencyMatrix() {
        System.out.println("Adjacency Matrix:");
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                System.out.print(adjacencyMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }


private void addUnitJourney(UnitJourney unitJourney) {
    // Null checks
    if (unitJourney == null || unitJourney.getStartStation() == null || unitJourney.getEndStation() == null) {
        return; // Skip invalid journeys
    }

    // Find indices with null-safe method
    int startIndex = findStationIndex(unitJourney.getStartStation());
    int endIndex = findStationIndex(unitJourney.getEndStation());

    // Only add if both indices are valid
    if (startIndex != -1 && endIndex != -1) {
        adjacencyMatrix[startIndex][endIndex] = unitJourney.getTime();
    }
}

    // Null-safe method to find station index
    private int findStationIndex(Station station) {
        if (station == null || stations == null) {
            return -1;
        }

        for (int i = 0; i < stations.size(); i++) {
            // Use equals method for comparison
            if (station.equals(stations.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean edgeExists(int startIndex, int endIndex) {
        return adjacencyMatrix[startIndex][endIndex] != 0;
    }

    private UnitJourney getUnitJourney(int startIndex, int endIndex) throws Exception{

        if (adjacencyMatrix[startIndex][endIndex] != 0) {
            Station start = unitJourneys.get(startIndex).getStartStation();
            Station end = unitJourneys.get(endIndex).getEndStation();
            Log.d("END1 = END2?", String.valueOf(end.equals(unitJourneys.get(startIndex).getEndStation())));
            Line line = unitJourneys.get(startIndex).getLine();
            double time = unitJourneys.get(startIndex).getTime();
            String interchangeType = unitJourneys.get(startIndex).getInterchangeType();
            return new UnitJourney(start, end, line, time, interchangeType);
        } else {
            throw new Exception("No unitJourney at " + startIndex + ", " + endIndex);
        }

    }

    public RouteResult findRoute (Station start, Station end) throws Exception { // Implements Dijkstra's algorithm
        boolean change = false;

        if (! stations.contains(start) || ! stations.contains(end)) {
//            Toast.makeText(context, "Station does not exist", Toast.LENGTH_SHORT).show();
            Log.e("Map", "About to throw exception - Station does not exist");
            throw new Exception("Station does not exist");

        }
        Log.e("Map", "About to throw exception - Station does not exist One");
        int startIndex = stations.indexOf(start);
        int endIndex = stations.indexOf(end);

        if (startIndex == -1 || endIndex == -1) {
            Log.e("Map", "About to throw exception - Station does not exist - three");
            throw new Exception("Station does not exist");
        }
        Log.e("Map", "About to throw exception - Station does not exist two");

        int numStations = stations.size();

        double[] distances = new double[numStations];
        boolean[] visited = new boolean[numStations];
        Station[] previousStations = new Station[numStations];

        // Initialize distances
        for (int i = 0; i < numStations; i++) {
            distances[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
            previousStations[i] = null;
        }
        distances[startIndex] = 0;

        // Create priority queue for getting minimum distance
        PriorityQueue<StationDistance> pq = new PriorityQueue<>();
        pq.offer(new StationDistance(startIndex, 0));

        while (!pq.isEmpty()) {
            StationDistance current = pq.poll();
            int currentIndex = current.stationIndex;

            if (visited[currentIndex]) continue;
            visited[currentIndex] = true;

            if (currentIndex == endIndex) break; // Found the destination

            // Check all neighboring stations
            for (int i = 0; i < numStations; i++) {
                if (!visited[i] && adjacencyMatrix[currentIndex][i] != Double.POSITIVE_INFINITY) {
                    double newDist = distances[currentIndex] + adjacencyMatrix[currentIndex][i];
                    if (newDist < distances[i]) {
                        distances[i] = newDist;
                        previousStations[i] = stations.get(currentIndex);
                        pq.offer(new StationDistance(i, newDist));
                    }
//                    if (previousStations[i].getLine() != previousStations[i-1].getLine() && i != 0) {
//                        Line line = previousStations[i].getLine();
//                        Log.d("Time on " + String.valueOf(line), String.valueOf(newDist));
//                    } else {
//                        Log.d("Time not recorded for ", previousStations[i] + " " + previousStations[i-1]);
//                    }
                }
            }
        }

        // Construct the route
        ArrayList<Station> route = new ArrayList<>();
        ArrayList<UnitJourney> journeys = new ArrayList<>();
        ArrayList<String> changeTimeMessages = new ArrayList<>();
        double totalTime = distances[endIndex];

        if (totalTime == Double.POSITIVE_INFINITY) {
            Log.e("Route Finding", "No route found between " + start.getStationName() + " and " + end.getStationName());
            throw  new Exception("No Route found");
        }

        Station current = end;
        while (current != null) {
            route.add(current);
            current = previousStations[stations.indexOf(current)];
        }
        Collections.reverse(route);

        // Find the corresponding UnitJourneys
        for (int i = 0; i < route.size() - 1; i++) {
            Station currentStation = route.get(i);
            Station nextStation = route.get(i + 1);

            // Find the UnitJourney between these stations
            for (UnitJourney journey : unitJourneys) {
                if (journey.getStartStation().equals(currentStation) &&
                        journey.getEndStation().equals(nextStation)) {

                    Log.d("Journey", String.valueOf(journey));


                    if ((journey.getInterchangeType().equals("Foot")) &&
                            (!journey.getStartStation().getLine().getLineName().equalsIgnoreCase("Gate"))) {
                        change = true;
                        String message = "Change Time from " +
                                journey.getStartStation().formatStation() + " To " + journey.getEndStation().formatStation() + ": " + Math.round(journey.getTime()) + " minutes";
                        Log.d("Change over", message);
                        journey.setChangeTimeMessage(message);
                    }
                    journeys.add(journey);

                    break;
                }
            }
        }
        for (UnitJourney journey : journeys) {
            String lName  = journey.getLine().toString();
            System.out.println("Line name in Result" + lName);
            System.out.println("Line time" + journey.getTime());


        }
        Log.d("Final Journey", String.valueOf(journeys));
        Log.d("Final Size for route", String.valueOf(route.size()));
        Log.d("Final Size for journeys", String.valueOf(journeys.size()));
        RouteResult routeResult = new RouteResult(route, journeys, totalTime, changeTimeMessages);
        Log.d("Final Route Result", String.valueOf(routeResult));
        return routeResult;



    }

    public static class RouteResult {
        private final ArrayList<Station> stations;
        private final ArrayList<UnitJourney> journeys;
        private final double totalTime;
        private final ArrayList<String> changeTimeMessages;

        public RouteResult(ArrayList<Station> stations, ArrayList<UnitJourney> journeys, double totalTime, ArrayList<String> changeTimeMessages) {
            this.stations = stations;
            this.journeys = journeys;
            this.totalTime = totalTime;
            this.changeTimeMessages = changeTimeMessages;
        }

        @Override
        public String toString() {
            StringBuilder route = new StringBuilder();
            for (Station station: stations) {
                String formattedStation = station.formatStation();
                route.append(formattedStation).append("");
            }

            if (!changeTimeMessages.isEmpty()) {
                route.append("Change Times:");
                for (String message : changeTimeMessages) {
                    route.append(message).append("");
                }
            }
            // route.append(Arrays.toString(journeys.toArray()));
            route.append("Time taken: ").append(totalTime);
            return route.toString();
        }


        public ArrayList<String> getChangeTimeMessages() {
            return changeTimeMessages;
        }

        public ArrayList<Station> getStations() {
            return stations;
        }

        public ArrayList<UnitJourney> getJourneys() {
            return journeys;
        }

        public double getTotalTime() {
            return totalTime;
        }
    }

    private static class StationDistance implements Comparable<StationDistance> {
        int stationIndex;
        double distance;

        StationDistance(int stationIndex, double distance) {
            this.stationIndex = stationIndex;
            this.distance = distance;
        }
        @Override
        public int compareTo(StationDistance other) {
            return Double.compare(this.distance, other.distance);
        }



    }



}



