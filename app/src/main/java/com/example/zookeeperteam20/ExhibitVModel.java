package com.example.zookeeperteam20;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitVModel extends AndroidViewModel {
    private LiveData<List<ExhibitItem>> exhibitItems;
    private final ExhibitItemDao exhibitItemDao;
    private List<Long> ids;

    public ExhibitVModel(@NonNull Application application){
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitItemDatabase db = ExhibitItemDatabase.getSingleton(context);
        exhibitItemDao = db.exhibitItemDao();
    }

    public LiveData<List<ExhibitItem>> getExhibitItems(){
        if(exhibitItems == null){
            loadUsers();
        }
        return exhibitItems;
    }

    private void loadUsers(){
        exhibitItems = exhibitItemDao.getAllLive();
    }

    public void addExhibitItem(ExhibitItem item){
        exhibitItemDao.insertItem(item);
    }
    public void clearAll(){
        exhibitItemDao.clearAll();
    }

}
