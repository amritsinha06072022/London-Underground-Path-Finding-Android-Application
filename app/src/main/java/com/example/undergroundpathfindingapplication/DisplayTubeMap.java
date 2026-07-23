package com.example.undergroundpathfindingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayTubeMap extends AppCompatActivity { // This activity allows the Tube Map to be
    // displayed.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_map);
    }


    public void GoToHome(View view) { // This runs when the 'Go To Home' button is pressed,
        // as defined in the onCLick of the XML file
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void GoToPlanRoute(View view) { // This runs when the 'Plan a Route' button is pressed,
        // as defined in the onClick of the XML file
        Intent intent = new Intent(this, PlanMenu.class);
        startActivity(intent);
    }
}
