package com.projecthci.hciproject;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roy on 5-1-2016.
 */
public class Schedule
{
    private Map<GregorianCalendar, Workout> scheduledWorkouts;

    public Schedule()
    {
        this.scheduledWorkouts = new HashMap<>();
    }

    public void setScheduledWorkouts(Map<GregorianCalendar, Workout> scheduledWorkouts)
    {
        this.scheduledWorkouts = scheduledWorkouts;
    }

    public void addWorkout(GregorianCalendar date, Workout workout)
    {
        this.scheduledWorkouts.put(date, workout);
    }

    public Map<GregorianCalendar, Workout> getScheduledWorkouts()
    {
        return scheduledWorkouts;
    }
}
