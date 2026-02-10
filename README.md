[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Q0FUR8_P)


## 🔍 Configurazione SonarQube

Il progetto è pre-configurato nel file `pom.xml`. Per garantire la massima sicurezza e rispettare le segnalazioni di SonarQube relative ai "Security Hotspots", le credenziali sensibili non sono state cablate nel codice.

### 🛡️ Nota sulla Sicurezza
Il token di accesso **non è stato incluso** nel file `pom.xml`. Questo evita violazioni critiche nei report di sicurezza. L'autenticazione deve essere fornita a runtime come parametro Maven.

---

### 🚀 Istruzioni per l'Analisi Locale

Per analizzare il progetto sulla propria macchina (es. all'indirizzo `http://localhost:9000`), è necessario seguire questi passaggi dal terminale nella cartella root del progetto:

#### **Passaggio 1: Generazione report di copertura (JaCoCo)**
È fondamentale eseguire i test unitari per generare i dati di copertura necessari ad ottenere il **Rating A** nella categoria *Coverage*:
```powershell
.\mvnw.cmd clean verify 
```
---

#### **Passaggio 2: Andare all'indirizzo http://localhost:9000**
* Creare un nuovo progetto con: 
    * projectKey = Eduquest
    * projectName = Eduquest
* Ottenere il TOKEN_SONAR(salvare in un posto sicuro)

#### **Passaggio 3: Esecuzione dell'analisi (Scegliere uno dei due metodi):**
* Tornare nel proprio IDE
* Scegliere uno dei due metodi per eseguire l'analisi
Metodo 1 (Maven globale):
```bash
mvn sonar:sonar "-Dsonar.token=[TOKEN_GENERATO_PRECEDENTEMENTE]" 
```

Metodo 2 (Maven Wrapper):
* Entrare nel terminale e spostarsi nella directory: "\Eduquest"
```bash
.\mvnw.cmd sonar:sonar "-Dsonar.token=[TOKEN_GENERATO_PRECEDENTEMENTE]"
```

* Aspettare circa 20 secondi e su localhost dovrebbe comparire l'analisi