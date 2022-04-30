package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //Variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] animalNameList;
    ArrayList<AnimalNames> arraylist = new ArrayList<AnimalNames>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Generate sample data

        animalNameList = new String[]{"Lion","Tiger","Dog","Cat","Tortoise","Monkey"};

        //Locate ListView in listview_main.xml
        list = (ListView) findViewById(R.id.listview);

        for(int i = 0; i < animalNameList.length;i++) {
            AnimalNames animalNames = new AnimalNames(animalNameList[i]);
            //Binds strings into array
            arraylist.add(animalNames);
        }
        //SearchView searchView = (SearchView) findViewById(R.id.SearchBar);
        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter(this, arraylist);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

}
