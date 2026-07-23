package com.example.undergroundpathfindingapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PopulateData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_viewer);
    }


    public void AddData(View view) {
        Log.d("status", "inside add Data");
        DBHandler database = new DBHandler(this);




    }
}
