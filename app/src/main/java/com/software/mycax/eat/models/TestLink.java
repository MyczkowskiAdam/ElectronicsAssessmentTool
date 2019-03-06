package com.software.mycax.eat.models;

@SuppressWarnings("unused")
public class TestLink {
    private String testUid;
    private String testTopic;

    public TestLink() {
        // empty constructor
    }

    public TestLink(String testUid, String testTopic) {
        this.testTopic = testTopic;
        this.testUid = testUid;
    }

    public void setTestTopic(String testTopic) {
        this.testTopic = testTopic;
    }

    public String getTestTopic() {
        return testTopic;
    }

    public void setTestUid(String testUid) {
        this.testUid = testUid;
    }

    public String getTestUid() {
        return testUid;
    }
}
