<h2 align="center">
  Università degli Studi di Camerino<br>
  Scuola di Scienze e Tecnologie<br>
  Corso di Laurea in Informatica<br>
  Corso di Laurea in Informatica per la Comunicazione Digitale<br>
  Corso di Algoritmi e Strutture Dati 2024/2025<br>
  Parte di Laboratorio (6 CFU)<br>
  <br>
  Istruzioni per la realizzazione del Progetto 1<br>
  <br>
  P1: Implementazione e Validazione di un Sistema Basato su Alberi di Merkle
</h2>

---

### **Merkle Tree**
Un **Merkle Tree** è una struttura dati ad albero binario utilizzata per garantire l'integrità e l'autenticità di un insieme di dati. È composto da nodi, ciascuno dei quali contiene un valore hash:
- **Foglie**: Rappresentano gli hash dei dati originali.
- **Nodi intermedi**: Rappresentano gli hash ottenuti combinando gli hash dei nodi figli (sinistro e destro).
- **Hash root (radice)**: È il nodo più in alto dell'albero, ottenuto combinando gli hash intermedi.

#### **Esempio di Merkle Tree**
1. Si calcolano gli hash delle foglie (es. `H1 = Hash(D1)`, `H2 = Hash(D2)`, ecc.).
2. Si combinano gli hash delle foglie per ottenere gli hash intermedi (es. `H12 = Hash(H1 + H2)`).
3. Si calcola l'hash root (es. `Hroot = Hash(H12 + H34)`).

---

### **Merkle Proof**
Una **Merkle Proof** è una sequenza di hash che permette di verificare se un dato appartiene a un Merkle Tree senza dover esaminare tutti i dati. La verifica avviene ricostruendo l'hash root a partire dal dato e dagli hash forniti nella proof, confrontandolo con la radice dell'albero.

#### **Esempio di Merkle Proof**
1. Si fornisce l'hash del dato da verificare (es. `H1`) e gli hash necessari per risalire alla radice (es. `H2` e `H34`).
2. Si combinano gli hash per ricostruire l'hash root (es. `H12 = Hash(H1 + H2)`, `Hroot = Hash(H12 + H34)`).
3. Se l'hash root calcolato coincide con quello dell'albero, il dato è valido.

---

### **Merkle Tree con numero di nodi non potenza di 2**
Quando il numero di nodi foglia non è una potenza di 2, si adotta una strategia per gestire i nodi singoli:
- Si ricalcola l'hash del nodo singolo (es. `Hash(Hash(E))`).
- Si procede combinando gli hash come al solito.

---

### **Vantaggi di Merkle Tree e Merkle Proof**
1. **Efficienza**: Verifica rapida dei dati con un numero ridotto di hash.
2. **Integrità**: Rilevazione immediata di manipolazioni.
3. **Scalabilità**: Adatto a insiemi di dati molto grandi.

---

### **Applicazioni**
- **Blockchain**: Verifica delle transazioni.
- **Sistemi di file distribuiti**: Verifica dell'integrità dei file.
- **Database**: Garantire che i dati non siano stati alterati.

---

### **Nota sull'hashing**
Il progetto utilizza l'algoritmo **MD5** per il calcolo degli hash:
- **MD5**: Produce un hash di 128 bit (16 byte) rappresentato come una stringa esadecimale di 32 caratteri.
- **Metodi**:
  - `dataToHash(Object data)`: Calcola l'hash di un oggetto.
  - `computeMD5(byte[] input)`: Calcola l'hash di un array di byte.
- **Limiti**: MD5 è vulnerabile a collisioni e non è sicuro per applicazioni crittografiche moderne. Alternative più sicure includono SHA-256.

---

### **Classi e Componenti del Progetto**
1. **HashUtil**: Fornisce metodi per il calcolo degli hash MD5.
2. **HashLinkedList**: Una lista concatenata che gestisce gli hash MD5 degli elementi.
3. **MerkleNode**: Rappresenta un nodo in un Merkle Tree (foglia o nodo intermedio).
4. **MerkleProof**: Gestisce le prove di Merkle per verificare l'appartenenza di un dato o branch all'albero.
5. **MerkleTree**: Rappresenta e gestisce un albero di Merkle completo, con metodi per la validazione dei dati e la generazione di prove.

---

### **Scopo del Progetto**
Implementare un sistema basato su Merkle Tree per garantire l'integrità e l'autenticità dei dati in un insieme più ampio, utilizzando le classi sopra descritte.
