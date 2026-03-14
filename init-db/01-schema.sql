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