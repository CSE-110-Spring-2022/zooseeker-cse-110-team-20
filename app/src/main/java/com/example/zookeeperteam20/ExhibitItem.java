package com.example.zookeeperteam20;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ExhibitItem {
    private long id = 0;
    private String name;
    private ZooData.VertexInfo.Kind kind;
    private List<String> tags;

    public ExhibitItem(String name, ZooData.VertexInfo.Kind kind, List<String> tags){
        this.name = name;
        this.kind = kind;
        this.tags = tags;
    }

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

    public String getExhibitName() {
        return this.name;
    }

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
