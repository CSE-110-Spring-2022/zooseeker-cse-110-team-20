package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        Intent intent = new Intent(this, Zoo_activity.class);
        startActivity(intent);
    }

    public void onStartClicked(View view){
        Intent intent = new Intent(this, Zoo_activity.class);
        startActivity(intent);
    }





}
