package com.example.schoolservlet.utils.records;

public record TeacherStudentGrades(
        int studentId,
        String schoolYear,
        String studentName,
        String subjectName,
        Double grade1,
        Double grade2
) {
}
