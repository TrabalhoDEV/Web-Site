package com.example.schoolservlet.utils.records;

import java.util.Date;

/**
 * Represents a teacher's pending task related to a student's subject.
 *
 * <p>Includes student information, subject details, grades, deadline, and current status.
 *
 * @param studentId        the ID of the student
 * @param studentSubjectId the ID of the student's subject
 * @param studentName      the name of the student
 * @param subjectName      the name of the subject
 * @param grade1           the first grade, if available
 * @param grade2           the second grade, if available
 * @param deadline         the deadline for submission or evaluation
 * @param status           the current status of the pendency
 */
public record TeacherPendency(
        int studentId,
        int studentSubjectId,
        String studentName,
        String subjectName,
        Double grade1,
        Double grade2,
        Date deadline,
        String status
) {}