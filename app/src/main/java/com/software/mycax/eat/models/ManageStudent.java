package com.software.mycax.eat.models;

public class ManageStudent {
    private final String studentName;
    private final String studentEmail;

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
