package com.example.b07demosummer2024;

import java.io.Serializable;
/**
 * Model class representing a reminder with different frequency types (daily, weekly, monthly).
 * Implements {@link Serializable} to allow passing between activities.
 * Used to store and transfer reminder data to/from Firebase.
 */
public class Reminder implements Serializable {
    public String frequency;
    public String time;
    public String day;
    public int date;
    //model class used to pass values that firebase can use
    public Reminder(){}//required for firebase to work

    /**
     * Constructor for daily reminders.
     * @param frequency Must be "daily"
     * @param time The reminder time in HH:MM format
     */
    public Reminder(String frequency, String time){
        this.frequency = frequency;
        this.time = time;
    }
    /**
     * Constructor for weekly reminders.
     * @param frequency Must be "weekly"
     * @param time The reminder time in HH:MM format
     * @param day The day of week (e.g., "Monday")
     */
    public Reminder(String frequency, String time, String day ){
        this.frequency = frequency;
        this.time = time;
        this.day = day;
    }
    /**
     * Constructor for monthly reminders.
     * @param frequency Must be "monthly"
     * @param time The reminder time in HH:MM format
     * @param date The date of month (1-31)
     */
    public Reminder(String frequency, String time, int date ){
        this.frequency = frequency;
        this.time = time;
        this.date = date;
    }
    /**
     * @return The reminder frequency (daily, weekly, monthly)
     */
    public String getFrequency() { return frequency; }
    /**
     * @return The reminder time in HH:MM format
     */
    public String getTime() { return time; }
    /**
     * @return The day of week for weekly reminders
     */
    public String getDay() { return day; }
    /**
     * @return The date of month for monthly reminders
     */
    public int getDate() { return date; }



}
