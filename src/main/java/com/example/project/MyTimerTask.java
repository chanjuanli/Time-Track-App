package com.example.project;

import javafx.scene.text.Text;

import java.util.TimerTask;

/**
 * The type My timer task.
 */
public class MyTimerTask extends TimerTask {
    private final Text datetimeTxt;
    private long startTimeMillis;

    /**
     * Instantiates a new My timer task.
     *
     * @param datetimeTxt the datetime txt
     */
    public MyTimerTask(Text datetimeTxt) {
        this.datetimeTxt = datetimeTxt;
    }

    /**
     * Sets start time millis.
     *
     * @param startTimeMillis the start time millis
     */
    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    @Override
    public void run() {
        long currentTimeMillis = System.currentTimeMillis();
        long durationInMillis = currentTimeMillis - startTimeMillis;

        long hours = durationInMillis / 1000 / 60 / 60;
        long minutes = durationInMillis / 1000 / 60 - hours * 60;
        long seconds = durationInMillis / 1000 - hours * 60 * 60 - minutes * 60;

        // creating a formatted string representing time in the format "HH:MM:SS" (hours:minutes:seconds).
        String timeString = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

        // Update GUI(Graphical User Interface).
        datetimeTxt.setText(timeString);
    }
}
