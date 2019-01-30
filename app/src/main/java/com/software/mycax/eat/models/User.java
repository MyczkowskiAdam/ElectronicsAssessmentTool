package com.software.mycax.eat.models;

public class User {
    public String name, email, schoolCode, teacherCode;
    public int accountType;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String schoolCode, String teacherCode, int accountType) {
        this.email = email;
        this.name = name;
        this.schoolCode = schoolCode;
        this.teacherCode = teacherCode;
        this.accountType = accountType;
    }

}
