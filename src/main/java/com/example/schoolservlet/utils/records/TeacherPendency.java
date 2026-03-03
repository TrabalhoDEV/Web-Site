package com.example.schoolservlet.utils.records;

import java.util.Date;

public record TeacherPendency(
        int studentId,
        String studentName,
        String subjectName,
        Double grade1,
        Double grade2,
        Date deadline,
        String status
) {}