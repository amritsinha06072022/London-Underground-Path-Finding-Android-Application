package com.example.undergroundpathfindingapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.undergroundpathfindingapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity { // This activity is the first activity which
    // runs when the application opens

    @Override
    protected void onCreate(Bundle savedInstanceState) { // This sets the layout to the appropriate
        // XML file
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void GoToShowTubeMapScreen(View view) { // This is clicked when the 'Show Tube Map'
        // button is clicked, as defined in onClick in the XML file
        Intent intent = new Intent(this, DisplayTubeMap.class);
        startActivity(intent);
    }

    public void GoToPlanRoute(View view) { // This is clicked when the 'Plan a Route' button is clicked,
        // as defined in onClick in the XML file
        Intent intent = new Intent(this, PlanMenu.class);
        startActivity(intent);
    }
}