package com.example.zookeeperteam20;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

import android.app.Instrumentation;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.checkerframework.checker.units.qual.A;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooActivityRoboTest {

    @Test
    public void testCorrectMultipleSearch(){
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("a", false);
            ExhibitItem item1 = (ExhibitItem) list.getItemAtPosition(0);
            ExhibitItem item2 = (ExhibitItem) list.getItemAtPosition(1);
            ExhibitItem item3 = (ExhibitItem) list.getItemAtPosition(2);
            ExhibitItem item4 = (ExhibitItem) list.getItemAtPosition(3);
            assertEquals("Elephant Odyssey", item1.getExhibitName());
            assertEquals("Alligators", item2.getExhibitName());
            assertEquals("Arctic Foxes", item3.getExhibitName());
            assertEquals("Gorillas", item4.getExhibitName());

            assertEquals(4, list.getAdapter().getCount());
        });
    }

    @Test
    public void testInvalidSearch(){
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("oof", false);

            assertEquals(0, list.getAdapter().getCount());
        });
    }
    @Test
    public void testCounter() {
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
    public void addItem() {
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

    @Test
    public void testAlgorithm(){
        List<String> tagsGator = new ArrayList<String>();
        List<String> tagsEle = new ArrayList<String>();
        List<String> tagsFox = new ArrayList<String>();
        ArrayList<String> answer = new ArrayList<>();
        ArrayList<ExhibitItem> selection = new ArrayList<>();
        tagsGator.add("alligator");
        tagsGator.add("reptile");
        tagsGator.add("gator");
        tagsEle.add("elephant");
        tagsEle.add("mammal");
        tagsEle.add("africa");
        tagsFox.add("arctic");
        tagsFox.add("fox");
        tagsFox.add("mammal");
        answer.add("Alligators");
        answer.add("Elephant Odyssey");
        answer.add("Arctic Foxes");
        selection.add(new ExhibitItem("elephant_odyssey", "Elephant Odyssey", ZooData.VertexInfo.Kind.EXHIBIT, tagsEle));
        selection.add(new ExhibitItem("gators", "Alligators", ZooData.VertexInfo.Kind.EXHIBIT, tagsGator));
        selection.add(new ExhibitItem("arctic_foxes", "Arctic Foxes", ZooData.VertexInfo.Kind.EXHIBIT, tagsFox));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), Plan_activity.class);
        intent.putExtra("plan", selection);
        ActivityScenario<Plan_activity> scenario = ActivityScenario.launch(intent);
        scenario.onActivity(activity -> {
            for(int i = 0; i < activity.ordered.size(); i++){
                assertEquals(activity.ordered.get(i).getExhibitName(), answer.get(i));
            }
        });
        scenario.close();
    }
    @Test
    public void testNoRepeats(){
        List<String> tagsGator = new ArrayList<String>();
        List<String> tagsEle = new ArrayList<String>();
        List<String> tagsFox = new ArrayList<String>();
        ArrayList<String> answer = new ArrayList<>();
        ArrayList<ExhibitItem> selection = new ArrayList<>();
        tagsGator.add("alligator");
        tagsGator.add("reptile");
        tagsGator.add("gator");
        tagsEle.add("elephant");
        tagsEle.add("mammal");
        tagsEle.add("africa");
        tagsFox.add("arctic");
        tagsFox.add("fox");
        tagsFox.add("mammal");
        answer.add("Alligators");
        answer.add("Elephant Odyssey");
        answer.add("Arctic Foxes");
        selection.add(new ExhibitItem("elephant_odyssey", "Elephant Odyssey", ZooData.VertexInfo.Kind.EXHIBIT, tagsEle));
        selection.add(new ExhibitItem("gators", "Alligators", ZooData.VertexInfo.Kind.EXHIBIT, tagsGator));
        selection.add(new ExhibitItem("arctic_foxes", "Arctic Foxes", ZooData.VertexInfo.Kind.EXHIBIT, tagsFox));
        selection.add(new ExhibitItem("gators", "Alligators", ZooData.VertexInfo.Kind.EXHIBIT, tagsGator));
        selection.add(new ExhibitItem("arctic_foxes", "Arctic Foxes", ZooData.VertexInfo.Kind.EXHIBIT, tagsFox));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), Plan_activity.class);
        intent.putExtra("plan", selection);
        ActivityScenario<Plan_activity> scenario = ActivityScenario.launch(intent);
        scenario.onActivity(activity -> {
            for(int i = 0; i < activity.ordered.size(); i++){
                assertEquals(activity.ordered.get(i).getExhibitName(), answer.get(i));
            }
        });
        scenario.close();
    }
}
