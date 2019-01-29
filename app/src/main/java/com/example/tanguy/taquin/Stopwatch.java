package com.example.tanguy.taquin;

import android.os.SystemClock;

class Stopwatch {

    private long startTime = -1;

    public void start()
    {
        if(startTime == -1) {
            startTime = SystemClock.elapsedRealtime();
        }

    }

    public long milliseconds()
    {
        long now = SystemClock.elapsedRealtime();
        return now - startTime;
    }

}
