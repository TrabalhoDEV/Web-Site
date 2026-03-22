package com.example.schoolservlet.models;

import java.util.Date;

/**
 * Represents a school subject.
 */
public class Subject {
    // Attributes:
    private int id;
    private String name;
    private Date deadline;

    // Constructors:
    /** Creates an empty subject instance. */
    public Subject(){}

    /**
     * Creates a subject with full data.
     *
     * @param id subject identifier
     * @param name subject name
     * @param deadline subject deadline
     */
    public Subject(int id, String name, Date deadline){
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    /**
     * Creates a subject with identifier only.
     *
     * @param id subject identifier
     */
    public Subject(int id) {
        this.id = id;
    }

    /** @return subject identifier */
    public int getId() { return id; }

    /** @return subject name */
    public String getName() { return name; }

    /** @return subject deadline */
    public Date getDeadline() { return deadline; }

    /** @param id subject identifier */
    public void setId(int id) { this.id = id; }

    /** @param name subject name */
    public void setName(String name) { this.name = name; }

    /** @param deadline subject deadline */
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    /**
     * Returns a printable representation of the subject.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deadline=" + deadline +
                '}';
    }
}