package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.TeacherPendency;
import java.util.List;
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
    Map<Integer, StudentSubject> findMany(int skip, int take, int studentId) throws DataException;


    /**
     * Retrieves the total count of subjects associated with a specific student.
     *
     * @param studentId the unique identifier of the student
     * @return the total number of subjects for the given student ID
     */
    int totalCount(int studentId) throws DataException;
    
    /**
     * Method that find the students with less grade, helping teacher find who he should give more attention
     * @param teacherId is teacher id
     * @return      students, subjects and the data between tehem, all related to passed teacher
     * @throws DataException  if an error in database or persistence happen;
     * @throws ValidationException  if teacherId is empty or less than 0
     */
    Map<Integer, StudentSubject> findStudentsThatRequireTeacher(int teacherId) throws DataException, ValidationException;

    /**
     * Method that returns percentuals of how many students were approved, failed or pending for grade.
     * @param teacherId is teacher id
     * @return          a record that contains how many students were approved, failed and pending
     * @throws DataException  if an error in database or persistence happen;
     * @throws ValidationException  if teacherId is empty or less than 0
     */
    StudentsPerformance studentsPerformance(int teacherId) throws DataException, ValidationException;

    /**
     * Method that returns pendencies of teacher, like which students he should give grades, and if it's late or not
     * @param teacherId is teacher id
     * @return      a list of records that each contains some data that will be showed to teacher
     * @throws DataException  if an error in database or persistence happen;
     * @throws ValidationException  if teacherId is empty or less than 0
     */
    List<TeacherPendency> teacherPendency(int teacherId) throws DataException, ValidationException;

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
    int totalCount(int studentId);
}
