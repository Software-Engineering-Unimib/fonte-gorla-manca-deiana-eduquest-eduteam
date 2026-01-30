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
    numeroDomande INTEGER NOT NULL,
    dataCreazione DATE NOT NULL,
    docenteID_FK INTEGER NOT NULL,
    PRIMARY KEY(questionarioID),
    CONSTRAINT FK_DocenteQuestionario FOREIGN KEY (docenteID_FK) REFERENCES accounts(accountID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;

CREATE TABLE domande (
    domandaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    numeroRisposte INTEGER NOT NULL,
    rispostaCorrettaID INTEGER,
    questionarioID_FK INTEGER,
    PRIMARY KEY(domandaID),
    CONSTRAINT FK_QuestionarioDiOrigine FOREIGN KEY (questionarioID_FK) REFERENCES questionari(questionarioID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;

CREATE TABLE risposte (
    rispostaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    domandaID_FK INTEGER,
    PRIMARY KEY (rispostaID),
    CONSTRAINT FK_DomandaDiOrigine FOREIGN KEY (domandaID_FK) REFERENCES domande(domandaID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;

ALTER TABLE domande
ADD CONSTRAINT FK_RispostaCorretta FOREIGN KEY (rispostaCorrettaID) REFERENCES risposte(rispostaID) ON DELETE SET NULL ON UPDATE CASCADE;

-- Dati di test Account
INSERT INTO accounts (nome, cognome, userName, email, password, tipo)
VALUES 
    ('pinco', 'pallo', 'PincoPallino1', 'PincoPallo@prova.edu', 'PasswordValida1!', 'Studente'),
    ('Franco', 'Rossi', 'FrancoRossi2', 'FrancoRossi@prova.edu', 'PasswordValida2!', 'Docente');

INSERT INTO studenti (accountID_FK, mediaPunteggio)
VALUES 
    (1, 0.0);

INSERT INTO docenti (accountID_FK, insegnamento)
VALUES 
    (2, NULL);

-- Dati di test Questionari
INSERT INTO questionari (nome, descrizione, numeroDomande, dataCreazione, docenteID_FK)
VALUES 
    ('Matematica', 'Prova di matematica', 5, '2001-09-11', 2),
    ('Italiano', 'Prova di italiano', 3, '2004-06-05', 2),
    ('Simbolismo', 'La nebbia agli irti colli piovigginando sale', 1, '2026-01-24', 2);
