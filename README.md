# ing-sw-2025-gioia-marra-liu-gulli

Voto: 30L/30

Prova finale del corso di ingegneria del software - Galaxy Trucker in Java.

### Componenti del Gruppo:

- Filippo Gulli 
- Daniele Junjie Liu
- Alessandro Giuseppe Gioia
- Mattia Marra

### Funzionalità Implementate:
 
- Regole complete
- TUI (Interfaccia Testuale)
- GUI (Interfaccia Grafica)
- RMI 
- Socket
- 2 Funzionalità avanzate:
  - Volo di prova
  - Partite Multiple

### Istruzioni di Avvio:

Per avviare il gioco è necessario avere installata sul proprio dispositivo una versione di [Java JDK](https://jdk.java.net/24/) superiore o uguale alla 23
e bisogna assicurarsi che venga utilizzata per l'esecuzione la versione corretta in caso ne sia installata più di una.


#### Avvio tramite Jar

  Dopo aver scaricato la cartella del progetto è possibile avviare i Jar
  aprendo il terminale nella cartella <code>deliverables/final/jar</code> ed eseguendo i seguenti comandi:
  - Per il Server [Windows \ Unix]  


    ```java -Dfile.encoding=UTF-8 -jar GalaxyTrucker-Server.jar```

  - Per la Tui (Interfaccia testuale) [Windows \ Unix]


    ```java -Dfile.encoding=UTF-8 -jar GalaxyTrucker-TUI.jar```

  - Per la Gui (Interfaccia Grafica) [Windows]


    ```javaw --module-path extra\win\openjfx-win\javafx-controls;extra\win\openjfx-win\javafx-fxml;extra\win\openjfx-win\javafx-graphics;extra\win\openjfx-win\javafx-base --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar GalaxyTrucker-GUI.jar```

  - Per la Gui (Interfaccia Grafica) [Unix]


    ```java --module-path extra/unix/openjfx-unix/javafx-controls:extra/unix/openjfx-unix/javafx-fxml:extra/unix/openjfx-unix/javafx-graphics:extra/unix/openjfx-unix/javafx-base --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar GalaxyTrucker-GUI.jar```
  
Sono stati inclusi nella cartella <code>extra/\<platform\>/openjfx-\<platform></code> i Jar di JavaFX delle rispettive piattaforme per semplificare la procedura di avvio.
Per Unix potrebbe essere necessario regolare i permessi di lettura delle cartelle.

#### Avvio semplificato

1. Scaricare il gioco: è possibile scaricare la cartella con gli eseguibili al seguente [link](https://cdn.oci.diubi.dev/GalaxyTrucker-Jars.zip) 
2. Decomprimere la cartella appena scaricata
3. Per Windows, navigando nella cartella <code>extra/win</code> è possibile avviare Server, Tui o Gui semplicemente facendo click doppio sui file <code>.bat</code>. 
4. Per Unix, è possibile avviare i file <code>.sh</code> da terminale eseguendo i comandi <code>./launchserver.sh</code>, <code>./launchtui.sh</code>, <code>./launchgui.sh</code> nella cartella <code>extra/unix</code>. Potrebbe essere necessario regolare i permessi di esecuzione con il comando <code>chmod +x launch*.sh</code>.

    
### Copertura dei test:
![image](https://github.com/user-attachments/assets/492a3f77-3659-44c4-bab4-b9c2f5095f7d)

Come mostrato nell'immagine, i test coprono l'85% delle linee della parte model e l'81% delle linee della parte controller, pertanto si può affermare che le componenti model e controller del progetto sono state testate in modo approfondito.

Note:
1. È stata creata una serie di classi di mock per facilitare il testing del controller, come ad esempio MockShipFactory, che consente di simulare navi realistiche già costruite, oppure MockResponse per simulare le risposte dell’utente.
2. È stata implementata una classe chiamata GameTestHelper, che consente di simulare l'intero flusso di gioco fino alla fase di volo (FlightPhase) o alla fase di costruzione (BuildingPhase), così da testare comportamenti realistici in uno scenario completo.
3. È stato simulato anche un FakeClientHandler per emulare l'invio dei messaggi, permettendo così di testare l'interazione tra client e server senza una connessione reale
4. Gli effetti delle carte sono testati in CardEffectTest, simulando il gioco e verificando che ogni carta attivi correttamente il proprio effetto.
### Comandi e funzionalità automatiche:

Tutti i menu e la maggior parte delle interazioni sono da effettuare con il tasto sinistro del mouse

- Fase di Costruzione:
  - Prendere e piazzare le tessere con click sinistro
  - Ruotare una tessera con Q ed E oppure con il tasto destro del Mouse
  - I timer (clessidre) sono stati ridotti a 10 secondi per facilitare le demo
- Fase di controllo della nave:
  - Eliminare le tessere non valide con click sinistro
- Fase di volo:
  - Prendere e piazzare le merci con click sinistro
  - Per attivare un componente prendere un segnalino batteria con click sinistro e mentre lo si ha in mano cliccare sul componente che si desidera attivare.
  - Selezionare la navicella che si desidera visualizzare tramite il menu laterale, durante il gioco in automatico viene sempre mostrata l'ultima navicella che ha subito dei cambiamenti per effetto delle carte. È sempre possibile tornare a visualizzare la propria navicella selezionandola. 
   

