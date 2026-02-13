DROP DATABASE IF EXISTS DB_EduQuest;
CREATE DATABASE DB_EduQuest;
USE DB_EduQuest;
-- Tabelle Account
CREATE TABLE accounts (
    accountID INTEGER auto_increment,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    userName VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tipo ENUM('Studente', 'Docente') NOT NULL,
    PRIMARY KEY(accountID)
) ENGINE = INNODB;
CREATE TABLE studenti (
    accountID_FK INTEGER NOT NULL,
    mediaPunteggio DOUBLE DEFAULT 0.0,
    eduPoints INT DEFAULT 0,
    PRIMARY KEY(accountID_FK),
    CONSTRAINT FK_AccountStudente FOREIGN KEY (accountID_FK) REFERENCES accounts(accountID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE docenti (
    accountID_FK INTEGER NOT NULL,
    insegnamento VARCHAR(100),
    PRIMARY KEY(accountID_FK),
    CONSTRAINT FK_AccountDocente FOREIGN KEY (accountID_FK) REFERENCES accounts(accountID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
-- Tabelle Questionari
CREATE TABLE questionari (
    questionarioID INTEGER auto_increment,
    nome VARCHAR(40) NOT NULL,
    descrizione VARCHAR(500) NOT NULL,
    materia VARCHAR(100) NOT NULL,
    livelloDiff ENUM('Facile', 'Medio', 'Difficile') NOT NULL,
    dataCreazione DATE NOT NULL,
    docenteID_FK INTEGER NOT NULL,
    PRIMARY KEY(questionarioID),
    CONSTRAINT FK_DocenteQuestionario FOREIGN KEY (docenteID_FK) REFERENCES accounts(accountID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE compitini (
    questionarioID_FK INTEGER NOT NULL,
    dataFine DATE NOT NULL,
    tentativiMax INTEGER DEFAULT 1,
    puntiBonus INTEGER DEFAULT 0,
    assegnatiPtBonus BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (questionarioID_FK),
    CONSTRAINT FK_QuestionarioCompitino FOREIGN KEY (questionarioID_FK) REFERENCES questionari(questionarioID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE esercitazioni (
    questionarioID_FK INTEGER NOT NULL,
    noteDidattiche VARCHAR(1000),
    PRIMARY KEY (questionarioID_FK),
    CONSTRAINT FK_QuestionarioEsercitazione FOREIGN KEY (questionarioID_FK) REFERENCES questionari(questionarioID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE domande (
    domandaID INTEGER auto_increment,
    -- ad ogni intero di tipo corrisponderà una tipologia di domanda, 
    -- sono state lasciate "posizioni vuote" per possibili successive implementazioni
    tipo TINYINT(8) NOT NULL,
    -- 1-Multipla, 2=Multipla-Risposta, 3=Vero/Falso
    testo VARCHAR(500) NOT NULL,
    questionarioID_FK INTEGER,
    punteggio INTEGER DEFAULT 1,
    PRIMARY KEY(domandaID),
    CONSTRAINT FK_QuestionarioDiOrigine FOREIGN KEY (questionarioID_FK) REFERENCES questionari(questionarioID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE feedback (
    feedbackID INTEGER AUTO_INCREMENT,
    testo VARCHAR(1000) NOT NULL,
    -- Relazione 1:1 tra Domanda e Feedback
    domandaID_FK INTEGER NOT NULL UNIQUE,
    PRIMARY KEY (feedbackID),
    CONSTRAINT FK_DomandaFeedback FOREIGN KEY (domandaID_FK) REFERENCES domande(domandaID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE risposte (
    rispostaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    isCorretta BOOLEAN DEFAULT FALSE,
    domandaID_FK INTEGER,
    PRIMARY KEY (rispostaID),
    CONSTRAINT FK_DomandaDiOrigine FOREIGN KEY (domandaID_FK) REFERENCES domande(domandaID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
-- Tabella principale per l'oggetto Compilazione
CREATE TABLE compilazioni (
    compilazioneID INTEGER AUTO_INCREMENT,
    studenteID_FK INTEGER NOT NULL,
    questionarioID_FK INTEGER NOT NULL,
    -- Mappa il boolean completato
    completato BOOLEAN DEFAULT FALSE,
    -- Mappa int punteggio
    punteggio INTEGER DEFAULT 0,
    -- Mappa int numeroDomande (anche se ridondante rispetto a questionari)
    numeroDomande INTEGER,
    PRIMARY KEY(compilazioneID),
    -- Vincoli di integrità referenziale
    CONSTRAINT FK_StudenteCompilazione FOREIGN KEY (studenteID_FK) REFERENCES studenti(accountID_FK) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_QuestionarioCompilazione FOREIGN KEY (questionarioID_FK) REFERENCES questionari(questionarioID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
-- Tabella di giunzione per mappare l'array Risposta[] risposte
CREATE TABLE compilazioni_risposte (
    compilazioneID_FK INTEGER NOT NULL,
    rispostaID_FK INTEGER NOT NULL,
    PRIMARY KEY(compilazioneID_FK, rispostaID_FK),
    CONSTRAINT FK_CompilazioneRif FOREIGN KEY (compilazioneID_FK) REFERENCES compilazioni(compilazioneID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_RispostaSelezionata FOREIGN KEY (rispostaID_FK) REFERENCES risposte(rispostaID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
-- =========================
-- Dati popolamento Account
INSERT INTO accounts (nome, cognome, userName, email, password, tipo)
VALUES (
        'Pinco',
        'Pallo',
        'PincoPallino1',
        'pincopallo@prova.edu',
        'Pass123!',
        'Studente'
    ),
    (
        'Franco',
        'Rossi',
        'FrancoRossi2',
        'francorossi@prova.edu',
        'Pass123!',
        'Docente'
    ),
    (
        'Maria',
        'Verdi',
        'MariaV88',
        'm.verdi@prova.edu',
        'Pass123!',
        'Docente'
    ),
    (
        'Luca',
        'Neri',
        'LucaN8',
        'luca.neri@prova.edu',
        'Pass123!',
        'Studente'
    ),
    (
        'Giulia',
        'Bianchi',
        'GiusyB',
        'g.bianchi@prova.edu',
        'Pass123!',
        'Studente'
    ),
    (
        'Marco',
        'Neri',
        'MarkBlack',
        'm.neri@prova.edu',
        'Pass123!',
        'Studente'
    ),
    (
        'Elena',
        'Sofia',
        'EleSofi',
        'e.sofia@prova.edu',
        'Pass123!',
        'Studente'
    );
INSERT INTO studenti (accountID_FK, mediaPunteggio, eduPoints)
VALUES (1, 50.0, 50),
    (4, 88.5, 250),
    (5, 55.0, 40),
    (6, 92.0, 310),
    (7, 74.5, 150);
INSERT INTO docenti (accountID_FK, insegnamento)
VALUES (2, 'Matematica e Fisica'),
    (3, 'Lettere Classiche');
-- =========================
-- Dati popolamento Questionari, Compitini ed Esercitazioni
INSERT INTO questionari (
        nome,
        descrizione,
        materia,
        livelloDiff,
        dataCreazione,
        docenteID_FK
    )
VALUES (
        'Geometria piana',
        'Test sui triangoli e poligoni',
        'Matematica',
        'Facile',
        '2025-09-11',
        2
    ),
    (
        'Analisi del testo',
        'Prova di comprensione',
        'Italiano',
        'Medio',
        '2025-06-05',
        2
    ),
    (
        'Simbolismo',
        'La nebbia agli irti colli',
        'Letteratura',
        'Difficile',
        '2026-01-24',
        3
    ),
    (
        'Algebra Lineare',
        'Matrici e sistemi',
        'Matematica',
        'Difficile',
        '2026-02-01',
        2
    );
INSERT INTO compitini (
        questionarioID_FK,
        dataFine,
        tentativiMax,
        puntiBonus,
        assegnatiPtBonus
    )
VALUES (3, '2026-02-28', 1, 5, FALSE),
    -- Scade a fine mese, 1 solo tentativo
    (4, '2026-03-15', 3, 10, TRUE);
INSERT INTO esercitazioni (questionarioID_FK, noteDidattiche)
VALUES (
        1,
        'Si consiglia l''uso della riga e del compasso virtuale.'
    ),
    (
        2,
        'Focus sulla parafrasi dei testi del XIX secolo.'
    );
-- =========================
-- Domande, Risposte e Feedback
INSERT INTO domande (tipo, testo, questionarioID_FK, punteggio)
VALUES -- ID 1
    (1, 'Somma angoli interni triangolo?', 1, 10),
    -- ID 2
    (1, 'Chi ha scritto i Promessi Sposi?', 2, 5),
    -- ID 3
    (1, 'In che raccolta è "X Agosto"?', 3, 15),
    -- ID 4
    (
        3,
        'Una matrice quadrata è sempre invertibile?',
        4,
        20
    ),
    -- ID 5
    (3, 'Il quadrato è un parallelogramma?', 1, 10),
    -- ID 6
    (
        2,
        'Quali sono temi tipici del Romanticismo?',
        2,
        25
    ),
    -- ID 7
    (
        3,
        'Il termine "Innominato" appare nei Promessi Sposi?',
        2,
        10
    );
INSERT INTO risposte (testo, isCorretta, domandaID_FK)
VALUES ('180°', TRUE, 1),
    ('90°', FALSE, 1),
    ('360°', FALSE, 1),
    ('Manzoni', TRUE, 2),
    ('Dante', FALSE, 2),
    ('Myricae', TRUE, 3),
    ('Alcyone', FALSE, 3),
    ('Vero', FALSE, 4),
    ('Falso', TRUE, 4),
    ('Vero', TRUE, 5),
    ('Falso', FALSE, 5),
    ('Soggettivismo', TRUE, 6),
    ('Razionalismo estremo', FALSE, 6),
    ('Ritorno alla natura', TRUE, 6),
    ('Vero', TRUE, 7),
    ('Falso', FALSE, 7);
INSERT INTO feedback (testo, domandaID_FK)
VALUES ('Corretto! La somma è sempre (n-2)*180.', 1),
    (
        'Alessandro Manzoni è il padre della lingua italiana moderna.',
        2
    ),
    (
        'Il Romanticismo privilegia l''emozione e il legame con la natura incontaminata.',
        6
    ),
    (
        'L''Innominato è uno dei personaggi più complessi del romanzo manzoniano.',
        7
    );
-- =========================
-- Compilazioni e statistiche
INSERT INTO compilazioni (
        studenteID_FK,
        questionarioID_FK,
        completato,
        punteggio,
        numeroDomande
    )
VALUES -- ID 1
    (1, 1, TRUE, 100, 2),
    -- ID 2
    (1, 2, TRUE, 0, 1),
    -- ID 3
    (4, 1, TRUE, 95, 3),
    -- ID 4 (in sospeso)
    (5, 3, FALSE, 0, 1);
INSERT INTO compilazioni_risposte (compilazioneID_FK, rispostaID_FK)
VALUES (1, 1),
    (1, 10),
    (2, 5),
    (3, 1),
    (3, 10);