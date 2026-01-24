CREATE DATABASE Test_Questionario;
USE Test_Questionario;

CREATE TABLE questionari(
	
    questionarioID INTEGER auto_increment,
    nome VARCHAR(40) NOT NULL,
    descrizione VARCHAR(500) NOT NULL,
    numeroDomande INTEGER NOT NULL,
    dataCreazione DATE NOT NULL,
    PRIMARY KEY(questionarioID)
) ENGINE=INNODB;

CREATE TABLE domande(

	domandaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    numeroRisposte INTEGER NOT NULL,
    rispostaCorretta INTEGER,
    questionario INTEGER,
    
	CONSTRAINT PK_Domanda PRIMARY KEY (domandaID,questionario),
    
    CONSTRAINT FK_QuestionarioDiOrigine FOREIGN KEY (questionario)
    REFERENCES questionari(questionarioID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    
) ENGINE=INNODB;

CREATE TABLE risposte(

	rispostaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    domanda INTEGER,
    
	CONSTRAINT PK_Risposta PRIMARY KEY (rispostaID,domanda),

    CONSTRAINT FK_DomandaDiOrigine FOREIGN KEY (domanda)
    REFERENCES domande(domandaID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=INNODB;

ALTER TABLE domande ADD CONSTRAINT FK_RispostaCorretta FOREIGN KEY (rispostaCorretta) REFERENCES risposte(rispostaID) ON DELETE SET NULL ON UPDATE CASCADE;

INSERT INTO questionari (nome, descrizione, numeroDomande, dataCreazione) VALUES
('Matematica', 'Prova di matematica', '5', '2001-09-11'),
('Italiano', 'Prova di italiano', '3', '2004-06-05'),
('Simbolismo', 'La nebbia agli irti colli piovigginando sale', '1', '2026-01-24');
