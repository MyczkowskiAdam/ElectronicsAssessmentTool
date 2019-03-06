package com.software.mycax.eat.models;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TestSet {
    private String circuitUrl;
    private String testUid;
    private List<TestQuestion> questionList;
    private String teacherCode;
    private String testTopic;
    @SuppressWarnings("FieldCanBeLocal")
    private int testSize;

    public TestSet() {
        // Default constructor required for calls to DataSnapshot.getValue(TestSet.class)
    }

    public TestSet(String circuitUrl, String testUid, String teacherCode, String testTopic) {
        this.circuitUrl = circuitUrl;
        this.testUid = testUid;
        this.questionList = new ArrayList<>();
        this.teacherCode = teacherCode;
        this.testTopic = testTopic;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getTestTopic() {
        return testTopic;
    }

    public void setTestTopic(String testTopic) {
        this.testTopic = testTopic;
    }

    public String getCircuitUrl() {
        return circuitUrl;
    }

    public void setCircuitUrl(String circuitUrl) {
        this.circuitUrl = circuitUrl;
    }

    public String getTestUid() {
        return testUid;
    }

    public void setTestUid(String testUid) {
        this.testUid = testUid;
    }

    public List<TestQuestion> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<TestQuestion> questionList) {
        this.questionList = questionList;
    }

    public void addQuestion(TestQuestion testQuestion) {
        questionList.add(testQuestion);
    }

    public int getTestSize() {
        return questionList.size();
    }

    public void setTestSize(int testSize) {
        this.testSize = testSize;
    }
}
