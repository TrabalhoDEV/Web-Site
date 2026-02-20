package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Student;

/**
 * Interface for define a contract in database's table student operations.
 */
public interface IStudentDAO {

    /**
     * Updates the school class assigned to a specific student
     * @param id            the unique identifier of the student to be updated
     * @param idSchoolClass the unique identifier of the new school class to assign
     * @throws NotFoundException   if no student or school class is found with the given IDs
     * @throws DataException       if a database or persistence error occurs during the update
     * @throws ValidationException if the provided IDs fail business rule validation
     */
    void updateIdSchoolClass(int id, int idSchoolClass)
            throws NotFoundException, DataException, ValidationException;

    /**
     * Updates the password of a specific student
     * @param id       the unique identifier of the student whose password will be updated
     * @param password the new plain-text password to be stored (typically hashed by the implementation)
     * @throws NotFoundException   if no student is found with the given ID
     * @throws DataException       if a database or persistence error occurs during the update
     * @throws ValidationException if the password does not meet the required format or strength rules
     */
    void updatePassword(int id, String password)
            throws NotFoundException, DataException, ValidationException;

    /**
     * Confirms and finalizes the enrollment of a pre-enrolled student
     * @param student the object containing all required enrollment data
     * @throws NotFoundException   if the student record to be enrolled cannot be located
     * @throws DataException       if a database or persistence error occurs during enrollment
     * @throws ValidationException if the student object contains invalid or incomplete data
     */
    void enrollIn(Student student)
            throws NotFoundException, DataException, ValidationException;

    /**
     * Authenticates a student by validating their enrollment number and password
     * @param enrollment the student's unique enrollment number used as a login identifier
     * @param password   the plain-text password to be verified against the stored hash
     * @return           true if the credentials are valid and the student is authenticated;
     * @throws NotFoundException   if no student is found by that enrollment number
     * @throws DataException       if a database or persistence error occurs during authentication
     * @throws ValidationException if the enrollment number or password fail format validation
     */
    boolean login(String enrollment, String password)
            throws NotFoundException, DataException, ValidationException;
}