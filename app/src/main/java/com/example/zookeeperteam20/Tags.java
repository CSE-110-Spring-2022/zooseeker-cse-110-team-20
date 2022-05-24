package com.example.zookeeperteam20;

import java.io.Serializable;
import java.util.List;

public class Tags implements Serializable {
    private List<String> tags;
    public Tags(List<String> tags){
        this.tags = tags;
    }
    public List<String> getTags(){
        return tags;
    }
    public void setTags(List<String> tags){
        this.tags = tags;
    }
}
