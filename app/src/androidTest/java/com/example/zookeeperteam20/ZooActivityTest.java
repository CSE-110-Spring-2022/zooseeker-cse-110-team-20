package com.example.zookeeperteam20;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ZooActivityTest {
    /*
    ExhibitItemDatabase testdb;
    ExhibitItemDao exhibitItemDao;

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testdb = Room.inMemoryDatabaseBuilder(context, ExhibitItemDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitItemDatabase.injectTestDatabase(testdb);

        List<ExhibitItem> exhibits = ExhibitItem.loadJSON(context, "sample_node_info.json");
        exhibitItemDao = testdb.exhibitItemDao();
        exhibitItemDao.insertAll(exhibits);
    }

    */
    @Test
    public void testCountSearch(){
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("ele", false);
            assertEquals(1, list.getCount());
        });
    }
    @Test
    public void testCorrectSearch(){
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("ele", false);
            ExhibitItem item = (ExhibitItem) list.getItemAtPosition(0);
            assertEquals("Elephant Odyssey", item.getExhibitName());
        });
    }
    @Test
    public void testCounter(){
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            TextView counter = activity.findViewById(R.id.number);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("ele", false);
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);

            assertEquals("1", counter.getText());
        });
    }
    @Test
    public void addItem(){
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);


        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("ele", false);
            ExhibitItem item = (ExhibitItem) list.getItemAtPosition(0);
            ArrayList<ExhibitItem> selected = activity.selected;
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);

            assertEquals(selected.get(0), item);
        });

    }
}
