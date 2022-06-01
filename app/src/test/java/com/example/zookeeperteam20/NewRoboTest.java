package com.example.zookeeperteam20;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class NewRoboTest {

    @Before
    public void resetDatabase() {
        ExhibitItemDatabase testdb;
        ExhibitItemDao exhibitItemDao;

        Context context = ApplicationProvider.getApplicationContext();
        testdb = Room.inMemoryDatabaseBuilder(context, ExhibitItemDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitItemDatabase.injectTestDatabase(testdb);

        exhibitItemDao = testdb.exhibitItemDao();
        exhibitItemDao.clearAll();
    }

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
            search.setQuery("ca", false);
            ExhibitItem item1 = (ExhibitItem) list.getItemAtPosition(0);
            ExhibitItem item2 = (ExhibitItem) list.getItemAtPosition(1);
            ExhibitItem item3 = (ExhibitItem) list.getItemAtPosition(2);
            ExhibitItem item4 = (ExhibitItem) list.getItemAtPosition(3);
            assertEquals("Blue Capped Motmot", item1.getExhibitName());
            assertEquals("Capuchin Monkeys", item2.getExhibitName());
            assertEquals("Fern Canyon", item3.getExhibitName());
            assertEquals("Toucan", item4.getExhibitName());
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
            search.setQuery("a", false);
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
            search.setQuery("a", false);
            ExhibitItem item = (ExhibitItem) list.getItemAtPosition(0);
            ArrayList<ExhibitItem> selected = activity.selected;
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);

            assertEquals(selected.get(0), item);
        });
    }

    @Test
    public void testAlgorithm(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<String> answer = new ArrayList<>();
        ArrayList<ExhibitItem> selection = new ArrayList<>();


        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        answer.add("Parker Aviary");
        answer.add("Fern Canyon");
        answer.add("Capuchin Monkeys");

        selection.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        selection.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        selection.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

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
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("gor", false);
            ExhibitItem item = (ExhibitItem) list.getItemAtPosition(0);
            ArrayList<ExhibitItem> selected = activity.selected;
            Button plan = activity.findViewById(R.id.button);
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);
            search.setQuery("a", false);
            list.performItemClick(
                    list.getAdapter().getView(0,null,null),1,0);
            list.performItemClick(
                    list.getAdapter().getView(0,null,null),1,0);
            plan.performClick();
            assertEquals(activity.noRepeats.size(), 2);
        });
    }

    @Test
    public void testDirections(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        Intent planIntent = new Intent(ApplicationProvider.getApplicationContext(),  Plan_activity.class);
        planIntent.putExtra("plan", ordered);
        ActivityScenario<Plan_activity> planScenario
                = ActivityScenario.launch(planIntent);
        planScenario.moveToState(Lifecycle.State.CREATED);
        planScenario.moveToState(Lifecycle.State.STARTED);
        planScenario.moveToState(Lifecycle.State.RESUMED);
        final double[] distance = new double[1];
        planScenario.onActivity(activity -> {
            distance[0] = 0.0;
            distance[0] = activity.dists.get(0);
        });
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        final double[] totDistance= new double[1];
        totDistance[0] = 0.0;
        scenario.onActivity(activity -> {
            for(Path p : activity.pathsBetweenExhibits){
                totDistance[0] = totDistance[0] + p.getDistance();
            }
        });
        assertEquals(distance[0], totDistance[0], .0001);
    }

    @Test
    public void testFirstExhibit(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            assertEquals("Parker Aviary", activity.pathsBetweenExhibits.get(activity.pathsBetweenExhibits.size()-1).target);
        });
    }

    @Test
    public void nextItem(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.Next);
            for(int i = 1; i < ordered.size()-1; i++){
                next.performClick();
                assertEquals(ordered.get(i).getExhibitName(), activity.nextPath.get(activity.nextPath.size()-1).target);
            }
        });
    }

    @Test
    public void testLastItemisExit(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.Next);
            for(int i = 0; i < ordered.size(); i++){
                next.performClick();
            }
            //next.performClick();
            assertEquals(0, activity.nextPath.size());
        });
    }

    @Test
    public void testCounterforRepeats() {
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            TextView counter = activity.findViewById(R.id.number);
            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("gor", false);
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);
            assertEquals("1", counter.getText());
        });
    }

    @Test
    public void previousItem(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            Button next = activity.findViewById(R.id.Next);
            for(int i = 1; i < ordered.size(); i++){
                next.performClick();
            }
            Button previous = activity.findViewById(R.id.Previous);
            for(int i = ordered.size()-1; i > 0; i--){
                previous.performClick();
                assertEquals(ordered.get(i-1).getExhibitName(), activity.prevPath.get(activity.prevPath.size()-1).target);
            }
        });
    }

    @Test
    public void testSkip(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            Button skip = activity.findViewById(R.id.Skip);
            skip.performClick();
            assertEquals(activity.ordered.get(1).getExhibitName(), "Capuchin Monkeys");
        });
    }

    @Test
    public void testSettings(){
        List<String> stringsPA = new ArrayList<String>();
        List<String> stringsCM = new ArrayList<String>();
        List<String> stringsFC = new ArrayList<String>();

        ArrayList<ExhibitItem> ordered = new ArrayList<>();

        stringsFC.add("plants");
        stringsFC.add("ferns");
        stringsFC.add("green");

        stringsCM.add("tufted");
        stringsCM.add("capuchin");
        stringsCM.add("monkey");
        stringsCM.add("mammal");
        stringsCM.add("primate");
        stringsCM.add("south");
        stringsCM.add("america");

        Tags tagsPA = new Tags(stringsPA);
        Tags tagsCM = new Tags(stringsCM);
        Tags tagsFC = new Tags(stringsFC);

        ordered.add(new ExhibitItem("parker_aviary", "Parker Aviary", ZooData.VertexInfo.Kind.EXHIBITGROUP, tagsPA));
        ordered.add(new ExhibitItem("fern_canyon", "Fern Canyon", ZooData.VertexInfo.Kind.EXHIBIT, tagsFC));
        ordered.add(new ExhibitItem("capuchin", "Capuchin Monkeys", ZooData.VertexInfo.Kind.EXHIBIT, tagsCM));

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),  DirectionsActivity.class);
        intent.putExtra("Directions", ordered);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            Button settings = activity.findViewById(R.id.settings);
            String previousSource = activity.pathsBetweenExhibits.get(0).getSource();
            String previousTarget = activity.pathsBetweenExhibits.get(activity.pathsBetweenExhibits.size()-1).getTarget();
            int previousSize = activity.pathsBetweenExhibits.size();
            settings.performClick();
            assertNotEquals(previousSize, activity.currPath.size());
            assertEquals(previousSource, activity.currPath.get(0).getSource());
            assertEquals(previousTarget,activity.currPath.get(activity.currPath.size()-1).getTarget());
        });
    }

    /*@Test
    public void testDisplaySelected() {
        ActivityScenario<Zoo_activity> scenario
                = ActivityScenario.launch(Zoo_activity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SearchView search = activity.findViewById(R.id.search);
            setContentView
            RecyclerView displayed = activity.findViewById(R.id.exhibit_items);

            ListView list = activity.findViewById(R.id.listview_Exhibits);
            search.setQuery("a", false);
            list.performItemClick(
                    list.getAdapter().getView(0, null, null), 0, 0);

            assertEquals("Bali Mynah", displayed.getChildAt(0));

        });
    }

    @Test
    public void retainPlan(){

    }*/


}
