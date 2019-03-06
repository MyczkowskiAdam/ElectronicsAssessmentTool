package com.software.mycax.eat.models;

@SuppressWarnings("unused")
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

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
