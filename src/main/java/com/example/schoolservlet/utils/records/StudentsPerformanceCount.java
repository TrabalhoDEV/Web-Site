package com.example.schoolservlet.utils.records;

/**
 * Represents the aggregated count of student performance metrics.
 *
 * <p>Tracks the number of approved, pending, and failed students.
 *
 * @param approved the total number of students who passed
 * @param pending  the total number of students with pending status
 * @param failed   the total number of students who failed
 */
public record StudentsPerformanceCount(int approved, int pending, int failed) {
}
