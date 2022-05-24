package com.example.zookeeperteam20;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitItemDao {
    @Insert
    long insert(ExhibitItem exhibitItem);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertItem(ExhibitItem item);
    @Insert
    List<Long> insertAll(List<ExhibitItem> exhibitList);

    @Query("SELECT * FROM 'exhibit_items' WHERE 'animal'=:animal")
    ExhibitItem get(String animal);

    @Query("SELECT * FROM 'exhibit_items'")
    List<ExhibitItem> getAll();

    @Query("SELECT * FROM 'exhibit_items'")
    LiveData<List<ExhibitItem>> getAllLive();

    @Update
    int update(ExhibitItem exhibitItem);

    @Delete
    int delete(ExhibitItem exhibitItem);
    @Query("DELETE FROM 'exhibit_items'")
    int clearAll();

    @Query("SELECT COUNT() FROM 'exhibit_items'")
    int getDataCount();


}
