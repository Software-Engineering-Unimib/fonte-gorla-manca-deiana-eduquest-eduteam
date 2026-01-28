CREATE DATABASE Test_Questionario;
USE Test_Questionario;
CREATE TABLE questionari(
    questionarioID INTEGER auto_increment,
    nome VARCHAR(40) NOT NULL,
    descrizione VARCHAR(500) NOT NULL,
    numeroDomande INTEGER NOT NULL,
    dataCreazione DATE NOT NULL,
    PRIMARY KEY(questionarioID)
) ENGINE = INNODB;
CREATE TABLE domande(
    domandaID INTEGER auto_increment,
    -- ad ogni intero di tipo corrisponderà una tipologia di domanda, sono state lasciate "posizioni vuote" per possibili successive implementazioni
    tipo TINYINT(8) NOT NULL,
    -- 1-Multipla, 2=Multipla-Risposta, 3=Vero/Falso
    testo VARCHAR(500) NOT NULL,
    numeroRisposte INTEGER NOT NULL,
    questionarioID_FK INTEGER,
    -- domandaID è auto_increment quindi unica in tabella, includere questionarioID_FK è ridondante se ce l'ho come FK
    PRIMARY KEY(domandaID),
    CONSTRAINT FK_QuestionarioDiOrigine FOREIGN KEY (questionarioID_FK) REFERENCES questionari(questionarioID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
CREATE TABLE risposte(
    rispostaID INTEGER auto_increment(3),
    testo VARCHAR(500) NOT NULL,
    isCorretta BOOLEAN DEFAULT FALSE,
    domandaID_FK INTEGER,
    PRIMARY KEY (rispostaID),
    CONSTRAINT FK_DomandaDiOrigine FOREIGN KEY (domandaID_FK) REFERENCES domande(domandaID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = INNODB;
ALTER TABLE domande
ADD CONSTRAINT FK_RispostaCorretta FOREIGN KEY (rispostaCorrettaID) REFERENCES risposte(rispostaID) ON DELETE
SET NULL ON UPDATE CASCADE;
INSERT INTO questionari (nome, descrizione, numeroDomande, dataCreazione)
VALUES (
        'Questionario Fantasma',
        'Questionario creato per scopi di debug',
        '0',
        '2000-01-01'
    ),
    (
        'Italiano',
        'Prova di italiano',
        '3',
        '2004-06-05'
    ),
    (
        'Matematica',
        'Prova di matematica',
        '3',
        '2003-02-07'
    ),
    (
        'Simbolismo',
        'La nebbia agli irti colli piovigginando sale',
        '1',
        '2026-01-24'
    );
INSERT INTO domande (
        tipo,
        testo,
        numeroRisposte,
        rispostaVera,
        questionarioID_FK
    )
VALUES(
        '0' 'Domanda fantasma',
        '0',
        '0',
        '0'
    );
INSERT INTO risposte (
        rispostaID,
        testo,
        rispostaCorretta,
        domandaID_FK
    )
VALUES(
        '0',
        'Risposta fantasma',
        '0',
        '0'
    ),
    (
        '1',
        'Vero',
        '0',
        '0'
    ),
    (
        '2',
        'Falso',
        '0',
        '0'
    );