package com.example.zookeeperteam20;

public class Animal {
    private String location;
    private int distance;
    Species species;

    public Animal(String location, Species species) {
        this.location = location;
        this.species = species;
    }

    public String getLocation(){
        return this.location;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance(){
        return this.distance;
    }
}
