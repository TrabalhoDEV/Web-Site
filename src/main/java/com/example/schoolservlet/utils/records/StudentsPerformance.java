package com.example.schoolservlet.utils.records;

/**
 * Represents the performance summary of students.
 *
 * <p>Contains counts of approved, pending, and failed students.
 *
 * @param approved the number of students who passed
 * @param pending  the number of students with pending status
 * @param failed   the number of students who failed
 */
public record StudentsPerformance(int approved, int pending, int failed) {
}