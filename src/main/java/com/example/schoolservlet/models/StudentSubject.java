package com.example.schoolservlet.models;

import com.example.schoolservlet.utils.Constants;

import java.util.Date;

/**
 * Represents a student's progress in a specific subject.
 */
public class StudentSubject {
    // Attributes:
    private int id;
    private Student student;
    private Subject subject;
    private Double grade1;
    private Double grade2;
    private String obs;

    // Constructors
    /** Creates an empty student-subject instance. */
    public StudentSubject(){}

    /**
     * Creates a student-subject record.
     *
     * @param id record identifier
     * @param obs observation text
     * @param grade1 first grade
     * @param grade2 second grade
     * @param student student reference
     * @param subject subject reference
     */
    public StudentSubject(int id, String obs, Double grade1, Double grade2, Student student, Subject subject){
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.grade1 = grade1;
        this.grade2 = grade2;
        this.obs = obs;
    }

    /** @return record identifier */
    public int getId() { return id; }

    /** @return student reference */
    public Student getStudent() { return student; }

    /** @return subject reference */
    public Subject getSubject() { return subject; }

    /** @return first grade */
    public Double getGrade1() { return grade1; }

    /** @return second grade */
    public Double getGrade2() { return grade2; }

    /**
     * Computes the average from available grades.
     *
     * @return arithmetic mean when both grades exist; otherwise the existing grade; or {@code null} if none exists
     */
    public Double getAverage() {
        if (grade1 != null && grade2 != null){
            return (grade1 + grade2) / 2;
        }
        if (grade1 != null){
            return grade1;
        }
        if (grade2 != null){
            return grade2;
        }
        return null;
    }

    /** @return observation text */
    public String getObs() { return obs; }

    /**
     * Returns the current status based on grades and subject deadline.
     *
     * @return "Aprovado", "Reprovado", or "Pendente"
     */
    public String getStatus(){
        if ((grade1 != null && grade2 != null) && this.subject.getDeadline().before(new Date())){
            return getAverage() >= Constants.MIN_GRADE_TO_BE_APPROVAL ? "Aprovado" : "Reprovado";
        }
        return "Pendente";
    }

    /** @param id record identifier */
    public void setId(int id) { this.id = id; }

    /** @param student student reference */
    public void setStudent(Student student) { this.student = student; }

    /** @param subject subject reference */
    public void setSubject(Subject subject) { this.subject = subject; }

    /** @param grade1 first grade */
    public void setGrade1(Double grade1) { this.grade1 = grade1; }

    /** @param grade2 second grade */
    public void setGrade2(Double grade2) { this.grade2 = grade2; }

    /** @param obs observation text */
    public void setObs(String obs) { this.obs = obs; }

    /**
     * Returns a printable representation of the student-subject record.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "StudentSubject{" +
                "id=" + id +
                ", student=" + student +
                ", subject=" + subject +
                ", grade1=" + grade1 +
                ", grade2=" + grade2 +
                ", obs='" + obs + '\'' +
                '}';
    }
}