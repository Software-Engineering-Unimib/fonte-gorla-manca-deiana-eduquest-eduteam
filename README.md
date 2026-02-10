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


** 2. Esecuzione dell'analisi (Scegliere uno dei due metodi): **
Metodo 1 (Maven globale):
```bash
mvn sonar:sonar "-Dsonar.token=sqp_297bf92638a2f554246f2f3d9184e84a4bfac9c3" 
```

Metodo 2 (Maven globale):
```bash
.\mvnw.cmd sonar:sonar "-Dsonar.token=sqp_297bf92638a2f554246f2f3d9184e84a4bfac9c3"
```