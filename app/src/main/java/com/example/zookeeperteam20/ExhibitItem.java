package com.example.zookeeperteam20;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/* This class represents the exhibits that we load from
    a JSON file and constructs an ExhibitItem from the data
 */
@Entity(tableName = "exhibit_items", indices = @Index(value = {"animal"}, unique = true))
public class ExhibitItem implements Serializable {
    //Initializes variables of ExhibitItem
    @PrimaryKey(autoGenerate = true)
    public long value;

    @NonNull

    @ColumnInfo(name = "animal")
    private String id;
    private String parentId = "NULLNULLNULL";
    private String parentName = "NULLNAME";
    private String name;
    private ZooData.VertexInfo.Kind kind;
    private Tags tags;


    //Constructor for ExhibitItem
    public ExhibitItem(@NonNull String id,String name, ZooData.VertexInfo.Kind kind, Tags tags){
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.tags = tags;
    }

    //Method to construct a list of ExhibitItems from a JSON file
    public static List<ExhibitItem> loadJSON(Context context, String path) {
        try{
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ExhibitItem>>(){}.getType();
            return gson.fromJson(reader, type);
        }catch(IOException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public String getId() {return this.id;}

    public String getParentId() {
        return this.parentId;
    }

    public String getParentName() {
        return this.parentName;
    }

    public String getExhibitName() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
    public ZooData.VertexInfo.Kind getKind(){
        return this.kind;
    }

    public Tags getTags(){
        return this.tags;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setParentId(@NonNull String parentId) {
        this.parentId = parentId;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
    public void setKind(ZooData.VertexInfo.Kind kind) {
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTags(Tags tags){
        this.tags = tags;
    }

    //Converts Exhibititem into a string
    @Override
    public String toString() {
        return "ExhibitItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + kind + '\'' +
                ", tags=" + tags +
                '}';
    }

}
