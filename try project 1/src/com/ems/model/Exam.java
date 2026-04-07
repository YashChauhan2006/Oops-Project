package com.ems.model;

import java.util.Date;

public class Exam {
    private int examId;
    private String title;
    private String description;
    private int durationMinutes;
    private Date scheduledAt;
    private int createdBy; // Teacher's userId

    public Exam() {}

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public Date getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Date scheduledAt) { this.scheduledAt = scheduledAt; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    @Override
    public String toString() {
        return title + " (" + durationMinutes + " mins)";
    }
}
