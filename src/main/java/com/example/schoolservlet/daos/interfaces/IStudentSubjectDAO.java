package com.example.schoolservlet.daos.interfaces;

import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.records.StudentsPerformance;
import com.example.schoolservlet.utils.records.TeacherPendency;
import java.util.List;
import java.util.Map;

/**
 * Interface that defines the contract, like functions, exceptions and params of diferent methos implemented in {@link com.example.schoolservlet.daos.StudentSubjectDAO}
 */
public interface IStudentSubjectDAO {
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
}
