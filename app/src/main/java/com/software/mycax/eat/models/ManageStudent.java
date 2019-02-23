package com.software.mycax.eat.models;

public class ManageStudent {
    private String studentName;
    private String studentEmail;

    public ManageStudent() {
        // empty constructor
    }

    public ManageStudent(String studentName, String studentEmail) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }
}
