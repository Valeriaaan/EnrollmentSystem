package com.example.enrollmentsystem.model;

public class Student {
    private String studentID;
    private String name;
    private String department;
    private String level;
    private String course;
    private String curriculum;

    public Student(String studentID, String name, String department, String level, String course, String curriculum) {
        this.studentID = studentID;
        this.name = name;
        this.department = department;
        this.level = level;
        this.course = course;
        this.curriculum = curriculum;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getStudentName() {
        return name;
    }

    public String getStudentDepartment() {
        return department;
    }

    public String getStudentLevel() {
        return level;
    }

    public String getStudentCourse() {
        return course;
    }

    public String getStudentCurriculum() {
        return curriculum;
    }

    @Override
    public String toString() {
        return name;
    }
}
