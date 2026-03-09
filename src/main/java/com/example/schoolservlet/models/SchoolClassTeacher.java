package com.example.schoolservlet.models;

public class SchoolClassTeacher {
//    Atributes:
    private int id;
    private SchoolClass schoolClass;
    private Teacher teacher;

//    Construtors:

    public SchoolClassTeacher() {
    }

    public SchoolClassTeacher(int id, SchoolClass schoolClass, Teacher teacher) {
        this.id = id;
        this.schoolClass = schoolClass;
        this.teacher = teacher;
    }

    //    Getters:

    public int getId() {
        return id;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public Teacher getTeacher() {
        return teacher;
    }

//    Setters:
    public void setId(int id) {
        this.id = id;
    }


    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
