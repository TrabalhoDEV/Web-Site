package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.StudentSubject;

import java.util.Map;

/**
 * Data Access Object interface for managing Student Subject operations.
 * Defines contract for accessing student subject data from the database.
 */
public interface IStudentSubjectDAO {

    /**
     * Retrieves a paginated collection of subjects for a specific student.
     *
     * @param skip the number of records to skip for pagination
     * @param take the maximum number of records to retrieve
     * @param studentId the unique identifier of the student
     * @return a {@link Map} where the key is the subject ID and the value is the {@link StudentSubject} object,
     *         or an empty map if no subjects are found
     */
    Map<Integer, StudentSubject> findMany(int skip, int take, int studentId);


    /**
     * Retrieves the total count of subjects associated with a specific student.
     *
     * @param studentId the unique identifier of the student
     * @return the total number of subjects for the given student ID
     */
    int totalCount(int studentId) throws DataException;
}