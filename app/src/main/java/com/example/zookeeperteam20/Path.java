package com.example.zookeeperteam20;

public class Path {
    public String source;
    public String target;
    public double distance;
    public String street;

    public Path(String source, String target, double distance, String street) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.street = street;
    }

    public String getSource() { return this.source;}

    public String getTarget() { return this.target;}

    public double getDistance() { return this.distance;}

    public String getStreet() { return this.street;}

    @Override
    public String toString() {
        return "Walk " + this.distance
                + " meters along " +
                this.street + " from " +
                this.source + " to " +
                this.target;
    }





}
