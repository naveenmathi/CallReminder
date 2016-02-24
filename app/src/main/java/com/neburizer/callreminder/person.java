package com.neburizer.callreminder;

import java.util.ArrayList;

/**
 * Created by nm3 on 12/23/2015.
 */
public class person {
    String number = "";
    String name = "";
    person previous = null;

    public person getPrevious() {
        return previous;
    }
    public void setPrevious(person previous) {
        this.previous = previous;
    }

    //constructor
    public person(String number)
    {
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //array list consisting of all called timings for this person
    ArrayList<Long> calledList = new ArrayList<Long>();
    public ArrayList<Long> getCalledList() {
        return calledList;
    }
    public void addCall(Long timeOfCall) {
        calledList.add(timeOfCall);
    }


}
