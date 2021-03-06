package com.software.mycax.eat.models;

import com.google.firebase.database.IgnoreExtraProperties;

@SuppressWarnings("unused")
@IgnoreExtraProperties
public final class User {
    private String userUid;
    private String name;
    private String email;
    private String schoolCode;
    private String teacherCode;
    private String accountKey;
    private int accountType;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userUid, String name, String email, String schoolCode, String teacherCode, int accountType, String accountKey) {
        this.userUid = userUid;
        this.email = email;
        this.name = name;
        this.schoolCode = schoolCode;
        this.teacherCode = teacherCode;
        this.accountType = accountType;
        this.accountKey = accountKey;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }
}
