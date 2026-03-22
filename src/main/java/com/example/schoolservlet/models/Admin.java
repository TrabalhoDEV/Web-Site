package com.example.schoolservlet.models;

/**
 * Represents an administrator user.
 */
public class Admin {
    // Attributes:
    private int id;
    private String document;
    private String email;
    private String password;

    // Constructors:
    /** Creates an empty admin instance. */
    public Admin(){}

    /**
     * Creates an admin with basic identification data.
     *
     * @param id admin identifier
     * @param email admin email
     * @param document admin document
     */
    public Admin(int id, String email, String document){
        this.id = id;
        this.email = email;
        this.document = document;
    }

    /**
     * Creates an admin for authentication workflows.
     *
     * @param document admin document
     * @param password admin password
     */
    public Admin(String document, String password) {
        this.document = document;
        this.password = password;
    }

    /** @return admin identifier */
    public int getId() { return id; }

    /** @return admin document */
    public String getDocument() { return document; }

    /** @return admin email */
    public String getEmail() { return email; }

    /** @return admin password */
    public String getPassword() { return password; }

    /**
     * Sets admin identifier.
     *
     * @param id admin identifier
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets admin email.
     *
     * @param email admin email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Sets admin document.
     *
     * @param document admin document
     */
    public void setDocument(String document) { this.document = document; }

    /**
     * Sets admin password.
     *
     * @param password admin password
     */
    public void setPassword(String password) { this.password = password; }
}