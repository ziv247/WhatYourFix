package com.exmp.ziv24.mapsproject.model;

public class Opening_hours {
    private String open_now;

    private String[] weekday_text;

    public String getOpen_now ()
    {

        if (open_now == "true"){
            return "true";
        }else {
            return "false";
        }
    }

    public void setOpen_now (String open_now)
    {
        this.open_now = open_now;
    }

    public String[] getWeekday_text ()
    {
        return weekday_text;
    }

    public void setWeekday_text (String[] weekday_text)
    {
        this.weekday_text = weekday_text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [open_now = "+open_now+", weekday_text = "+weekday_text+"]";
    }
}
