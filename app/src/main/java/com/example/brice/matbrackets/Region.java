package com.example.brice.matbrackets;

/**
 * Created by Brice on 1/13/2016.
 */
public class Region {

    private int id;
    private String name;
    private String abbreviation;

    public Region(int id, String name, String abbr){
        this.id = id;
        this.name = name;
        this.abbreviation = abbr;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
