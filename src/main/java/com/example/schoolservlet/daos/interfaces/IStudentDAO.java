package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Student;

public interface IStudentDAO {
    /**
     * Method used to change the school class of the student
     * @param id is student's id
     * @param idSchoolClass is new school class' id
     * @return              true if the update happen
     */
    void updateIdSchoolClass(int id, int idSchoolClass) throws NotFoundException, DataAccessException, InvalidNumberException;

    /**
     * Method used to change student password
     * @param id is student's id
     * @param password is student's new password
     * @return         if the change happen true, else false
     */
    void updatePassword(int id, String password) throws NotFoundException, DataAccessException, InvalidNumberException;

    /**
     * Method user to confirm student's enrollment, after the pre-enrollment
     * @param student is student's object with all fields
     * @return        true if the enrollment is concluded
     */
    void enrollIn(Student student) throws NotFoundException, DataAccessException, InvalidNumberException, ValidationException;

    /**
     * Method that validates if student can be logged in
     * @param enrollment is student's enrollment
     * @param password is input's password
     * @return         true if student password matches the hash in database
     */
    boolean login(String enrollment, String password) throws RequiredFieldException, NotFoundException, DataAccessException;
}
