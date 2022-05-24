package com.example.zookeeperteam20;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class TagsConverter {
    @TypeConverter
    public Tags stringToTags(String s){
        List<String> tags = Arrays.asList(s.split("\\s*,\\s*"));
        return new Tags(tags);
    }
    @TypeConverter
    public String tagsToString(Tags t){
        String val = "";

        for(String s : t.getTags())
            val += s + ",";
        return val;
    }
}
