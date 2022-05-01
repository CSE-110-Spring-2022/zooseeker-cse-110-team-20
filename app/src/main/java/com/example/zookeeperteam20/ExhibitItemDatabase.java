/*package com.example.zookeeperteam20;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {ExhibitItem.class}, version = 1)
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
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<ExhibitItem> exhibits = ExhibitItem
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).exhibitItemDao().insertAll(exhibits);
                        });
                    }
                })
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
*/