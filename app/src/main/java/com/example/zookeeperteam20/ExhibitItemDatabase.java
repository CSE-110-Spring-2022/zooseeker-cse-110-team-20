package com.example.zookeeperteam20;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Database(entities = {ExhibitItem.class}, version = 1)
@TypeConverters({TagsConverter.class})
public abstract class ExhibitItemDatabase extends RoomDatabase {
    private static ExhibitItemDatabase singleton = null;

    public abstract ExhibitItemDao exhibitItemDao();

    public synchronized static ExhibitItemDatabase getSingleton(Context context){
        if(singleton == null){
            singleton = ExhibitItemDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ExhibitItemDatabase makeDatabase(Context context){
        return Room.databaseBuilder(context, ExhibitItemDatabase.class, "exhibit_app.db")
                .allowMainThreadQueries()
                //.addCallback(new Callback() {
                    /*
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<ExhibitItem> exhibits = new ArrayList<ExhibitItem>();
                            Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
                            ExhibitItem e0;
                            for (ZooData.VertexInfo node : vInfo.values()) {
                                if (node.kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                                    e0 = new ExhibitItem(node.id,node.name,node.kind, new Tags(node.tags));
                                    exhibits.add(e0);
                                }
                            }
                            getSingleton(context).exhibitItemDao().insertAll(exhibits);
                        });
                     */

                //)
            .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(ExhibitItemDatabase testDatabase){
        if(singleton != null){
            singleton.close();
        }
        singleton = testDatabase;
    }
}
