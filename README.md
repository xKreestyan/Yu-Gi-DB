# Yu-Gi-DB

## Documentazione Progetto

**Indice:**

- [Yu-Gi-DB](#yu-gi-db)
  - [Documentazione Progetto](#documentazione-progetto)
  - [1. Introduzione e Obiettivi](#1-introduzione-e-obiettivi)
  - [2. Funzionalità Principali](#2-funzionalità-principali)
    - [2.1 Consultazione e Ricerca Carte](#21-consultazione-e-ricerca-carte)
    - [2.2 Visualizzazione Dettagliata della Carta](#22-visualizzazione-dettagliata-della-carta)
    - [2.3 Navigazione per Caratteristiche](#23-navigazione-per-caratteristiche)
    - [2.4 Gestione dei Preferiti](#24-gestione-dei-preferiti)
    - [2.5 Database Locale](#25-database-locale)
  - [3. Architettura e Tecnologie Utilizzate](#3-architettura-e-tecnologie-utilizzate)
  - [4. Interfaccia Grafica (UI/UX)](#4-interfaccia-grafica-uiux)
  - [5. API di Riferimento](#5-api-di-riferimento)
  - [6. Pattern Architetturale MVVM e Flussi di Dati](#6-pattern-architetturale-mvvm-e-flussi-di-dati)

---

## 1. Introduzione e Obiettivi

**Nome Applicazione:** Yu-Gi-DB

**Obiettivo del Progetto:**

Il progetto "Yu-Gi-DB" si pone l'obiettivo di sviluppare un'applicazione Android nativa che permetta agli utenti di consultare e navigare in maniera efficiente e intuitiva l'ampio database di carte del gioco di carte collezionabili Yu-Gi-Oh!. L'applicazione sfrutta le API pubbliche messe a disposizione dal sito [YGOPRODeck](https://ygoprodeck.com/api-guide/), una risorsa completa e aggiornata per i giocatori di Yu-Gi-Oh!.

L'intento è quello di fornire uno strumento pratico e veloce per la ricerca di informazioni specifiche sulle carte, la scoperta di nuove carte correlate e la gestione di una collezione personale di preferiti, il tutto ottimizzato per un'esperienza mobile.

---

## 2. Funzionalità Principali

L'applicazione Yu-Gi-DB offre le seguenti funzionalità chiave:

### 2.1 Consultazione e Ricerca Carte
Gli utenti possono cercare carte all'interno del database di YGOPRODeck. La funzionalità di ricerca avviene principalmente tramite il **nome** della carta. È inoltre disponibile una **ricerca avanzata** che permette di filtrare le carte in base ai seguenti criteri:
*   ID carta
*   Tipo (es. Mostro, Magia, Trappola)
*   Attributo (es. LUCE, OSCURITÀ - per i mostri)
*   Livello (per i mostri)
*   Nome del set a cui appartiene la carta
*   Codice del set a cui appartiene la carta
*   Range di ATK (valore minimo e/o massimo)
*   Range di DEF (valore minimo e/o massimo)

L'app interroga il database locale e restituisce un elenco di risultati pertinenti.

### 2.2 Visualizzazione Dettagliata della Carta
Selezionando una carta dall'elenco dei risultati di ricerca o da altre sezioni dell'app, l'utente accede a una schermata di dettaglio. Questa schermata presenta informazioni complete sulla carta selezionata, tra cui:
*   Nome della carta
*   Immagine/artwork della carta
*   Descrizione/effetto
*   Tipo (es. Mostro, Magia, Trappola)
*   Attributo (es. LUCE, OSCURITÀ, FUOCO - per i mostri)
*   Livello/Rango/Link Rating (per i mostri)
*   ATK/DEF (per i mostri)
*   Set di appartenenza (con rarità e codici)
*   Prezzi indicativi (se forniti dalle API)

### 2.3 Navigazione per Caratteristiche
Dalla schermata di dettaglio di una carta, l'utente può interagire con specifici elementi informativi per avviare una nuova ricerca contestuale. Cliccando su:
*   Attributo
*   Tipo
*   Livello
*   Set in cui la carta appare

l'applicazione visualizzerà un elenco di tutte le altre carte che condividono quella medesima caratteristica, facilitando la scoperta e l'analisi di carte correlate.

### 2.4 Gestione dei Preferiti
Gli utenti possono contrassegnare le carte di loro interesse come "preferite". Ogni carta, sia nell'elenco di ricerca che nella vista di dettaglio, presenta un'**icona a forma di cuore** per aggiungerla o rimuoverla dalla lista dei preferiti. Esiste una sezione dedicata nell'app dove l'utente può visualizzare e gestire rapidamente tutte le carte salvate, permettendo un accesso immediato alle carte più consultate.

### 2.5 Database Locale
Per migliorare le prestazioni, consentire un accesso offline ai dati precedentemente caricati e gestire i preferiti, Yu-Gi-DB implementa un database locale **SQLite**.
* **Caching e Ricerca Locale:** Le informazioni delle carte, una volta recuperate dalle API durante il popolamento iniziale, vengono memorizzate nel database locale. Questa strategia di archiviazione locale è stata adottata per aderire alle linee guida di YGOPRODeck, che raccomandano vivamente di scaricare i dati per ridurre al minimo le chiamate API. Tale approccio è fondamentale non solo per ottimizzare le prestazioni e ridurre il traffico di rete, ma anche per evitare potenziali restrizioni all'accesso API, come il blacklisting dell'indirizzo IP, imposte dal provider in caso di utilizzo eccessivo delle risorse remote. Di conseguenza, tutte le successive operazioni di ricerca e consultazione delle carte avvengono interrogando esclusivamente questo database locale, garantendo risposte rapide.
*   **Preferiti:** Lo stato di "preferito" di una carta è salvato nel database locale, garantendo la persistenza di questa informazione tra le sessioni di utilizzo dell'app.
*   **Gestione:** Il database locale è gestito tramite la Room Persistence Library, che fornisce un layer di astrazione sopra SQLite, semplificando l'accesso ai dati e garantendo la robustezza delle query.

---

## 3. Architettura e Tecnologie Utilizzate

L'applicazione è stata sviluppata seguendo le moderne pratiche di sviluppo Android:
*   **Linguaggio:** Kotlin.
*   **UI:** Interfaccia utente realizzata principalmente con **Jetpack Compose** per un approccio dichiarativo e moderno. Alcune schermate o componenti specifici potrebbero avvalersi anche di **ConstraintLayout** per la gestione di layout complessi.
*   [**Architettura**](#6-pattern-architetturale-mvvm-e-flussi-di-dati): Adottata l'architettura **MVVM (Model-View-ViewModel)** per una chiara separazione delle responsabilità tra UI, logica di presentazione e gestione dei dati, migliorando testabilità e manutenibilità.
*   **Networking:** Libreria **Volley** per la gestione delle chiamate API asincrone a YGOPRODeck.
*   **Asincronia:** **Coroutine Kotlin** per la gestione efficiente delle operazioni in background, come le chiamate di rete e le interazioni con il database.
*   **Dependency Injection:** **Hilt** per la gestione delle dipendenze all'interno dell'applicazione, semplificando la fornitura delle istanze necessarie ai vari componenti.
*   **Database Locale:** **Room Persistence Library** come ORM per SQLite.
*   **Navigazione:** Jetpack Navigation Component integrato con Compose per la gestione dei flussi di navigazione tra le schermate.

---

## 4. Interfaccia Grafica (UI/UX)

L'interfaccia grafica di Yu-Gi-DB è stata progettata per essere intuitiva, pulita e funzionale:

*   **Tema:** L'app adotta automaticamente il tema chiaro o scuro in base alle impostazioni del sistema Android dell'utente.
  
*   **Schermate Principali:**
    *   **Home/Ricerca:** Presenta una barra di ricerca e opzioni per la ricerca avanzata. I risultati sono visualizzati tramite una **`LazyVerticalGrid`**, mostrando un'anteprima delle carte (immagine e nome) per una rapida identificazione.
    *   **Dettaglio Carta:** Organizza le informazioni della carta in modo leggibile, con l'immagine in evidenza e gli elementi interattivi per la navigazione per caratteristiche ben visibili.
    *   **Preferiti:** Visualizza le carte salvate dall'utente in una **`LazyVerticalGrid`**, come nella schermata di ricerca, per un accesso e una gestione agevoli.
*   **Navigazione:** La navigazione tra le sezioni è fluida e segue le convenzioni standard di Android, gestita tramite Navigation Component.
* **User Experience:** Interfaccia gradevole e intuitiva. I caricamenti sono rapidi dopo il download iniziale dei dati.

---

## 5. API di Riferimento

L'applicazione Yu-Gi-DB si basa sulle API fornite da **YGOPRODeck**.
*   **Guida API Ufficiale:** [https://ygoprodeck.com/api-guide/](https://ygoprodeck.com/api-guide/)
*   **Formato Dati:** Le API restituiscono i dati delle carte in formato JSON.
*   **Endpoint Utilizzato e Strategia di Popolamento Iniziale:**
    *   Per il popolamento iniziale del database locale e per le funzionalità dimostrative del progetto, è stato utilizzato l'endpoint principale: `https://db.ygoprodeck.com/api/v7/cardinfo.php`.
    *   Specificamente, per limitare il volume di dati iniziali e focalizzarsi sulla dimostrazione delle funzionalità, sono state scaricate le carte appartenenti ai set **"Legend of Blue Eyes White Dragon" (LOB)** e **"Metal Raiders"**.
    *   Questo è stato ottenuto utilizzando parametri di query specifici:
        *   Per le carte in italiano del set Metal Raiders: `?cardset=Metal%20Raiders&language=it`
        *   Per le carte in inglese del set Metal Raiders: `?cardset=Metal%20Raiders`
        *   (Analogamente per il set LOB).

    * **È importante notare che, analizzando le risposte JSON fornite dall'API, soltanto il nome della carta e la sua descrizione  variano in base al parametro della lingua selezionata, mentre gli altri attributi della carta (come tipo, attributo numerico, ATK/DEF, ID, ecc.) rimangono in lingua inglese.**
    *   Questa selezione iniziale di carte è sufficiente per testare e valutare tutte le funzionalità dell'applicazione, inclusa la ricerca, la visualizzazione dei dettagli, la navigazione per caratteristiche e la gestione dei preferiti con supporto bilingue (italiano e inglese) per i dati scaricati.

---

## 6. Pattern Architetturale MVVM e Flussi di Dati
Di seguito vengono analizzati i flussi di dati principali attraverso i componenti Model, View e ViewModel in scenari specifici:


* **Flusso 1: Scaricamento Iniziale dei Dati**

    * Questo flusso si attiva tipicamente al primo avvio dell'applicazione o quando è necessario un aggiornamento completo del dataset locale.

        `InitMainScreen (View)` &harr; `CardListViewModel` &harr; `YuGiRepo` &harr; `VolleyApiClientImpl (API)`

        inoltre

        `YuGiRepo` &harr; `YuGiDAO (DB)`

* **Flusso 2: Visualizzazione Dettaglio Carta al Click**

    * Questo flusso si attiva quando l'utente seleziona una specifica carta da un elenco per visualizzarne i dettagli.

        `LargeCardView (View)`  &harr;  `CardDetailViewModel`  &harr;  `YuGiRepo`  &harr;  `YuGiDAO (DB)`

* **Flusso 3: Ricerca Carte e Gestione Preferiti**

    * Questo scenario copre le interazioni relative alla ricerca di carte con criteri specifici e alla visualizzazione/gestione della lista dei preferiti.
    
        `UI (es. InitCardsScreenView, AdvancedSearchView, FavoritesScreen) (View)`  &harr;  `{AdvancedSearchViewModel, CardListViewModel, FavoritesViewModel}`  &harr;  `YuGiRepo`  &harr;  `YuGiDAO (DB)`
---


