-- =========================
-- SCHOOL CLASS
-- =========================
INSERT INTO school_class (school_year)
VALUES ('1º Ano A'),
       ('1º Ano B'),
       ('2º Ano A');

-- =========================
-- TEACHERS
-- =========================
INSERT INTO teacher (name, email, username, password)
VALUES ('Caio Marcos', 'caio.maciel@institutojef.org.br', 'caio.marcos', '123456'),
       ('Mariana Souza', 'mariana.souza@school.com', 'mariana.souza', '123456'),
       ('Roberto Lima', 'roberto.lima@school.com', 'roberto.lima', '123456');

-- =========================
-- SUBJECTS
-- =========================
INSERT INTO subject (name, deadline)
VALUES ('Matemática', '2026-12-10'),
       ('Português', '2026-12-10'),
       ('História', '2026-12-10');

-- =========================
-- ADMINS
-- =========================
INSERT INTO admin (document, email, password)
VALUES ('12345678901', 'devsecretaria@gmail.com', 'admin123');

-- =========================
-- STUDENTS
-- =========================
INSERT INTO student (name, email, password, cpf, id_school_class)
VALUES ('Ana Costa', 'ana.costa@student.com', '123456', '11111111111', 1),
       ('Bruno Pereira', 'bruno.pereira@student.com', '123456', '22222222222', 1),
       ('Lucas Santos', 'lucas.santos@student.com', '123456', '33333333333', 2),
       ('Juliana Alves', 'juliana.alves@student.com', '123456', '44444444444', 3);

-- =========================
-- SCHOOL CLASS TEACHER
-- =========================
INSERT INTO school_class_teacher (id_school_class, id_teacher)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 3);

-- =========================
-- SCHOOL CLASS SUBJECT
-- =========================
INSERT INTO school_class_subject (id_school_class, id_subject)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 3),
       (3, 2);

-- =========================
-- SUBJECT TEACHER
-- =========================
INSERT INTO subject_teacher (id_subject, id_teacher)
VALUES (1, 1),
       (2, 2),
       (3, 3);

-- =========================
-- STUDENT SUBJECT (GRADES)
-- =========================
INSERT INTO student_subject (id_student, id_subject, grade1, grade2, obs)
VALUES (1, 1, 8.5, 7.0, 'Bom desempenho'),
       (1, 2, 9.0, 8.5, 'Ótimo aluno'),
       (2, 1, 6.0, 7.0, 'Precisa melhorar na prova'),
       (3, 3, 7.5, 6.5, 'Participa das aulas'),
       (4, 2, 9.5, 9.0, 'Excelente');