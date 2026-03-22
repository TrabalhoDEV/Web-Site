package com.example.schoolservlet.models;

/**
 * Represents a school class/group.
 */
public class SchoolClass {

    // Attributes:
    private int id;
    private String schoolYear;

    // Constructors:
    /** Creates an empty school class instance. */
    public SchoolClass() {

    }

    /**
     * Creates a school class with full data.
     *
     * @param id class identifier
     * @param schoolYear school year/label
     */
    public SchoolClass (int id, String schoolYear) {
        this.id = id;
        this.schoolYear = schoolYear;
    }
    /**
     * Creates a school class with identifier only.
     *
     * @param id class identifier
     */
    public SchoolClass (int id) {
        this.id = id;
    }

    // Getters:
    /** @return class identifier */
    public int getId() {
        return id;
    }

    /** @return school year/label */
    public String getSchoolYear() {
        return schoolYear;
    }

    // Setters:
    /**
     * Sets class identifier.
     *
     * @param id class identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets school year/label.
     *
     * @param schoolYear school year/label
     */
    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }
}