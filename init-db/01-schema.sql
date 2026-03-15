CREATE TABLE school_class
(
    id          SERIAL PRIMARY KEY,
    school_year VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE teacher
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(150) NOT NULL,
    email    VARCHAR(355) NOT NULL UNIQUE,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password TEXT NOT NULL
);

CREATE TABLE student
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(150) DEFAULT 'não informado',
    email    VARCHAR(345) UNIQUE,
    password TEXT DEFAULT 'não informado',
    status   INT          DEFAULT 1,
    cpf      VARCHAR(11) NOT NULL UNIQUE CHECK (cpf ~ '^[0-9]{11}$') ,
	id_school_class INT NOT NULL REFERENCES school_class(id)
);

CREATE TABLE subject
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE,
    deadline DATE         NOT NULL
);

CREATE TABLE admin
(
    id       SERIAL PRIMARY KEY,
    document VARCHAR(11),
    email    VARCHAR(355) NOT NULL UNIQUE,
    password TEXT NOT NULL
);

CREATE TABLE school_class_teacher
(
    id              SERIAL PRIMARY KEY,
    id_school_class INT NOT NULL REFERENCES school_class (id) ON DELETE CASCADE,
    id_teacher      INT NOT NULL REFERENCES teacher (id) ON DELETE CASCADE,
    CONSTRAINT uk_school_class_teacher UNIQUE (id_school_class, id_teacher)
);

CREATE TABLE school_class_subject
(
    id              SERIAL PRIMARY KEY,
    id_school_class INT NOT NULL REFERENCES school_class (id) ON DELETE CASCADE,
    id_subject      INT NOT NULL REFERENCES subject (id) ON DELETE CASCADE,
    CONSTRAINT uk_school_class_subject UNIQUE (id_school_class, id_subject)
);

CREATE TABLE subject_teacher
(
    id         SERIAL PRIMARY KEY,
    id_subject INT NOT NULL REFERENCES subject (id) ON DELETE CASCADE,
    id_teacher INT NOT NULL REFERENCES teacher (id) ON DELETE CASCADE,
    CONSTRAINT uk_subject_teacher UNIQUE (id_subject, id_teacher)
);

CREATE TABLE student_subject
(
    id         SERIAL PRIMARY KEY,
    id_student INT NOT NULL REFERENCES student (id) ON DELETE CASCADE,
    id_subject INT NOT NULL REFERENCES subject (id) ON DELETE CASCADE,
    grade1     NUMERIC(4, 2) CHECK (grade1 BETWEEN 0 AND 10),
    grade2     NUMERIC(4, 2) CHECK (grade2 BETWEEN 0 AND 10),
    obs        TEXT,
    CONSTRAINT uk_student_subject UNIQUE (id_student, id_subject)
);

CREATE VIEW vw_fact_student_grades AS
SELECT s.id          AS student_id,
       s.status,
       sc.id         AS class_id,
       sc.school_year,
       sub.id        AS subject_id,
       sub.name      AS subject_name,
       ss.grade1,
       ss.grade2,
       ROUND((ss.grade1 + ss.grade2) / 2.0, 2) AS avg_grade
FROM student_subject ss
         JOIN student s ON s.id = ss.id_student
         JOIN school_class sc ON sc.id = s.id_school_class
         JOIN subject sub ON sub.id = ss.id_subject;

CREATE VIEW vw_students_per_class AS
SELECT school_year, COUNT(DISTINCT student_id) AS total_students
FROM vw_fact_student_grades
GROUP BY school_year;

CREATE VIEW vw_subjects_per_teacher AS
SELECT t.name, COUNT(st.id_subject) AS total_subjects
FROM teacher t
         JOIN subject_teacher st ON st.id_teacher = t.id
GROUP BY t.name;

CREATE VIEW vw_classes_per_teacher AS
SELECT t.name, COUNT(sct.id_school_class) AS total_classes
FROM teacher t
         JOIN school_class_teacher sct ON sct.id_teacher = t.id
GROUP BY t.name;

CREATE VIEW vw_avg_grades_subject AS
SELECT subject_name, ROUND(AVG(avg_grade), 2) AS avg_grade
FROM vw_fact_student_grades
GROUP BY subject_name;

CREATE VIEW vw_avg_grades_class AS
SELECT school_year, ROUND(AVG(avg_grade), 2) AS avg_grade
FROM vw_fact_student_grades
GROUP BY school_year
ORDER BY avg_grade DESC;

CREATE VIEW vw_student_average AS
SELECT student_id, subject_id, avg_grade AS media
FROM vw_fact_student_grades;

CREATE VIEW vw_approval_rate_per_class AS
SELECT school_year,
       ROUND(
                       SUM(CASE WHEN avg_grade >= 7 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2
           ) AS approval_rate
FROM vw_fact_student_grades
GROUP BY school_year
ORDER BY approval_rate DESC;

CREATE VIEW vw_dashboard_kpi AS
SELECT (SELECT COUNT(*) FROM student)      AS total_students,
       (SELECT COUNT(*) FROM teacher)      AS total_teachers,
       (SELECT COUNT(*) FROM school_class) AS total_classes,
       (SELECT COUNT(*) FROM subject)      AS total_subjects,
       (
           SELECT ROUND(AVG(student_count), 2)
           FROM (
                    SELECT COUNT(*) AS student_count
                    FROM student
                    GROUP BY id_school_class
                ) sub
       )                                   AS avg_students_per_class,
       (
           SELECT sc.school_year
           FROM school_class sc
                    LEFT JOIN student s ON s.id_school_class = sc.id
           GROUP BY sc.school_year
           ORDER BY COUNT(s.id) DESC
                                              LIMIT 1
    )                                   AS class_with_most_students,
           (
               SELECT COUNT(s.id)
               FROM school_class sc
                        LEFT JOIN student s ON s.id_school_class = sc.id
               GROUP BY sc.school_year
               ORDER BY COUNT(s.id) DESC
               LIMIT 1
           )                                   AS max_students_in_class;