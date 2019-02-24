package com.software.mycax.eat.models;

public class TestQuestion {
    private String question;
    private String answer;

    public TestQuestion() {
        // Default constructor required for calls to DataSnapshot.getValue(TestQuestion.class)
    }

    public TestQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
