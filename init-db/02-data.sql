-- data load para testes de api - escola vertice
-- regras aplicadas:
--   • todos os valores inseridos em letras minusculas
--   • status = 0 (inativo) se nome != 'não informado' ou se aluno possui notas lancadas
--   • status = 1 (ativo, default) apenas para alunos com nome 'não informado' e sem notas
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
-- school_class (12)
-- =========================
INSERT INTO school_class (school_year) VALUES
       ('1o ano a'),
       ('1o ano b'),
       ('1o ano c'),
       ('1o ano d'),
       ('2o ano a'),
       ('2o ano b'),
       ('2o ano c'),
       ('2o ano d'),
       ('3o ano a'),
       ('3o ano b'),
       ('3o ano c'),
       ('3o ano d');

-- =========================
-- teacher (12)
-- cada professor ministra 2 disciplinas (definidas em subject_teacher)
-- =========================
INSERT INTO teacher (name, email, username, password) VALUES
      ('carlos eduardo lima',    'carlos.lima@escolavertice.com.br',      'carlos.lima',      '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('patricia souza mendes',  'patricia.mendes@escolavertice.com.br',  'patricia.mendes',  '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('roberto alves figueira', 'roberto.figueira@escolavertice.com.br', 'roberto.figueira', '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('fernanda costa braga',   'fernanda.braga@escolavertice.com.br',   'fernanda.braga',   '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('lucas andrade pinto',    'lucas.pinto@escolavertice.com.br',      'lucas.pinto',      '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('mariana oliveira reis',  'mariana.reis@escolavertice.com.br',     'mariana.reis',     '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('jose roberto carvalho',  'jose.carvalho@escolavertice.com.br',    'jose.carvalho',    '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('ana paula vieira',       'ana.vieira@escolavertice.com.br',       'ana.vieira',       '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('thiago nascimento silva', 'thiago.silva@escolavertice.com.br',    'thiago.silva',     '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('claudia ferreira gomes', 'claudia.gomes@escolavertice.com.br',    'claudia.gomes',    '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('sergio monteiro dias',   'sergio.dias@escolavertice.com.br',      'sergio.dias',      '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('renata lima sousa',      'renata.sousa@escolavertice.com.br',     'renata.sousa',     '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A');

-- =========================
-- subject (12)
-- =========================
INSERT INTO subject (name, deadline) VALUES
     ('matemática',      '2026-12-10'),
     ('português',       '2026-12-10'),
     ('história',        '2026-12-10'),
     ('ciências',        '2026-12-10'),
     ('geografia',       '2026-12-10'),
     ('física',          '2026-12-10'),
     ('química',         '2026-12-10'),
     ('inglês',          '2026-12-10'),
     ('educacão física', '2026-12-10'),
     ('artes',           '2026-12-10'),
     ('filosofia',       '2026-12-10'),
     ('sociologia',      '2026-12-10');

-- =========================
-- admin (12)
-- =========================
INSERT INTO admin (document, email, password) VALUES
      ('12345678901', 'secretaria@escolavertice.com.br',    '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('98765432100', 'diretoria@escolavertice.com.br',     '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('11122233344', 'coordenacao@escolavertice.com.br',   '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('55566677788', 'financeiro@escolavertice.com.br',    '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('99988877766', 'rh@escolavertice.com.br',            '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('44455566677', 'ti@escolavertice.com.br',            '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('33322211100', 'pedagogia@escolavertice.com.br',     '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('77788899900', 'biblioteca@escolavertice.com.br',    '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('22211100099', 'laboratorio@escolavertice.com.br',   '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('66677788899', 'ouvidoria@escolavertice.com.br',     '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('10011022033', 'comunicacao@escolavertice.com.br',   '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
      ('20022033044', 'devsecretaria@gmail.com',            '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A');

-- =========================
-- student
-- status = 0 → aluno com nome real OU com notas lancadas
-- status = 1 → aluno sem nome cadastrado e sem notas (default do schema)
-- ids resultantes: 1-60 (nomeados), 61-63 (nao informado + notas), 64-69 (nao informado, sem notas)
-- =========================

-- alunos com nome real → status = 2 (ativo)
INSERT INTO student (name, email, cpf, status, id_school_class, password) VALUES
    -- 1o ano a (turma 1)
    ('miguel fernandes',       'miguel.fernandes@aluno.vertice.com.br',     '10000000001', 2,  1, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('ana clara souza',        'ana.clara@aluno.vertice.com.br',            '10000000002', 2,  1, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('bruno henrique silva',   'bruno.henrique@aluno.vertice.com.br',       '10000000003', 2,  1, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('camila rodrigues',       'camila.rodrigues@aluno.vertice.com.br',     '10000000004', 2,  1, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('daniel martins',         'daniel.martins@aluno.vertice.com.br',       '10000000005', 2,  1, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 1o ano b (turma 2)
    ('eduarda nogueira',       'eduarda.nogueira@aluno.vertice.com.br',     '10000000006', 2,  2, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('felipe araujo',          'felipe.araujo@aluno.vertice.com.br',        '10000000007', 2,  2, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('gabriela moraes',        'gabriela.moraes@aluno.vertice.com.br',      '10000000008', 2,  2, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('henrique almeida',       'henrique.almeida@aluno.vertice.com.br',     '10000000009', 2,  2, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('isabela castro',         'isabela.castro@aluno.vertice.com.br',       '10000000010', 2,  2, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 1o ano c (turma 3)
    ('joao pedro santos',      'joao.pedro@aluno.vertice.com.br',           '10000000011', 2,  3, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('larissa teixeira',       'larissa.teixeira@aluno.vertice.com.br',     '10000000012', 2,  3, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('matheus oliveira',       'matheus.oliveira@aluno.vertice.com.br',     '10000000013', 2,  3, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('nathalia barros',        'nathalia.barros@aluno.vertice.com.br',      '10000000014', 2,  3, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('otavio ramos',           'otavio.ramos@aluno.vertice.com.br',         '10000000015', 2,  3, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 1o ano d (turma 4)
    ('paula vieira',           'paula.vieira@aluno.vertice.com.br',         '10000000016', 2,  4, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('rafael gomes',           'rafael.gomes@aluno.vertice.com.br',         '10000000017', 2,  4, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('sabrina lopes',          'sabrina.lopes@aluno.vertice.com.br',        '10000000018', 2,  4, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('thiago carvalho',        'thiago.carvalho@aluno.vertice.com.br',      '10000000019', 2,  4, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('vitoria nascimento',     'vitoria.nascimento@aluno.vertice.com.br',   '10000000020', 2,  4, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 2o ano a (turma 5)
    ('wellington freitas',     'wellington.freitas@aluno.vertice.com.br',   '10000000021', 2,  5, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('yasmin monteiro',        'yasmin.monteiro@aluno.vertice.com.br',      '10000000022', 2,  5, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('andre luiz batista',     'andre.batista@aluno.vertice.com.br',        '10000000023', 2,  5, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('beatriz cunha',          'beatriz.cunha@aluno.vertice.com.br',        '10000000024', 2,  5, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('caio menezes',           'caio.menezes@aluno.vertice.com.br',         '10000000025', 2,  5, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 2o ano b (turma 6)
    ('debora santana',         'debora.santana@aluno.vertice.com.br',       '10000000026', 2,  6, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('eduardo pires',          'eduardo.pires@aluno.vertice.com.br',        '10000000027', 2,  6, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('flavia rezende',         'flavia.rezende@aluno.vertice.com.br',       '10000000028', 2,  6, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('gustavo macedo',         'gustavo.macedo@aluno.vertice.com.br',       '10000000029', 2,  6, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('helena correia',         'helena.correia@aluno.vertice.com.br',       '10000000030', 2,  6, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 2o ano c (turma 7)
    ('igor cavalcante',        'igor.cavalcante@aluno.vertice.com.br',      '10000000031', 2,  7, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('juliana pacheco',        'juliana.pacheco@aluno.vertice.com.br',      '10000000032', 2,  7, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('kevin rocha',            'kevin.rocha@aluno.vertice.com.br',          '10000000033', 2,  7, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('leticia azevedo',        'leticia.azevedo@aluno.vertice.com.br',      '10000000034', 2,  7, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('marcos vinicius toledo', 'marcos.toledo@aluno.vertice.com.br',        '10000000035', 2,  7, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 2o ano d (turma 8)
    ('nicole ferreira',        'nicole.ferreira@aluno.vertice.com.br',      '10000000036', 2,  8, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('pedro henrique duarte',  'pedro.duarte@aluno.vertice.com.br',         '10000000037', 2,  8, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('rafaela campos',         'rafaela.campos@aluno.vertice.com.br',       '10000000038', 2,  8, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('samuel borges',          'samuel.borges@aluno.vertice.com.br',        '10000000039', 2,  8, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('tatiana moreira',        'tatiana.moreira@aluno.vertice.com.br',      '10000000040', 2,  8, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 3o ano a (turma 9)
    ('ursula fonseca',         'ursula.fonseca@aluno.vertice.com.br',       '10000000041', 2,  9, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('vitor hugo alves',       'vitor.alves@aluno.vertice.com.br',          '10000000042', 2,  9, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('wendy cristina lima',    'wendy.lima@aluno.vertice.com.br',           '10000000043', 2,  9, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('xavier costa melo',      'xavier.melo@aluno.vertice.com.br',          '10000000044', 2,  9, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('yara beatriz silva',     'yara.silva@aluno.vertice.com.br',           '10000000045', 2,  9, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 3o ano b (turma 10)
    ('zelia prado',            'zelia.prado@aluno.vertice.com.br',          '10000000046', 2, 10, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('alex souza pereira',     'alex.pereira@aluno.vertice.com.br',         '10000000047', 2, 10, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('bruna mendes',           'bruna.mendes@aluno.vertice.com.br',         '10000000048', 2, 10, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('cesar augusto neves',    'cesar.neves@aluno.vertice.com.br',          '10000000049', 2, 10, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('diana leal',             'diana.leal@aluno.vertice.com.br',           '10000000050', 2, 10, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 3o ano c (turma 11)
    ('elton marques',          'elton.marques@aluno.vertice.com.br',        '10000000051', 2, 11, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('fabio henrique',         'fabio.henrique@aluno.vertice.com.br',       '10000000052', 2, 11, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('giovana porto',          'giovana.porto@aluno.vertice.com.br',        '10000000053', 2, 11, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('hugo diniz',             'hugo.diniz@aluno.vertice.com.br',           '10000000054', 2, 11, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('iris santos',            'iris.santos@aluno.vertice.com.br',          '10000000055', 2, 11, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    -- 3o ano d (turma 12)
    ('julio cezar ribeiro',    'julio.ribeiro@aluno.vertice.com.br',        '10000000056', 2, 12, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('karla miranda',          'karla.miranda@aluno.vertice.com.br',        '10000000057', 2, 12, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('leandro soares',         'leandro.soares@aluno.vertice.com.br',       '10000000058', 2, 12, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('monica teles',           'monica.teles@aluno.vertice.com.br',         '10000000059', 2, 12, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A'),
    ('nilson araujo',          'nilson.araujo@aluno.vertice.com.br',        '10000000060', 2, 12, '$argon2id$v=19$m=65536,t=3,p=1$WBSHsp7QOW+Ax1kDYiOHfA$FIGsUfRoSMeTjJLiKrkPvL28CFY7wXAfjWxHBD+hO+A');

-- alunos sem nome cadastrado COM notas → status = 1 (inativo)  [ids: 61, 62, 63]
INSERT INTO student (cpf, status, id_school_class) VALUES
   ('20000000001', 1,  1),
   ('20000000002', 1,  5),
   ('20000000003', 1,  9);

-- alunos sem nome cadastrado SEM notas → status = 2 (ativo, default)  [ids: 64-69]
INSERT INTO student (cpf, id_school_class) VALUES
   ('20000000004',  2),
   ('20000000005',  6),
   ('20000000006',  7),
   ('20000000007', 10),
   ('20000000008', 11),
   ('20000000009', 12);

-- =========================
-- school_class_teacher (24)
-- distribuicao: 2 professores por turma
-- carlos(1): mat+fis  | patricia(2): port+qui  | roberto(3): hist+fil | fernanda(4): cie+soc
-- lucas(5):  geo+ef   | mariana(6):  ing+art   | jose(7):    mat+ef   | ana(8):      port+art
-- thiago(9): fis+qui  | claudia(10): hist+soc  | sergio(11): cie+fil  | renata(12):  geo+ing
-- =========================
INSERT INTO school_class_teacher (id_school_class, id_teacher, subject_list) VALUES
     ( 1,  1, '{1,6}'),    -- carlos:  mat+fis   → 1o ano a
     ( 1,  2, '{2,7}'),    -- patricia: port+qui → 1o ano a
     ( 2,  3, '{3,11}'),   -- roberto: hist+fil  → 1o ano b
     ( 2,  4, '{4,12}'),   -- fernanda: cie+soc  → 1o ano b
     ( 3,  5, '{5,9}'),    -- lucas:  geo+ef     → 1o ano c
     ( 3,  6, '{8,10}'),   -- mariana: ing+art   → 1o ano c
     ( 4,  7, '{1,9}'),    -- jose:  mat+ef      → 1o ano d
     ( 4,  8, '{2,10}'),   -- ana:  port+art     → 1o ano d
     ( 5,  9, '{6,7}'),    -- thiago: fis+qui    → 2o ano a
     ( 5, 10, '{3,12}'),   -- claudia: hist+soc  → 2o ano a
     ( 6, 11, '{4,11}'),   -- sergio: cie+fil    → 2o ano b
     ( 6, 12, '{5,8}'),    -- renata: geo+ing    → 2o ano b
     ( 7,  1, '{1,6}'),    -- carlos:  mat+fis   → 2o ano c
     ( 7,  6, '{8,10}'),   -- mariana: ing+art   → 2o ano c
     ( 8,  2, '{2,7}'),    -- patricia: port+qui → 2o ano d
     ( 8,  5, '{5,9}'),    -- lucas:  geo+ef     → 2o ano d
     ( 9,  3, '{3,11}'),   -- roberto: hist+fil  → 3o ano a
     ( 9,  7, '{1,9}'),    -- jose:  mat+ef      → 3o ano a
     (10,  4, '{4,12}'),   -- fernanda: cie+soc  → 3o ano b
     (10,  8, '{2,10}'),   -- ana:  port+art     → 3o ano b
     (11,  9, '{6,7}'),    -- thiago: fis+qui    → 3o ano c
     (11, 12, '{5,8}'),    -- renata: geo+ing    → 3o ano c
     (12, 10, '{3,12}'),   -- claudia: hist+soc  → 3o ano d
     (12, 11, '{4,11}');   -- sergio: cie+fil    → 3o ano d

-- =========================
-- school_class_subject (12 turmas × 12 disciplinas = 144 registros)
-- =========================
INSERT INTO school_class_subject (id_school_class, id_subject) VALUES
       (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),
       (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(2,12),
       (3,1),(3,2),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(3,9),(3,10),(3,11),(3,12),
       (4,1),(4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8),(4,9),(4,10),(4,11),(4,12),
       (5,1),(5,2),(5,3),(5,4),(5,5),(5,6),(5,7),(5,8),(5,9),(5,10),(5,11),(5,12),
       (6,1),(6,2),(6,3),(6,4),(6,5),(6,6),(6,7),(6,8),(6,9),(6,10),(6,11),(6,12),
       (7,1),(7,2),(7,3),(7,4),(7,5),(7,6),(7,7),(7,8),(7,9),(7,10),(7,11),(7,12),
       (8,1),(8,2),(8,3),(8,4),(8,5),(8,6),(8,7),(8,8),(8,9),(8,10),(8,11),(8,12),
       (9,1),(9,2),(9,3),(9,4),(9,5),(9,6),(9,7),(9,8),(9,9),(9,10),(9,11),(9,12),
       (10,1),(10,2),(10,3),(10,4),(10,5),(10,6),(10,7),(10,8),(10,9),(10,10),(10,11),(10,12),
       (11,1),(11,2),(11,3),(11,4),(11,5),(11,6),(11,7),(11,8),(11,9),(11,10),(11,11),(11,12),
       (12,1),(12,2),(12,3),(12,4),(12,5),(12,6),(12,7),(12,8),(12,9),(12,10),(12,11),(12,12);

-- =========================
-- subject_teacher (24)
-- disciplinas compartilhadas: matematica(1) → carlos(1) e jose(7)
--                             portugues(2)  → patricia(2) e ana(8)
--                             etc.
-- =========================
INSERT INTO subject_teacher (id_subject, id_teacher) VALUES
     ( 1,  1),  -- matematica      → carlos
     ( 6,  1),  -- fisica          → carlos
     ( 2,  2),  -- portugues       → patricia
     ( 7,  2),  -- quimica         → patricia
     ( 3,  3),  -- historia        → roberto
     (11,  3),  -- filosofia       → roberto
     ( 4,  4),  -- ciencias        → fernanda
     (12,  4),  -- sociologia      → fernanda
     ( 5,  5),  -- geografia       → lucas
     ( 9,  5),  -- educacao fisica → lucas
     ( 8,  6),  -- ingles          → mariana
     (10,  6),  -- artes           → mariana
     ( 1,  7),  -- matematica      → jose (compartilhada)
     ( 9,  7),  -- educacao fisica → jose (compartilhada)
     ( 2,  8),  -- portugues       → ana (compartilhada)
     (10,  8),  -- artes           → ana (compartilhada)
     ( 6,  9),  -- fisica          → thiago (compartilhada)
     ( 7,  9),  -- quimica         → thiago (compartilhada)
     ( 3, 10),  -- historia        → claudia (compartilhada)
     (12, 10),  -- sociologia      → claudia (compartilhada)
     ( 4, 11),  -- ciencias        → sergio (compartilhada)
     (11, 11),  -- filosofia       → sergio (compartilhada)
     ( 5, 12),  -- geografia       → renata (compartilhada)
     ( 8, 12);  -- ingles          → renata (compartilhada)

-- =========================
-- student_subject (notas)
-- cenarios cobertos:
--   aprovado em todas | reprovado em todas | misto
--   nota1 lancada / nota2 pendente | ambas nulas (sem lancamento)
-- =========================
INSERT INTO student_subject (id_student, id_subject, grade1, grade2, obs) VALUES

    -- -----------------------------------------------
    -- turma 1o ano a  (alunos 1-5)
    -- -----------------------------------------------

    -- aluno 1 (miguel) — misto
    (1,  1,  8.5,  7.5, 'bom desempenho em matematica'),
    (1,  2,  4.5,  5.0, 'dificuldade em interpretacao de texto'),
    (1,  3,  7.8,  8.0, 'boa participacao nas discussoes'),
    (1,  4,  3.5,  4.0, 'precisa de reforco em ciencias'),
    (1,  5,  6.5,  7.0, 'desempenho satisfatorio em geografia'),
    (1,  6,  5.5,  5.0, 'dificuldade com formulas de fisica'),

    -- aluno 2 (ana clara) — aprovada em todas
    (2,  1,  8.0,  7.2, 'aprovada em matematica'),
    (2,  2,  7.5,  8.0, 'boa redacao'),
    (2,  3,  8.5,  9.0, 'destaque em historia'),
    (2,  4,  7.0,  7.5, 'aprovada em ciencias'),
    (2,  7,  7.5,  8.0, 'aprovada em quimica'),
    (2,  8,  9.5,  9.0, 'excelente em ingles'),

    -- aluno 3 (bruno) — reprovado em todas
    (3,  1,  4.0,  5.0, 'reprovado em matematica'),
    (3,  2,  4.5,  3.5, 'reprovado em portugues'),
    (3,  3,  5.5,  4.0, 'reprovado em historia'),
    (3,  5,  2.5,  3.0, 'reprovado em geografia'),
    (3,  6,  5.0,  4.5, 'reprovado em fisica'),
    (3,  9,  4.0,  3.5, 'reprovado em educacao fisica'),

    -- aluno 4 (camila) — aprovada com notas altas
    (4,  1,  7.5,  7.0, 'aprovada em matematica'),
    (4,  2,  8.0,  8.5, 'redacao acima da media'),
    (4,  5,  9.0,  8.5, 'excelente em geografia'),
    (4,  8,  8.0,  8.5, 'aprovada em ingles'),
    (4, 10,  9.5, 10.0, 'nota maxima em artes'),
    (4, 11,  7.0,  6.5, 'aprovada em filosofia'),

    -- aluno 5 (daniel) — misto com pendente
    (5,  1,  3.0,  4.0, 'reprovado - muitas faltas'),
    (5,  2,  7.0,  6.5, 'aprovado em portugues'),
    (5,  3,  4.5,  5.0, 'reprovado em historia'),
    (5,  6,  6.5,  7.5, 'aprovado em fisica'),
    (5, 11,  5.0,  4.5, 'reprovado em filosofia'),
    (5, 12,  7.0, NULL, 'aguardando segunda avaliacao de sociologia'),

    -- -----------------------------------------------
    -- turma 1o ano b  (alunos 6-10)
    -- -----------------------------------------------

    -- aluno 6 (eduarda) — notas excelentes
    (6,  1,  9.0,  8.0, 'excelente desempenho em matematica'),
    (6,  2,  9.5,  9.0, 'melhor redacao da turma'),
    (6,  3,  8.5,  9.0, 'aprovada com destaque em historia'),
    (6,  6,  9.0,  8.5, 'destaque em fisica'),
    (6,  8, 10.0,  9.5, 'nota maxima em ingles'),
    (6,  9,  8.5,  9.0, 'aprovada em educacao fisica'),

    -- aluno 7 (felipe) — sem lancamento de notas
    (7,  1, NULL, NULL, 'sem lancamento de notas'),
    (7,  2, NULL, NULL, 'sem lancamento de notas'),
    (7,  3, NULL, NULL, 'sem lancamento de notas'),

    -- aluno 8 (gabriela) — misto
    (8,  1,  5.0,  4.5, 'reprovada em matematica'),
    (8,  2,  7.0,  7.5, 'aprovada em portugues'),
    (8,  4,  6.0,  6.5, 'aprovada em ciencias'),
    (8,  7,  8.0,  7.5, 'aprovada em quimica'),

    -- aluno 9 (henrique) — reprovado na maioria
    (9,  1,  2.5,  3.5, 'reprovado em matematica'),
    (9,  3,  6.5,  7.0, 'aprovado em historia'),
    (9,  5,  4.0,  3.0, 'reprovado em geografia'),
    (9,  8,  8.0,  7.5, 'aprovado em ingles'),

    -- aluno 10 (isabela) — misto com destaque em filosofia
    (10,  1,  7.2,  7.0, 'aprovada em matematica'),
    (10,  3,  8.0,  8.5, 'aprovada em historia'),
    (10,  7,  5.0,  4.5, 'reprovada em quimica'),
    (10, 11,  9.0,  8.5, 'destaque em filosofia'),

    -- -----------------------------------------------
    -- turma 1o ano c  (alunos 11-15)
    -- -----------------------------------------------

    -- aluno 11 (joao pedro) — misto com destaque em ef
    (11,  2,  4.0,  3.0, 'reprovado em portugues'),
    (11,  4,  6.5,  7.0, 'aprovado em ciencias'),
    (11,  6,  7.5,  8.0, 'aprovado em fisica'),
    (11,  9,  9.5, 10.0, 'nota maxima em educacao fisica'),

    -- aluno 12 (larissa) — aprovada em todas
    (12,  1,  8.0,  7.5, 'aprovada em matematica'),
    (12,  5,  8.5,  9.0, 'destaque em geografia'),
    (12,  8,  7.0,  7.5, 'aprovada em ingles'),
    (12, 10,  9.0,  9.5, 'excelente em artes'),

    -- aluno 13 (matheus) — nota1 lancada, nota2 pendente
    (13,  1,  7.0, NULL, 'aguardando segunda avaliacao em matematica'),
    (13,  2,  6.5, NULL, 'aguardando segunda avaliacao em portugues'),
    (13,  3, NULL, NULL, 'sem lancamento em historia'),

    -- aluno 14 (nathalia) — misto
    (14,  2,  7.0,  7.5, 'aprovada em portugues'),
    (14,  4,  8.5,  9.0, 'aprovada em ciencias'),
    (14,  6,  5.5,  4.5, 'reprovada em fisica'),
    (14, 12,  6.0,  6.5, 'aprovada em sociologia'),

    -- aluno 15 (otavio) — misto
    (15,  1,  6.0,  6.5, 'aprovado em matematica'),
    (15,  5,  5.5,  5.0, 'reprovado em geografia'),
    (15,  8,  7.0,  7.5, 'aprovado em ingles'),
    (15, 11,  8.0,  7.5, 'aprovado em filosofia'),

    -- -----------------------------------------------
    -- turma 1o ano d  (alunos 16-20)
    -- -----------------------------------------------

    -- aluno 16 (paula) — nota maxima em matematica
    (16,  1,  9.5, 10.0, 'nota maxima em matematica'),
    (16,  2,  8.5,  9.0, 'aprovada com destaque'),
    (16,  4,  7.5,  8.0, 'aprovada em ciencias'),
    (16,  9,  8.0,  8.5, 'aprovada em educacao fisica'),

    -- aluno 17 (rafael) — misto
    (17,  1,  5.0,  4.5, 'reprovado em matematica'),
    (17,  3,  7.0,  7.5, 'aprovado em historia'),
    (17,  7,  8.0,  8.5, 'aprovado em quimica'),
    (17, 10,  9.5, 10.0, 'nota maxima em artes'),

    -- aluno 18 (sabrina) — aprovada em todas
    (18,  2,  8.5,  9.0, 'excelente em portugues'),
    (18,  4,  7.0,  7.5, 'aprovada em ciencias'),
    (18,  5,  6.5,  7.0, 'aprovada em geografia'),
    (18,  8,  9.0,  9.5, 'excelente em ingles'),

    -- aluno 19 (thiago carvalho) — reprovado em todas
    (19,  1,  4.0,  4.5, 'reprovado em matematica'),
    (19,  6,  5.0,  5.5, 'reprovado em fisica'),
    (19,  7,  4.5,  5.0, 'reprovado em quimica'),
    (19, 12,  3.0,  3.5, 'reprovado em sociologia'),

    -- aluno 20 (vitoria) — aprovada
    (20,  1,  7.0,  7.5, 'aprovada em matematica'),
    (20,  2,  6.5,  7.0, 'aprovada em portugues'),
    (20,  9,  8.5,  9.0, 'destaque em educacao fisica'),
    (20, 10,  8.0,  7.5, 'aprovada em artes'),

    -- -----------------------------------------------
    -- turma 2o ano a  (alunos 21-25)
    -- -----------------------------------------------

    -- aluno 21 (wellington) — misto
    (21,  1,  7.5,  8.0, 'aprovado em matematica'),
    (21,  3,  4.5,  5.0, 'reprovado em historia'),
    (21,  6,  8.0,  8.5, 'aprovado em fisica'),
    (21, 11,  5.0,  4.5, 'reprovado em filosofia'),

    -- aluno 22 (yasmin) — aprovada
    (22,  2,  9.0,  9.5, 'excelente em portugues'),
    (22,  4,  7.5,  8.0, 'aprovada em ciencias'),
    (22,  8,  8.5,  9.0, 'aprovada em ingles'),
    (22, 12,  6.5,  7.0, 'aprovada em sociologia'),

    -- aluno 23 (andre) — misto
    (23,  1,  6.0,  6.5, 'aprovado em matematica'),
    (23,  5,  7.0,  7.5, 'aprovado em geografia'),
    (23,  7,  5.5,  5.0, 'reprovado em quimica'),
    (23,  9,  8.5,  9.0, 'destaque em educacao fisica'),

    -- aluno 24 (beatriz) — nota1 lancada, nota2 pendente
    (24,  1,  7.0, NULL, 'aguardando segunda nota em matematica'),
    (24,  2,  8.5, NULL, 'aguardando segunda nota em portugues'),
    (24,  6,  9.0,  8.5, 'aprovada em fisica'),

    -- aluno 25 (caio) — reprovado em todas
    (25,  1,  3.5,  4.0, 'reprovado em matematica'),
    (25,  3,  4.0,  4.5, 'reprovado em historia'),
    (25,  6,  2.5,  3.0, 'reprovado em fisica'),
    (25,  7,  5.0,  4.5, 'reprovado em quimica'),

    -- -----------------------------------------------
    -- turma 2o ano b  (alunos 26-30)
    -- -----------------------------------------------

    -- aluno 26 (debora) — misto
    (26,  2,  7.0,  7.5, 'aprovada em portugues'),
    (26,  5,  6.5,  7.0, 'aprovada em geografia'),
    (26, 10,  8.0,  8.5, 'aprovada em artes'),
    (26, 11,  5.0,  4.5, 'reprovada em filosofia'),

    -- aluno 27 (eduardo) — notas altas
    (27,  1,  8.5,  9.0, 'excelente em matematica'),
    (27,  4,  7.5,  8.0, 'aprovado em ciencias'),
    (27,  6,  9.0,  8.5, 'destaque em fisica'),
    (27, 12,  7.0,  7.5, 'aprovado em sociologia'),

    -- aluno 28 (flavia) — misto
    (28,  2,  5.5,  5.0, 'reprovada em portugues'),
    (28,  3,  4.0,  4.5, 'reprovada em historia'),
    (28,  8,  7.0,  7.5, 'aprovada em ingles'),
    (28,  9, 10.0,  9.5, 'nota maxima em educacao fisica'),

    -- aluno 29 (gustavo) — misto
    (29,  1,  6.5,  7.0, 'aprovado em matematica'),
    (29,  5,  8.0,  8.5, 'aprovado em geografia'),
    (29,  7,  4.5,  5.0, 'reprovado em quimica'),
    (29, 11,  7.5,  8.0, 'aprovado em filosofia'),

    -- aluno 30 (helena) — aprovada com notas altas
    (30,  2,  9.5, 10.0, 'nota maxima em portugues'),
    (30,  4,  8.5,  9.0, 'aprovada em ciencias'),
    (30,  6,  7.0,  7.5, 'aprovada em fisica'),
    (30, 10,  9.0,  9.5, 'excelente em artes'),

    -- -----------------------------------------------
    -- turma 2o ano c  (alunos 31-35)
    -- -----------------------------------------------

    (31,  1,  4.5,  4.0, 'reprovado em matematica'),
    (31,  3,  5.5,  5.0, 'reprovado em historia'),
    (31,  8,  7.5,  8.0, 'aprovado em ingles'),

    (32,  2,  7.0,  7.5, 'aprovada em portugues'),
    (32,  5,  8.5,  9.0, 'destaque em geografia'),
    (32,  9,  9.5, 10.0, 'nota maxima em educacao fisica'),

    (33,  1,  6.5,  7.0, 'aprovado em matematica'),
    (33,  4,  5.0,  4.5, 'reprovado em ciencias'),
    (33,  6, NULL, NULL, 'sem lancamento de notas para fisica'),

    (34,  3,  8.0,  8.5, 'aprovada em historia'),
    (34,  7,  7.5,  8.0, 'aprovada em quimica'),
    (34, 11,  9.0,  9.5, 'excelente em filosofia'),

    (35,  1,  3.0,  3.5, 'reprovado em matematica'),
    (35,  2,  4.0,  4.5, 'reprovado em portugues'),
    (35, 12,  5.0,  5.5, 'reprovado em sociologia'),

    -- -----------------------------------------------
    -- turma 2o ano d  (alunos 36-40)
    -- -----------------------------------------------

    (36,  1,  7.5,  8.0, 'aprovada em matematica'),
    (36,  5,  6.5,  7.0, 'aprovada em geografia'),
    (36,  8,  9.0,  9.5, 'excelente em ingles'),

    (37,  2,  5.0,  5.5, 'reprovado em portugues'),
    (37,  6,  7.0,  7.5, 'aprovado em fisica'),
    (37, 10,  8.5,  9.0, 'aprovado em artes'),

    (38,  1,  8.5,  9.0, 'aprovada em matematica'),
    (38,  3,  7.5,  8.0, 'aprovada em historia'),
    (38,  9,  9.0,  9.5, 'excelente em educacao fisica'),

    (39,  4,  4.5,  5.0, 'reprovado em ciencias'),
    (39,  7,  6.5,  7.0, 'aprovado em quimica'),
    (39, 11,  8.0,  8.5, 'aprovado em filosofia'),

    (40,  2,  7.0,  7.5, 'aprovada em portugues'),
    (40,  5,  5.5,  5.0, 'reprovada em geografia'),
    (40, 12,  8.0,  8.5, 'aprovada em sociologia'),

    -- -----------------------------------------------
    -- turma 3o ano a  (alunos 41-45)
    -- -----------------------------------------------

    (41,  1,  9.5, 10.0, 'nota maxima em matematica'),
    (41,  3,  8.5,  9.0, 'aprovada em historia'),
    (41,  6,  7.5,  8.0, 'aprovada em fisica'),

    (42,  2,  4.0,  4.5, 'reprovado em portugues'),
    (42,  7,  5.5,  5.0, 'reprovado em quimica'),
    (42, 12,  6.5,  7.0, 'aprovado em sociologia'),

    (43,  1,  7.0,  7.5, 'aprovada em matematica'),
    (43,  5,  8.0,  8.5, 'aprovada em geografia'),
    (43,  9,  9.5, 10.0, 'nota maxima em educacao fisica'),

    (44,  3,  5.0,  5.5, 'reprovado em historia'),
    (44,  6,  4.5,  4.0, 'reprovado em fisica'),
    (44, 11,  7.5,  8.0, 'aprovado em filosofia'),

    (45,  2,  8.5,  9.0, 'aprovada em portugues'),
    (45,  4,  7.5,  8.0, 'aprovada em ciencias'),
    (45, 10,  9.0,  9.5, 'excelente em artes'),

    -- -----------------------------------------------
    -- turma 3o ano b  (alunos 46-50)
    -- -----------------------------------------------

    (46,  1,  6.0,  6.5, 'aprovada em matematica'),
    (46,  5,  7.0,  7.5, 'aprovada em geografia'),
    (46,  8,  5.5,  5.0, 'reprovada em ingles'),

    (47,  2,  9.0,  9.5, 'excelente em portugues'),
    (47,  6,  8.5,  9.0, 'aprovado em fisica'),
    (47, 12,  7.5,  8.0, 'aprovado em sociologia'),

    (48,  1,  4.5,  5.0, 'reprovada em matematica'),
    (48,  3,  7.0,  7.5, 'aprovada em historia'),
    (48,  9,  8.5,  9.0, 'aprovada em educacao fisica'),

    (49,  4,  7.5,  8.0, 'aprovado em ciencias'),
    (49,  7,  6.0,  6.5, 'aprovado em quimica'),
    (49, 11,  5.5,  5.0, 'reprovado em filosofia'),

    (50,  2,  8.0,  8.5, 'aprovada em portugues'),
    (50,  5,  9.5, 10.0, 'nota maxima em geografia'),
    (50, 10,  7.5,  8.0, 'aprovada em artes'),

    -- -----------------------------------------------
    -- turma 3o ano c  (alunos 51-55)
    -- -----------------------------------------------

    (51,  1,  5.5,  5.0, 'reprovado em matematica'),
    (51,  3,  6.5,  7.0, 'aprovado em historia'),
    (51,  8,  8.0,  8.5, 'aprovado em ingles'),

    (52,  2,  7.5,  8.0, 'aprovado em portugues'),
    (52,  6,  9.0,  8.5, 'aprovado em fisica'),
    (52, 12,  4.5,  5.0, 'reprovado em sociologia'),

    (53,  1,  8.5,  9.0, 'aprovada em matematica'),
    (53,  4,  7.0,  7.5, 'aprovada em ciencias'),
    (53,  9,  9.5, 10.0, 'nota maxima em educacao fisica'),

    (54,  3,  4.0,  4.5, 'reprovado em historia'),
    (54,  7,  5.5,  5.0, 'reprovado em quimica'),
    (54, 11,  7.0,  7.5, 'aprovado em filosofia'),

    (55,  2,  9.5, 10.0, 'nota maxima em portugues'),
    (55,  5,  8.5,  9.0, 'aprovada em geografia'),
    (55, 10,  7.5,  8.0, 'aprovada em artes'),

    -- -----------------------------------------------
    -- turma 3o ano d  (alunos 56-60)
    -- -----------------------------------------------

    (56,  1,  6.5,  7.0, 'aprovado em matematica'),
    (56,  3,  5.0,  5.5, 'reprovado em historia'),
    (56,  8,  8.0,  8.5, 'aprovado em ingles'),

    (57,  2,  7.0,  7.5, 'aprovada em portugues'),
    (57,  6,  4.5,  5.0, 'reprovada em fisica'),
    (57, 12,  9.0,  9.5, 'excelente em sociologia'),

    (58,  1,  8.0,  8.5, 'aprovado em matematica'),
    (58,  4,  7.5,  8.0, 'aprovado em ciencias'),
    (58,  9,  6.5,  7.0, 'aprovado em educacao fisica'),

    (59,  3,  5.5,  5.0, 'reprovada em historia'),
    (59,  7,  8.0,  8.5, 'aprovada em quimica'),
    (59, 11,  9.5, 10.0, 'nota maxima em filosofia'),

    (60,  2,  7.5,  8.0, 'aprovado em portugues'),
    (60,  5,  6.0,  6.5, 'aprovado em geografia'),
    (60, 10,  5.5,  5.0, 'reprovado em artes'),

    -- -----------------------------------------------
    -- alunos sem nome cadastrado COM notas (ids 61-63)
    -- nome = 'não informado' mas possuem notas → status = 0
    -- -----------------------------------------------
    (61,  1,  7.0,  7.5, 'aprovado - cadastro incompleto'),
    (61,  3,  6.5,  7.0, 'aprovado em historia - cadastro incompleto'),

    (62,  2,  5.0,  4.5, 'reprovado - cadastro incompleto'),
    (62,  6,  8.0,  8.5, 'aprovado em fisica - cadastro incompleto'),

    (63,  4,  9.0,  9.5, 'excelente em ciencias - cadastro incompleto'),
    (63,  9,  8.5,  9.0, 'aprovado em educacao fisica - cadastro incompleto');

-- alunos 64-69: nome 'não informado', sem notas → status = 1 (ativo), nenhum registro em student_subject

COMMIT;