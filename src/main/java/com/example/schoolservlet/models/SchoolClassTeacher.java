package com.example.schoolservlet.models;

public class SchoolClassTeacher {
//    Atributes:
    private int id;
    private int schoolClassId;
    private SchoolClass schoolClass;
    private int teacherId;
    private Teacher teacher;

//    Getters:

    public int getId() {
        return id;
    }

    public int getSchoolClassId() {
        return schoolClassId;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

//    Setters:
    public void setId(int id) {
        this.id = id;
    }

    public void setSchoolClassId(int schoolClassId) {
        this.schoolClassId = schoolClassId;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
