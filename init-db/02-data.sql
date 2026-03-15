-- Data load para testes de API - Escola Vertice
ROLLBACK;
BEGIN;

TRUNCATE TABLE
    student_subject,
    subject_teacher,
    school_class_subject,
    school_class_teacher,
    student,
    admin,
    subject,
    teacher,
    school_class
RESTART IDENTITY CASCADE;

-- =========================
-- SCHOOL CLASS
-- =========================
INSERT INTO school_class (school_year)
VALUES
    ('1o Ano A'),
    ('1o Ano B');

-- =========================
-- TEACHER
-- =========================
INSERT INTO teacher (name, email, username, password)
VALUES
    ('Carlos Eduardo Lima', 'carlos.lima@escolavertice.com.br', 'carlos.lima', 'Aa123456');

-- =========================
-- SUBJECTS
-- =========================
INSERT INTO subject (name, deadline)
VALUES
    ('Matematica', '2026-12-10'),
    ('Portugues',  '2026-12-10'),
    ('Historia',   '2026-12-10'),
    ('Ciencias',   '2026-12-10');

-- =========================
-- ADMIN
-- =========================
INSERT INTO admin (document, email, password)
VALUES
    ('12345678901', 'devsecretaria@gmail.com', 'As123456');

-- =========================
-- STUDENTS (14 alunos)
-- =========================
INSERT INTO student (name, email, cpf, id_school_class)
VALUES
    ('Miguel Fernandes',    'miguel.fernandes@aluno.vertice.com.br', '10000000001', 1),
    ('Ana Clara Souza',     'ana.clara@aluno.vertice.com.br',        '10000000002', 1),
    ('Bruno Henrique Silva','bruno.henrique@aluno.vertice.com.br',   '10000000003', 1),
    ('Camila Rodrigues',    'camila.rodrigues@aluno.vertice.com.br', '10000000004', 1),
    ('Daniel Martins',      'daniel.martins@aluno.vertice.com.br',   '10000000005', 1),
    ('Eduarda Nogueira',    'eduarda.nogueira@aluno.vertice.com.br', '10000000006', 1),
    ('Felipe Araujo',       'felipe.araujo@aluno.vertice.com.br',    '10000000007', 2),
    ('Gabriela Moraes',     'gabriela.moraes@aluno.vertice.com.br',  '10000000008', 2),
    ('Henrique Almeida',    'henrique.almeida@aluno.vertice.com.br', '10000000009', 2),
    ('Isabela Castro',      'isabela.castro@aluno.vertice.com.br',   '10000000010', 2),
    ('Joao Pedro Santos',   'joao.pedro@aluno.vertice.com.br',       '10000000011', 2),
    ('Larissa Teixeira',    'larissa.teixeira@aluno.vertice.com.br', '10000000012', 2),
    ('Matheus Oliveira',    'matheus.oliveira@aluno.vertice.com.br', '10000000013', 2),
    ('Nathalia Barros',     'nathalia.barros@aluno.vertice.com.br',  '10000000014', 2);

-- =========================
-- SCHOOL CLASS TEACHER
-- =========================
INSERT INTO school_class_teacher (id_school_class, id_teacher, subject_list)
VALUES
    (1, 1, '{1, 2, 3, 4}'),
    (2, 1, '{1, 2, 3, 4}');

-- =========================
-- SCHOOL CLASS SUBJECT
-- =========================
INSERT INTO school_class_subject (id_school_class, id_subject)
VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (2, 1), (2, 2), (2, 3), (2, 4);

-- =========================
-- SUBJECT TEACHER
-- =========================
INSERT INTO subject_teacher (id_subject, id_teacher)
VALUES
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 1);

-- =========================
-- STUDENT SUBJECT (notas)
-- =========================
INSERT INTO student_subject (id_student, id_subject, grade1, grade2, obs)
VALUES
    -- Aluno principal (2 aprovados, 2 reprovados)
    (1, 1,  8.5,  7.5, 'Bom desempenho em Matematica'),
    (1, 2,  4.5,  5.0, 'Dificuldade em interpretacao de texto'),
    (1, 3,  7.8,  8.0, 'Boa participacao nas discussoes'),
    (1, 4,  3.5,  4.0, 'Precisa reforco em conteudos basicos'),

    -- Demais alunos
    (2,  1, 8.0,  7.2, 'Aprovada'),
    (3,  2, 4.0,  5.0, 'Reprovado'),
    (4,  3, 7.5,  7.0, 'Aprovada'),
    (5,  4, 3.0,  4.0, 'Reprovado'),
    (6,  1, 9.0,  8.0, 'Aprovada'),
    (7,  2, 5.0,  4.5, 'Reprovado'),
    (8,  3, 8.0,  7.2, 'Aprovada'),
    (9,  4, 2.5,  3.5, 'Reprovado'),
    (10, 1, 7.2,  7.0, 'Aprovada'),
    (11, 2, 4.0,  3.0, 'Reprovado'),
    (12, 3, 8.0,  7.5, 'Aprovada'),
    (12, 4, 5.0,  4.0, 'Reprovada em disciplina complementar'),

    -- Pendentes
    (13, 1, NULL, NULL, 'Sem lancamento de notas ate o momento'),
    (14, 2, 7.0,  NULL, 'Aguardando segunda avaliacao');

COMMIT;