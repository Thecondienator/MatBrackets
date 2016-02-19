package com.example.brice.matbrackets;

/**
 * Created by Brice on 1/26/2016.
 */
public class Tournament {
    private int id;
    private String name;
    private int size;
    private String location_city;
    private String region;
    private String abbreviation;
    private int year;
    private float cost;
    private String image_name;
    private boolean viewable;

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public Tournament(){

    }

    public Tournament(String email, String token){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean isViewable() {
        return viewable;
    }

    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", location_city='" + location_city + '\'' +
                ", region='" + region + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", year=" + year +
                ", cost=" + cost +
                '}';
    }
}
