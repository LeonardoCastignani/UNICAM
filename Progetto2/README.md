<h2 align="center">
  Università degli Studi di Camerino<br>
  Scuola di Scienze e Tecnologie<br>
  Corso di Laurea in Informatica<br>
  Corso di Algoritmi e Strutture Dati 2024/2025<br>
  Parte di Laboratorio (6 CFU)<br>
  <br>
  Istruzioni per la realizzazione del Progetto 2<br>
  <br>
  P2: Implementazione di Grafi e Strutture Dati per lavorare su grafi 
</h2>

---

## **Grafo non orientato con matrice di adiacenza**
La classe `AdjacencyMatrixUndirectedGraph<L>` rappresenta un grafo non orientato utilizzando una **matrice di adiacenza dinamica**:
- Gli archi sono memorizzati come oggetti (`GraphEdge<L>`).
- La matrice si ridimensiona automaticamente in caso di inserimento o cancellazione di nodi.
- Gli indici dei nodi seguono l'ordine di inserimento e vengono riciclati in caso di cancellazione.

---

## **Insiemi disgiunti con foreste**
La classe `ForestDisjointSets<E>` gestisce una collezione di insiemi disgiunti utilizzando **foreste di alberi**:
- Ogni insieme è rappresentato da un albero, con nodi che contengono:
  - Un puntatore al genitore (`parent`).
  - Un campo `rank` (limite superiore all'altezza del sottoalbero).
- **Operazioni principali**:
  - **makeSet(x)**: Crea un nuovo insieme disgiunto.
  - **findSet(x)**: Trova il rappresentante dell'insieme a cui appartiene `x`, applicando la **compressione del cammino**.
  - **union(x, y)**: Unisce due insiemi, utilizzando l'**unione per rango**.

---

## **Calcolo delle componenti connesse**
Un algoritmo implementato nella classe `UndirectedGraphConnectedComponentsComputer<L>` calcola le **componenti connesse** di un grafo non orientato:
- Utilizza la struttura degli insiemi disgiunti per determinare i nodi raggiungibili tra loro.

---

## **Algoritmo di Kruskal per l'albero minimo ricoprente (MST)**
L'algoritmo di Kruskal, implementato nella classe `KruskalMSP<L>`, calcola l'**albero minimo ricoprente** in un grafo pesato:
- Utilizza la classe `ForestDisjointSets` per gestire gli insiemi disgiunti.
- Ordina gli archi in base al peso per costruire l'albero.

---

## **Riferimenti teorici**
Il progetto si basa sui concetti presentati nel libro **"Introduzione agli algoritmi"** di Cormen, Leiserson, Rivest e Stein:
- **Capitolo 21**: Insiemi disgiunti e loro implementazione con foreste.
- **Capitolo 23**: Algoritmi golosi per il calcolo dell'albero minimo ricoprente.

---

## **Punti chiave**
- **Matrice di adiacenza dinamica**: Rappresentazione efficiente di grafi non orientati con archi memorizzati come oggetti.
- **Foreste di alberi per insiemi disgiunti**: Gestione ottimizzata tramite euristiche di unione per rango e compressione del cammino.
- **Componenti connesse**: Calcolo delle componenti di un grafo utilizzando insiemi disgiunti.
- **Algoritmo di Kruskal**: Costruzione di un albero minimo ricoprente in grafi pesati, con ordinamento degli archi e gestione degli insiemi disgiunti.
- **Efficienza**: Le strutture e gli algoritmi sono progettati per garantire prestazioni ottimali in termini di tempo e spazio.
