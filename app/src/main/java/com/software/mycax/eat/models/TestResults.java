package com.software.mycax.eat.models;

public class TestResults {
    private String testUid;
    private int score;
    private int maxScore;
    private boolean completed;

    public TestResults() {
        // Default constructor required for calls to DataSnapshot.getValue(TestResults.class)
    }

    public TestResults(String testUid, int score, int maxScore, boolean completed) {
        this.testUid = testUid;
        this.score = score;
        this.maxScore = maxScore;
        this.completed = completed;
    }

    public String getTestUid() {
        return testUid;
    }

    public void setTestUid(String testUid) {
        this.testUid = testUid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
