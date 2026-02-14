[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Q0FUR8_P)

# 🚀 EduQuest - Guida all'Installazione, Configurazione e Analisi

Seguire questi passaggi per configurare l'ambiente di sviluppo e avviare il software sulla propria macchina locale.

---

## 🛠️ Setup del Database (MySQL)

1. **Installare MySQL Workbench**: Se non lo si ha già, è possibile scaricarlo da [qui](https://dev.mysql.com/downloads/workbench/).
2. **Creare la Connessione**:
   - **Host**: `localhost:3306`
   - **Username**: `root`
   - **Password**: `toor`

    ⚙️ **Personalizzazione Credenziali** > Se si desidera utilizzare credenziali diverse, modificare il file: `EduQuest\src\main\java\dev\eduteam\eduquest\repositories\ConnectionSingleton.java`
    I campi modificabili sono:
    ```java
    Esempio di configurazione nel ConnectionSingleton.java
    ds.setPortNumber(3306);     // Inserire la porta del server MySQL
    ds.setUser("root");         // Inserire l'utente con privilegi
    ds.setPassword("toor");     // Inserire la password della connessione
    ```

3. **Inizializzare il Database**:
   - Aprire il proprio IDE e importare il progetto **EduQuest**.
   - Localizzare il file: `fonte-gorla-manca-deiana-eduquest-eduteam\EduQuest\DB_EduQuest.sql`.
   - Copiare il contenuto del file e incollarlo in una nuova query tab su MySQL Workbench all'interno della connessione appena creata.
   - Cliccare sull'icona del **fulmine** (Execute) per generare lo schema e i dati.

---

## 💻 Avvio dell'Applicazione

1. **Preparazione IDE**:
   - Assicurarsi di avere installata l'estensione **Spring Boot Dashboard** (o simili).
2. **Esecuzione**:
   - Avviare il progetto tramite la Dashboard di Spring Boot.
   - Il server sarà attivo su: `http://localhost:8080`.
3. **Accesso**:
   - Aprire un browser e navigare all'indirizzo [http://localhost:8080](http://localhost:8080).

---

## 🔍 Analisi Statica con SonarQube

Per eseguire l'analisi della qualità del codice, seguire questa procedura:

### 1. Configurazione Iniziale
* Scaricare SonarQube da [sonarqube.org](http://www.sonarqube.org/downloads/).
* Avviare il server locale di SonarQube.
* Accedere alla dashboard: [http://localhost:9000](http://localhost:9000).
* **Nota:** Al primo accesso, modificare le credenziali di default.

### 2. Creazione Progetto
* Cliccare su **Create Project** > **Local Project**.
* Impostare `Eduquest` sia come **Project display name** che come **Project Key**.
* Proseguire cliccando **Next**.
* In "Set up new code", selezionare **Follows the instance's default** e cliccare **Create Project**.
* Selezionare il metodo di analisi **Locally** e generare un **Token** (Da salvare in un posto sicuro).

### 3. Esecuzione Analisi
Scegliere uno dei due metodi seguenti dal terminale del proprio IDE (o eseguire una Macro se il proprio IDE lo permette):

#### **Metodo 1: Maven Globale**
```bash
mvn sonar:sonar "-Dsonar.token=[TOKEN_GENERATO_PRECEDENTEMENTE]"
```
#### **Metodo 2: Maven Globale**
Aprire un nuovo terminale, per poi porsi nella directory `\Eduquest` ed eseguire il comando:
```bash
.\mvnw.cmd sonar:sonar "-Dsonar.token=[TOKEN_GENERATO_PRECEDENTEMENTE]"
```
Nel caso si utilizzi **MacOS** o **Linux** come sistema operativo, il comando da eseguire differisce leggermente dal precedente:
```bash
.\mvnw sonar:sonar "-Dsonar.token=[TOKEN_GENERATO_PRECEDENTEMENTE]"
```
💡 **Suggerimento:** Dopo l'invio del comando, attendere circa 20 secondi e ricaricare la pagina affinché i risultati appaiano sulla Dashboard di SonarQube.