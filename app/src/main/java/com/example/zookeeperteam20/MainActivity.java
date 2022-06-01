package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Class<?> activityClass;

        try {
            SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
            activityClass = Class.forName(
                    prefs.getString("lastActivity", Zoo_activity.class.getName()));
        } catch(ClassNotFoundException ex) {
            activityClass = Zoo_activity.class;
        }

        startActivity(new Intent(this, activityClass));
        //Intent intent = new Intent(this, Zoo_activity.class);
        //startActivity(intent);
    }

    public void onStartClicked(View view){
        Intent intent = new Intent(this, Zoo_activity.class);
        startActivity(intent);
    }





}
