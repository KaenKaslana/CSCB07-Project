package com.example.b07demosummer2024;

import java.io.Serializable;

public class Reminder implements Serializable {
    public String frequency;
    public String time;
    public String day;
    public int date;
    //model class used to pass values that firebase can use
    public Reminder(){}//required for firebase to work
    public Reminder(String frequency, String time){
        this.frequency = frequency;
        this.time = time;
    }
    public Reminder(String frequency, String time, String day ){
        this.frequency = frequency;
        this.time = time;
        this.day = day;
    }
    public Reminder(String frequency, String time, int date ){
        this.frequency = frequency;
        this.time = time;
        this.date = date;
    }
    public String getFrequency() { return frequency; }
    public String getTime() { return time; }
    public String getDay() { return day; }
    public int getDate() { return date; }



}
