package com.example.tanguy.taquin;

import android.os.SystemClock;

class Stopwatch {

    private long startTime = -1;
    private long stopTime = -1;

    public void start()
    {
        if(startTime == -1)
            startTime = SystemClock.elapsedRealtime();
        else if(stopTime != -1)
        {
            long now = SystemClock.elapsedRealtime();
            long stopped_for = now - stopTime;
            startTime += stopped_for;
            stopTime = -1;
        }
    }

    public void stop()
    {
        if(stopTime == -1)
            stopTime = SystemClock.elapsedRealtime();
    }

    /*public boolean stopped()
    {
        return stopTime != -1;
    }*/

    public long milliseconds()
    {
        long now = SystemClock.elapsedRealtime();
        long stopped_for = 0;
        if(stopTime != -1)
            stopped_for = now - stopTime;
        return now - startTime - stopped_for;
    }

}
