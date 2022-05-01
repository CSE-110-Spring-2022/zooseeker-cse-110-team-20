/*package com.example.zookeeperteam20;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitItemDao {
    @Insert
    long insert(ExhibitItem exhibitItem);
    @Insert
    List<Long> insertAll(List<ExhibitItem> exhibitList);

    @Query("SELECT * FROM 'exhibit_items' WHERE 'id'=:id")
    ExhibitItem get(long id);

    @Query("SELECT * FROM 'exhibit_items'")
    List<ExhibitItem> getAll();

    @Update
    int update(ExhibitItem exhibitItem);
}
*/