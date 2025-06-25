# ing-sw-2025-gioia-marra-liu-gulli

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

Per avviare il gioco è necessario avere installata sul proprio dispositivo una versione di Java successiva alla 23


#### Avvio tramite Jar

  Dopo aver scaricato la cartella del progetto è possibile avviare i Jar
  aprendo il terminale nella cartella "Jar" ed eseguendo i seguenti comandi:
  - Per il Server  

  
    java -Dfile.encoding=UTF-8 -jar GalaxyTrucker-Server.jar

  - Per la Tui (Interfaccia testuale)


    java -Dfile.encoding=UTF-8 -jar GalaxyTrucker-TUI.jar

  - Per la Gui (Interfaccia Grafica)


    javaw --module-path extra\openjfx\javafx-controls;extra\openjfx\javafx-fxml;extra\openjfx\javafx-graphics;extra\openjfx\javafx-base ^ --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^ -jar GalaxyTrucker-GUI.jar

abbiamo incluso nel progetto nella cartella dei Jar JavaFx per semplificare la procedura di avvio.

#### Avvio semplificato

1. Scaricare il gioco: è possibile scaricare la cartella con gli eseguibili al [LINK](cdn.oci.diubi.dev/GalaxyTrucker-Jars.zip) cdn.oci.diubi.dev/GalaxyTrucker-Jars.zip
2. Decomprimere la cartella appena scaricata
3. Navigando nella cartella extra è possibile avviare Server, Tui o Gui semplicemente facendo click doppio. 

    
### Copertura dei test:


### Comandi e funzionalità automatiche:

Tutti i menu e la maggior parte delle interazioni sono da effettuare con il tasto sinistro del mouse

- Fase di Costruzione:
  - Prendere e piazzare le tessere con click sinistro
  - Ruotare una tessera con Q ed E oppure con il tasto destro del Mouse
- Fase di cotrollo della nave:
  - Eliminare le tessere non valide con click sinistro
- Fase di volo:
  - Prendere e piazzare le merci con click sinistro
  - Per attivare un componente prendere un segnalino batteria con click sinistro e mentre lo si ha in mano cliccare sul componente che si desidera attivare.
  - Selezionare la navicella che si desidera visualizzare tramite il menu laterale, durante il gioco in automatico viene sempre mostrata l'ultima navicella che ha subito dei cambiamenti per effetto delle carte. E' sempre possibile tornare a visualizzare la propria navicella selezionandola. 
   

