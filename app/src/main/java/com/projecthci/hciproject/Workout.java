package com.projecthci.hciproject;

/**
 * Created by Roy on 5-1-2016.
 */
public class Workout
{
    private String name;
    private int repititions;
    private boolean done;

    public Workout(String name, int repititions)
    {
        this.name = name;
        this.repititions = repititions;
        this.done = false;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public boolean getDone()
    {
        return done;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRepititions(int repititions)
    {
        this.repititions = repititions;
    }

    public String getName()
    {
        return name;
    }

    public int getRepititions()
    {
        return repititions;
    }
}
