DROP DATABASE IF EXISTS Test_EduQuest;
CREATE DATABASE Test_EduQuest;
USE Test_EduQuest;
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
    numeroDomande INTEGER NOT NULL,
    dataCreazione DATE NOT NULL,
    docenteID_FK INTEGER NOT NULL,
    PRIMARY KEY(questionarioID),
    CONSTRAINT FK_DocenteQuestionario FOREIGN KEY (docenteID_FK) REFERENCES accounts(accountID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE compitini (
    questionarioID_FK INTEGER NOT NULL,
    dataFine DATE NOT NULL,
    tentativiMax INTEGER DEFAULT 1,
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
    numeroRisposte INTEGER NOT NULL,
    questionarioID_FK INTEGER,
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
    completato BOOLEAN DEFAULT FALSE,
    -- Mappa il boolean completato
    punteggio INTEGER DEFAULT 0,
    -- Mappa int punteggio
    numeroDomande INTEGER,
    -- Mappa int numeroDomande (anche se ridondante rispetto a questionari)
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
-- Dati di test Account
INSERT INTO accounts (nome, cognome, userName, email, password, tipo)
VALUES (
        'Pinco',
        'Pallo',
        'PincoPallino1',
        'pincopallo@prova.edu',
        'PasswordValida1!',
        'Studente'
    ),
    (
        'Franco',
        'Rossi',
        'FrancoRossi2',
        'francorossi@prova.edu',
        'PasswordValida2!',
        'Docente'
    ),
    (
        'Maria',
        'Verdi',
        'MariaV88',
        'm.verdi@prova.edu',
        'PasswordValida3!',
        'Docente'
    );
INSERT INTO studenti (accountID_FK, mediaPunteggio)
VALUES (1, 0.0);
INSERT INTO docenti (accountID_FK, insegnamento)
VALUES (2, 'Matematica e Fisica'),
    (3, 'Lettere Classiche');
-- Dati di test Questionari
-- Questionari
INSERT INTO questionari (
        nome,
        descrizione,
        materia,
        livelloDiff,
        numeroDomande,
        dataCreazione,
        docenteID_FK
    )
VALUES (
        'Geometria piana',
        'Test sui triangoli e poligoni',
        'Matematica',
        'Facile',
        1,
        '2025-09-11',
        2
    ),
    (
        'Analisi del testo',
        'Prova di comprensione',
        'Italiano',
        'Medio',
        1,
        '2025-06-05',
        2
    ),
    (
        'Simbolismo',
        'La nebbia agli irti colli',
        'Letteratura',
        'Difficile',
        1,
        '2026-01-24',
        3
    ),
    (
        'Algebra Lineare',
        'Matrici e sistemi',
        'Matematica',
        'Difficile',
        1,
        '2026-02-01',
        2
    );
INSERT INTO compitini (questionarioID_FK, dataFine, tentativiMax)
VALUES (3, '2026-02-28', 1),
    -- Scade a fine mese, 1 solo tentativo
    (4, '2026-03-15', 3);
-- Domande
INSERT INTO domande (tipo, testo, numeroRisposte, questionarioID_FK)
VALUES (1, 'Somma angoli interni triangolo?', 3, 1),
    -- ID 1 (Quest 1)
    (1, 'Chi ha scritto i Promessi Sposi?', 2, 2),
    -- ID 2 (Quest 2)
    (1, 'In che raccolta è "X Agosto"?', 3, 3),
    -- ID 3 (Quest 3)
    (
        3,
        'Una matrice quadrata è sempre invertibile?',
        2,
        4
    );
INSERT INTO risposte (testo, isCorretta, domandaID_FK)
VALUES ('180°', TRUE, 1),
    ('90°', FALSE, 1),
    ('360°', FALSE, 1),
    -- Risposte per Domanda 1
    ('Manzoni', TRUE, 2),
    ('Dante', FALSE, 2),
    -- Risposte per Domanda 2
    ('Myricae', TRUE, 3),
    ('Alcyone', FALSE, 3),
    ('Canti', FALSE, 3),
    -- Risposte per Domanda 3
    ('Vero', FALSE, 4),
    ('Falso', TRUE, 4);
-- Risposte per Domanda 4
-- Compilazioni
INSERT INTO compilazioni (
        studenteID_FK,
        questionarioID_FK,
        completato,
        punteggio,
        numeroDomande
    )
VALUES (1, 1, TRUE, 1, 1);
INSERT INTO compilazioni_risposte (compilazioneID_FK, rispostaID_FK)
VALUES (1, 1);