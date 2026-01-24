CREATE DATABASE Test_Questionario;
USE Test_Questionario;

CREATE TABLE questionario(
	
    questionarioID INTEGER auto_increment,
    nome VARCHAR(40) NOT NULL,
    descrizione VARCHAR(500) NOT NULL,
    numeroDomande INTEGER NOT NULL,
    dataCreazione DATE NOT NULL,
    PRIMARY KEY(questionarioID)
) ENGINE=INNODB;

CREATE TABLE domanda(

	domandaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    numeroRisposte INTEGER NOT NULL,
    rispostaCorretta INTEGER,
    questionario INTEGER,
    
	CONSTRAINT PK_Domanda PRIMARY KEY (domandaID,questionario),
    
    CONSTRAINT FK_QuestionarioDiOrigine FOREIGN KEY (questionario)
    REFERENCES questionario(questionarioID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    
) ENGINE=INNODB;

CREATE TABLE risposta(

	rispostaID INTEGER auto_increment,
    testo VARCHAR(500) NOT NULL,
    domanda INTEGER,
    
	CONSTRAINT PK_Risposta PRIMARY KEY (rispostaID,domanda),

    CONSTRAINT FK_DomandaDiOrigine FOREIGN KEY (domanda)
    REFERENCES domanda(domandaID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=INNODB;

ALTER TABLE domanda ADD CONSTRAINT FK_RispostaCorretta FOREIGN KEY (rispostaCorretta) REFERENCES risposta(rispostaID) ON DELETE SET NULL ON UPDATE CASCADE;
