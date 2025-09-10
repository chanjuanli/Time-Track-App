package com.example.project;

/**
 * The record class containing the project information, such as project name, start date, completion date, and duration.
 */
public class Record {
    /**
     * the name of the project.
     */
    private String projName;
    /**
     * the start date of the project.
     */
    private String startDate;
    /**
     * The completion date of the project.
     */
    private String completeDate;
    /**
     * The duration of the project in seconds.
     */
    private long durationInSecs;

    /**
     * Instantiates a new Record.
     *
     * @param projName       the name of the project
     * @param startDate      the start date
     * @param completeDate   the complete date
     * @param durationInSecs the duration in secs
     */
    public Record(String projName, String startDate, String completeDate, long durationInSecs) {
        this.projName = projName;
        this.startDate = startDate;
        this.completeDate = completeDate;
        this.durationInSecs = durationInSecs;
    }


    /**
     * Retrieves the name of the project.
     *
     * @return the project name
     */
    public String getProjName() {
        return projName;
    }

    /**
     * Sets the name of the project.
     *
     * @param projName the project name
     */
    public void setProjName(String projName) {
        this.projName = projName;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets complete date.
     *
     * @return the complete date
     */
    public String getCompleteDate() {
        return completeDate;
    }

    /**
     * Sets complete date.
     *
     * @param completeDate the complete date
     */
    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    /**
     * Gets duration in secs.
     *
     * @return the duration in secs
     */
    public long getDurationInSecs() {
        return durationInSecs;
    }

    /**
     * Sets duration in secs.
     *
     * @param durationInSecs the duration in secs
     */
    public void setDurationInSecs(long durationInSecs) {
        this.durationInSecs = durationInSecs;
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public String getDuration() {
        long hours = durationInSecs / 60 / 60;
        long minutes = durationInSecs / 60 - hours * 60;
        long seconds = durationInSecs - hours * 60 * 60 - minutes * 60;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
