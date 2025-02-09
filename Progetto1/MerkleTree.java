package it.unicam.cs.asdl2425.mp1;

import java.util.*;

/**
 * Un Merkle Tree, noto anche come hash tree binario, è una struttura dati per
 * verificare in modo efficiente l'integrità e l'autenticità dei dati
 * all'interno di un set di dati più ampio. Viene costruito eseguendo l'hashing
 * ricorsivo di coppie di dati (valori hash crittografici) fino a ottenere un
 * singolo hash root. In questa implementazione la verifica di dati avviene
 * utilizzando hash MD5.
 * 
 * @author Luca Tesei, Marco Caputo (template), CASTIGNANI LEONARDO
 * 											    leonardo.castignani@studenti.unicam.it
 *
 * @param <T>
 *                il tipo di dati su cui l'albero è costruito.
 */
public class MerkleTree<T> {
    /**
     * Nodo radice dell'albero.
     */
    private final MerkleNode root;

    /**
     * Larghezza dell'albero, ovvero il numero di nodi nell'ultimo livello.
     */
    private final int width;

    /**
     * Costruisce un albero di Merkle a partire da un oggetto HashLinkedList,
     * utilizzando direttamente gli hash presenti nella lista per costruire le
     * foglie. Si noti che gli hash dei nodi intermedi dovrebbero essere
     * ottenuti da quelli inferiori concatenando hash adiacenti due a due e
     * applicando direttmaente la funzione di hash MD5 al risultato della
     * concatenazione in bytes.
     *
     * @param hashList
     *                     un oggetto HashLinkedList contenente i dati e i
     *                     relativi hash.
     * @throws IllegalArgumentException
     *                                      se la lista è null o vuota.
     */
    public MerkleTree(HashLinkedList<T> hashList) {
    	// Controllo se la lista di hash è null o vuota
    	// e genero un'eccezione in tal caso
    	if(hashList == null || hashList.getSize() == 0)
    		throw new IllegalArgumentException("MT: parametro hashList null");

    	// Creo una lista di nodi foglia a partire dagli elementi nella lista di hash
        List<MerkleNode> nodifoglie = new ArrayList<MerkleNode>();
        // Itero per ogni elemento della lista hash
        for(T data : hashList) {
        	// Calcolo l'hash di ogni elemento della lista
            String hash = HashUtil.dataToHash(data);
            // Creo un nodo foglia con l'hash calcolato
            nodifoglie.add(new MerkleNode(hash));
        }

        // Inizio con le foglie e costruisco gli strati del Merkle Tree
        List<MerkleNode> nodi = nodifoglie;
        while(nodi.size() > 1) {
        	// Creo una nuova lista per i nodi genitori
            List<MerkleNode> nodiGenitori = new ArrayList<MerkleNode>();
            for(int i = 0; i < nodi.size(); i += 2) {
                if(i + 1 < nodi.size()) {
                	// Se ci sono due nodi, combino i loro hash
                    MerkleNode sinistra = nodi.get(i);
                    MerkleNode destra = nodi.get(i + 1);
                    String hashCombinato = HashUtil.computeMD5((sinistra.getHash() + destra.getHash()).getBytes());
                    // Creo un nodo genitore con l'hash combinato e i due figli
                    nodiGenitori.add(new MerkleNode(hashCombinato, sinistra, destra));
                }
                else {
                	// Se c'è un nodo dispari, creo un genitore con un solo figlio
                    MerkleNode figlioUnico = nodi.get(i);
                    String hashCombinato = HashUtil.computeMD5((figlioUnico.getHash() + "").getBytes());
                    // Il secondo figlio è null
                    nodiGenitori.add(new MerkleNode(hashCombinato, figlioUnico, null));
                }
            }
            // Aggiorno la lista di nodi con i genitori appena creati
            nodi = nodiGenitori;
        }
        // Imposto la radice dell'albero con il primo elemento della lista
        this.root = nodi.get(0);
        // Salvo il numero di foglie iniziali come larghezza dell'albero
        this.width = nodifoglie.size();
    }

    /**
     * Restituisce il nodo radice dell'albero.
     *
     * @return il nodo radice.
     */
    public MerkleNode getRoot() {
        return root;
    }

    /**
     * Restituisce la larghezza dell'albero.
     *
     * @return la larghezza dell'albero.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Restituisce l'altezza dell'albero.
     *
     * @return l'altezza dell'albero.
     */    
    public int getHeight() {
    	// Inizializzo l'altezza a 0
    	int height = 0;
    	// Partendo dal nodo radice dell'albero Merkle
        MerkleNode corrente = this.root;
        // Itero finché il nodo corrente ha almeno un figlio
        while(corrente.getLeft() != null || corrente.getRight() != null) {
        	// Incremento il valore dell'altezza
            height++;
            // Memorizzo il figlio sinistro se esiste, altrimenti il figlio destro
            corrente = corrente.getLeft() != null ? corrente.getLeft() : corrente.getRight();
        }
        // Restituisco l'altezza dell'albero
        return height;
    }

    /**
     * Restituisce l'indice di un dato elemento secondo l'albero di Merkle
     * descritto da un dato branch. Gli indici forniti partono da 0 e
     * corrispondono all'ordine degli hash corrispondenti agli elementi
     * nell'ultimo livello dell'albero da sinistra a destra. Nel caso in cui il
     * branch fornito corrisponda alla radice di un sottoalbero, l'indice
     * fornito rappresenta un indice relativo a quel sottoalbero, ovvero un
     * offset rispetto all'indice del primo elemento del blocco di dati che
     * rappresenta. Se l'hash dell'elemento non è presente come dato
     * dell'albero, viene restituito -1.
     *
     * @param branch
     *                   la radice dell'albero di Merkle.
     * @param data
     *                   l'elemento da cercare.
     * @return l'indice del dato nell'albero; -1 se l'hash del dato non è
     *         presente.
     * @throws IllegalArgumentException
     *                                      se il branch o il dato sono null o
     *                                      se il branch non è parte
     *                                      dell'albero.
     */
    public int getIndexOfData(MerkleNode branch, T data) {
    	// Controllo se il ramo o il dato sono null
    	// e lancio un'eccezione se lo sono
    	if(branch == null || data == null)
    		throw new IllegalArgumentException("GID: parametri branch/data null");

    	// Calcolo l'hash del dato utilizzando la funzione HashUtil
        String dataHash = HashUtil.dataToHash(data);
        // Chiamo il metodo ricorsivo per trovare l'indice del dato, partendo dall'indice 0
        return getIndexOfDataRec(branch, dataHash, 0);
    }
    
    /**
     * Ricerca ricorsivamente l'indice del nodo che contiene il dato specificato
     * in una struttura ad albero Merkle. Questo metodo esplora l'albero Merkle
     * a partire dal nodo fornito, confrontando l'hash di ciascun nodo con
     * l'hash del dato da ricercare. Se l'hash del nodo corrente corrisponde
     * all'hash del dato, viene restituito l'indice corrente.
     * Se il dato non viene trovato nel nodo corrente, il metodo continua la
     * ricerca ricorsiva nel figlio sinistro e destro del nodo. L'indice di
     * ciascun nodo figlio viene calcolato moltiplicando l'indice del nodo
     * corrente per 2 (per il figlio sinistro) e per 2 più 1
     * (per il figlio destro).
     *
     * @param nodo     il nodo corrente dell'albero Merkle in cui cercare il dato
     * 
     * @param dataHash l'hash del dato da ricercare
     * 
     * @param indice   l'indice corrente del nodo nella struttura ad albero
     * 
     * @return l'indice del nodo che contiene il dato specificato, oppure -1 se
     * 		   il dato non viene trovato
     * 
     * @throws IllegalArgumentException se il nodo fornito è null o se l'hash
     * 									del dato è null
     * 
     */
    private int getIndexOfDataRec(MerkleNode nodo, String dataHash, int indice) {
    	// Se il nodo è null, ritorno -1 per indicare che il dato non è stato trovato
        if(nodo == null) return -1;

        // Se l'hash del nodo corrisponde a quello del dato, ritorno l'indice corrente
        if(nodo.getHash().equals(dataHash)) return indice;

        // Cerco ricorsivamente nel figlio sinistro, moltiplicando l'indice per 2
        int indiceSinistro = getIndexOfDataRec(nodo.getLeft(), dataHash, indice * 2);
        // Se il dato è stato trovato nel ramo sinistro, ritorno il suo indice
        if(indiceSinistro != -1) return indiceSinistro;

        // Cerco ricorsivamente nel figlio destro, calcolando l'indice come indice * 2 + 1
        return getIndexOfDataRec(nodo.getRight(), dataHash, indice * 2 + 1);
    }

    /**
     * Restituisce l'indice di un elemento secondo questo albero di Merkle. Gli
     * indici forniti partono da 0 e corrispondono all'ordine degli hash
     * corrispondenti agli elementi nell'ultimo livello dell'albero da sinistra
     * a destra (e quindi l'ordine degli elementi forniti alla costruzione). Se
     * l'hash dell'elemento non è presente come dato dell'albero, viene
     * restituito -1.
     *
     * @param data
     *                 l'elemento da cercare.
     * @return l'indice del dato nell'albero; -1 se il dato non è presente.
     * @throws IllegalArgumentException
     *                                      se il dato è null.
     */
    public int getIndexOfData(T data) {
    	// Controllo se il dato è null
    	// e lancio un'eccezione se lo è
    	if(data == null)
    		throw new IllegalArgumentException("GID2: parametro data null");
    	
    	// Calcolo l'hash del dato utilizzando la funzione HashUtil
        String dataHash = HashUtil.dataToHash(data);
        // Chiamo il metodo ricorsivo per trovare l'indice del dato, partendo dall'indice 0
        return getIndexOfDataRec(this.root, dataHash, 0);
    }

    /**
     * Sottopone a validazione un elemento fornito per verificare se appartiene
     * all'albero di Merkle, controllando se il suo hash è parte dell'albero
     * come hash di un nodo foglia.
     *
     * @param data
     *                 l'elemento da validare
     * @return true se l'hash dell'elemento è parte dell'albero; false
     *         altrimenti.
     */
    public boolean validateData(T data) {
    	// Controllo se il dato è null
    	// e lancio un'eccezione se lo è
    	if(data == null)
    		throw new IllegalArgumentException("VD: parametro data null");
    	
    	// Calcolo l'hash del dato utilizzando la funzione HashUtil
        String dataHash = HashUtil.dataToHash(data);
        // Chiamo il metodo per la validazione ricorsiva partendo dalla radice
        return validateDataRec(this.root, dataHash);
    }

    /**
     * Verifica ricorsivamente se un dato con l'hash specificato esiste in una
     * struttura ad albero Merkle. Questo metodo esplora l'albero Merkle a
     * partire dal nodo fornito, confrontando l'hash di ciascun nodo con l'hash
     * del dato da verificare. Se l'hash del nodo corrente corrisponde all'hash
     * del dato, viene restituito true. Se il nodo corrente è null o se l'hash
     * del nodo corrente non corrisponde, il metodo continua la ricerca
     * ricorsiva nei figli sinistro e destro del nodo.
     *
     * @param nodo     il nodo corrente dell'albero Merkle in cui cercare il dato
     * 
     * @param dataHash l'hash del dato da verificare
     * 
     * @return true se l'hash del dato è trovato in un nodo dell'albero, false
     * 				altrimenti
     * 
     * @throws IllegalArgumentException se il nodo o l'hash del dato sono null
     * 
     */
    private boolean validateDataRec(MerkleNode nodo, String dataHash) {
    	// Se il nodo corrente è null, restituisco false
        if(nodo == null) return false;
        
        // Se l'hash del nodo corrente corrisponde all'hash del dato, restituisco true
        if(nodo.getHash().equals(dataHash)) return true;

        // Continuo la ricerca ricorsiva nei figli sinistro e destro
        return validateDataRec(nodo.getLeft(), dataHash) || validateDataRec(nodo.getRight(), dataHash);
    }

    /**
     * Sottopone a validazione un dato sottoalbero di Merkle, corrispondente
     * quindi a un blocco di dati, per verificare se è valido rispetto a questo
     * albero e ai suoi hash. Un sottoalbero è valido se l'hash della sua radice
     * è uguale all'hash di un qualsiasi nodo intermedio di questo albero. Si
     * noti che il sottoalbero fornito può corrispondere a una foglia.
     *
     * @param branch
     *                   la radice del sottoalbero di Merkle da validare.
     * @return true se il sottoalbero di Merkle è valido; false altrimenti.
     */
    public boolean validateBranch(MerkleNode branch) {
    	// Controllo se il branch è null
    	// e lancio un'eccezione se lo è
    	if(branch == null)
    		throw new IllegalArgumentException("VB: parametro branch null");

    	// Ottengo l'hash del nodo branch
        String branchHash = branch.getHash();
        // Chiamo il metodo per la validazione ricorsiva partendo dalla radice
        return validateBranchRec(this.root, branchHash);
    }

    /**
     * Verifica ricorsivamente se un nodo con l'hash specificato esiste in un
     * ramo dell'albero Merkle. Questo metodo esplora l'albero Merkle a partire
     * dal nodo fornito, confrontando l'hash di ciascun nodo con l'hash del ramo
     * da verificare. Se l'hash del nodo corrente corrisponde all'hash del ramo,
     * viene restituito true. Se il nodo corrente è null o se l'hash del nodo
     * corrente non corrisponde, il metodo continua la ricerca ricorsiva nei
     * figli sinistro e destro del nodo.
     *
     * @param nodo       il nodo corrente dell'albero Merkle in cui cercare
     * 					 l'hash del ramo
     * 
     * @param branchHash l'hash del ramo da verificare
     * 
     * @return true se l'hash del ramo è trovato in un nodo dell'albero,
     * 				false altrimenti
     * 
     * @throws IllegalArgumentException se il nodo o l'hash del ramo sono null
     * 
     */
    private boolean validateBranchRec(MerkleNode nodo, String branchHash) {
    	// Se il nodo corrente è null, restituisco false
        if(nodo == null) return false;

        // Se l'hash del nodo corrente corrisponde all'hash del branch, restituisco true
        if(nodo.getHash().equals(branchHash)) return true;

        // Eseguo la ricerca nel sottoalbero sinistro
        boolean validSinistra = validateBranchRec(nodo.getLeft(), branchHash);
        // Se trovo nel sottoalbero sinistro, restituisce true
        if(validSinistra) return true;

        // Altrimenti, eseguo la ricerca nel sottoalbero destro
        return validateBranchRec(nodo.getRight(), branchHash);
    }

    /**
     * Sottopone a validazione un dato albero di Merkle per verificare se è
     * valido rispetto a questo albero e ai suoi hash. Grazie alle proprietà
     * degli alberi di Merkle, ciò può essere fatto in tempo costante.
     *
     * @param otherTree
     *                      il nodo radice dell'altro albero di Merkle da
     *                      validare.
     * @return true se l'altro albero di Merkle è valido; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se l'albero fornito è null.
     */
    public boolean validateTree(MerkleTree<T> otherTree) {
    	// Controllo se l'otherTree è null
    	// e lancio un'eccezione se lo è
    	if(otherTree == null)
    		throw new IllegalArgumentException("VT: parametro otherTree null");

    	// Ottengo la radice dell'altro albero
        MerkleNode altraRoot = otherTree.getRoot();
        // Chiamo il metodo per la validazione ricorsiva partendo dalla radice
        return validateTreeRec(this.root, altraRoot);
    }

    /**
     * Valida ricorsivamente due alberi Merkle verificando se hanno la stessa
     * struttura e gli stessi hash. Questo metodo verifica se due alberi Merkle
     * sono identici a partire dai nodi forniti. Un albero è considerato valido
     * rispetto all'altro se entrambi i nodi in ciascuna posizione
     * corrispondente hanno lo stesso hash e la stessa struttura. Il metodo
     * utilizza un approccio ricorsivo per confrontare ogni coppia di nodi.
     *
     * @param nodo      il nodo corrente del primo albero Merkle
     * 
     * @param altroNodo il nodo corrente del secondo albero Merkle
     * 
     * @return true se gli alberi sono identici in termini di struttura e hash,
     * 				false altrimenti
     * 
     * @throws IllegalArgumentException se uno dei nodi forniti è null
     * 
     */
    private boolean validateTreeRec(MerkleNode nodo, MerkleNode altroNodo) {
    	// Caso base:
    	// entrambi i nodi sono null, l'albero è valido in questa posizione
        if(nodo == null && altroNodo == null) return true;
        // Caso base:
        // uno dei due nodi è null, l'albero non è valido
        if(nodo == null || altroNodo == null) return false;

        // Verifico se gli hash dei due nodi non corrispondono
        if(!nodo.getHash().equals(altroNodo.getHash())) return false;

        // Valido ricorsivamente i sotto-alberi sinistro e destro
        boolean validSinistra = validateTreeRec(nodo.getLeft(), altroNodo.getLeft());
        boolean validDestra = validateTreeRec(nodo.getRight(), altroNodo.getRight());

        // L'albero è valido se entrambi i sotto-alberi sono validi
        return validSinistra && validDestra;
    }

    /**
     * Trova gli indici degli elementi di dati non validi (cioè con un hash
     * diverso) in un dato Merkle Tree, secondo questo Merkle Tree. Grazie alle
     * proprietà degli alberi di Merkle, ciò può essere fatto confrontando gli
     * hash dei nodi interni corrispondenti nei due alberi. Ad esempio, nel caso
     * di un singolo dato non valido, verrebbe percorso un unico cammino di
     * lunghezza pari all'altezza dell'albero. Gli indici forniti partono da 0 e
     * corrispondono all'ordine degli elementi nell'ultimo livello dell'albero
     * da sinistra a destra (e quindi l'ordine degli elementi forniti alla
     * costruzione). Se l'albero fornito ha una struttura diversa, possibilmente
     * a causa di una quantità diversa di elementi con cui è stato costruito e,
     * quindi, non rappresenta gli stessi dati, viene lanciata un'eccezione.
     *
     * @param otherTree
     *                      l'altro Merkle Tree.
     * @throws IllegalArgumentException
     *                                      se l'altro albero è null o ha una
     *                                      struttura diversa.
     * @return l'insieme di indici degli elementi di dati non validi.
     */
    public Set<Integer> findInvalidDataIndices(MerkleTree<T> otherTree) {
    	// Controllo se l'altro albero è nullo o se la sua larghezza è diversa da quella dell'albero corrente
    	// Lancio un'eccezione in caso di parametri non validi
        if(otherTree == null || otherTree.getWidth() != this.width)
        	throw new IllegalArgumentException("FIDI: parametro otherTree null");

        // Insieme per memorizzare gli indici dei nodi con dati non validi
        Set<Integer> indiciInvalidi = new HashSet<Integer>();
        // Chiamo il confronto dei nodi a partire dalla radice
        compareNodes(this.root, otherTree.getRoot(), 0, indiciInvalidi);
        // Restituisco l'insieme degli indici dei nodi non validi
        return indiciInvalidi;
    }

    /**
     * Confronta ricorsivamente due nodi Merkle e identifica le differenze nei
     * loro hash. Questo metodo confronta due nodi Merkle e i loro rispettivi
     * sottoalberi, aggiungendo l'indice dei nodi con hash diversi a un insieme
     * di indici non validi. Se uno dei nodi è nullo, l'indice viene aggiunto
     * direttamente. Se gli hash dei nodi non corrispondono, il metodo verifica
     * se entrambi i nodi sono foglie; in tal caso, l'indice viene aggiunto,
     * altrimenti la ricerca continua ricorsivamente nei figli sinistro e destro.
     *
     * @param nodo1        il primo nodo Merkle da confrontare
     * 
     * @param nodo2        il secondo nodo Merkle da confrontare
     * 
     * @param indice       l'indice corrente nel confronto
     * 
     * @param indiciInvalidi l'insieme degli indici dei nodi non validi
     * 
     * @throws IllegalArgumentException se uno dei nodi forniti è null
     * 
     */
    private void compareNodes(MerkleNode nodo1, MerkleNode nodo2, int indice, Set<Integer> indiciInvalidi) {
    	// Se uno dei due nodi è nullo
        if(nodo1 == null || nodo2 == null) {
        	// Aggiungo l'indice ai nodi non validi se i nodi sono diversi
            if(nodo1 != nodo2) indiciInvalidi.add(indice);
            // Termino il confronto per questo ramo
            return;
        }

        // Confronto gli hash dei due nodi
        if(!nodo1.getHash().equals(nodo2.getHash())) {
        	// Se entrambi i nodi sono foglie ed hanno hash diversi, aggiungo l'indice
            if(nodo1.isLeaf() && nodo2.isLeaf()) indiciInvalidi.add(indice);
            else {
            	// Altrimenti, confronto ricorsivamente i figli sinistri e destri
                compareNodes(nodo1.getLeft(), nodo2.getLeft(), indice * 2, indiciInvalidi);
                compareNodes(nodo1.getRight(), nodo2.getRight(), indice * 2 + 1, indiciInvalidi);
            }
        }
    }

    /**
     * Restituisce la prova di Merkle per un dato elemento, ovvero la lista di
     * hash dei nodi fratelli di ciascun nodo nel cammino dalla radice a una
     * foglia contenente il dato. La prova di Merkle dovrebbe fornire una lista
     * di oggetti MerkleProofHash tale per cui, combinando l'hash del dato con
     * l'hash del primo oggetto MerkleProofHash in un nuovo hash, il risultato
     * con il successivo e così via fino all'ultimo oggetto, si possa ottenere
     * l'hash del nodo padre dell'albero. Nel caso in cui non ci, in determinati
     * step della prova non ci siano due hash distinti da combinare, l'hash deve
     * comunque ricalcolato sulla base dell'unico hash disponibile.
     *
     * @param data
     *                 l'elemento per cui generare la prova di Merkle.
     * @return la prova di Merkle per il dato.
     * @throws IllegalArgumentException
     *                                      se il dato è null o non è parte
     *                                      dell'albero.
     */
    public MerkleProof getMerkleProof(T data) {
    	// Controllo se il parametro data è null e lancio un'eccezione
        if(data == null)
        	throw new IllegalArgumentException("GMP1: parametro data null");
        
        // Calcolo l'hash del dato
        String hash = HashUtil.dataToHash(data);
        // Lista per memorizzare i MerkleProofHash trovati
        List<MerkleProof.MerkleProofHash> hashesProva = new ArrayList<MerkleProof.MerkleProofHash>();
        
        // Costruisco il MerkleProof verificando se il nodo corrispondente si trova nell'albero
        if(!buildMerkleProofRec(this.root, hash, hashesProva))
        	throw new IllegalArgumentException("GMP1.2: output false");
        
        // Creo un oggetto MerkleProof utilizzando l'hash della radice e il numero di elementi trovati
        MerkleProof prova = new MerkleProof(this.root.getHash(), hashesProva.size());
        for(MerkleProof.MerkleProofHash provaHash : hashesProva) {
        	// Aggiungo ciascun hash al MerkleProof
        	prova.addHash(provaHash.getHash(), provaHash.isLeft());
        }
        
        // Restituisco il MerkleProof
        return prova;
    }

    /**
     * Costruisce ricorsivamente una prova Merkle per un dato con l'hash
     * specificato in una struttura ad albero Merkle. Questo metodo esplora
     * l'albero Merkle a partire dal nodo fornito, cercando un nodo con l'hash
     * specificato. Se viene trovato un nodo con l'hash corrispondente, il
     * metodo aggiunge gli hash dei nodi fratelli alla lista delle prove,
     * costruendo così una prova Merkle. Il metodo controlla sia i sottoalberi
     * sinistro che destro e aggiunge gli hash corrispondenti alla lista delle
     * prove per ricostruire il percorso di verifica.
     *
     * @param nodo        il nodo corrente dell'albero Merkle in cui cercare
     * 					  l'hash del dato
     * 
     * @param hash        l'hash del dato da verificare
     * 
     * @param hashesProva la lista delle prove Merkle per il dato
     * 
     * @return true se l'hash del dato è trovato e la prova Merkle è costruita,
     * 				false altrimenti
     * 
     * @throws IllegalArgumentException se il nodo o l'hash del dato sono null
     * 
     */
    private boolean buildMerkleProofRec(MerkleNode nodo, String hash, List<MerkleProof.MerkleProofHash> hashesProva) {
    	// Se il nodo è null, ritorno false
        if(nodo == null) return false;
        
        // Se l'hash del nodo corrisponde all'hash cercato, ritorno true
        if(nodo.getHash().equals(hash)) return true;
        
        // Controllo il sottoalbero sinistro
        if(nodo.getLeft() != null && buildMerkleProofRec(nodo.getLeft(), hash, hashesProva)) {
        	// Aggiungo l'hash del sottoalbero destro (se esiste) alla lista
        	hashesProva.add(new MerkleProof.MerkleProofHash(nodo.getRight() != null ? nodo.getRight().getHash() : "", false));
        	// Ritorno true
            return true;
        }
        
        // Controllo il sottoalbero destro
        if(nodo.getRight() != null && buildMerkleProofRec(nodo.getRight(), hash, hashesProva)) {
        	// Aggiungo l'hash del sottoalbero sinistro (se esiste) alla lista
        	hashesProva.add(new MerkleProof.MerkleProofHash(nodo.getLeft() != null ? nodo.getLeft().getHash() : "", true));
        	// Ritorno true
        	return true;
        }
        
        // Se nessun nodo corrisponde, ritorno false
        return false;
    }

    /**
     * Restituisce la prova di Merkle per un dato branch, ovvero la lista di
     * hash dei nodi fratelli di ciascun nodo nel cammino dalla radice al dato
     * nodo branch, rappresentativo di un blocco di dati. La prova di Merkle
     * dovrebbe fornire una lista di oggetti MerkleProofHash tale per cui,
     * combinando l'hash del branch con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, si possa ottenere l'hash del nodo padre dell'albero.
     * Nel caso in cui non ci, in determinati step della prova non ci siano due
     * hash distinti da combinare, l'hash deve comunque ricalcolato sulla base
     * dell'unico hash disponibile.
     *
     * @param branch
     *                   il branch per cui generare la prova di Merkle.
     * @return la prova di Merkle per il branch.
     * @throws IllegalArgumentException
     *                                      se il branch è null o non è parte
     *                                      dell'albero.
     */
    public MerkleProof getMerkleProof(MerkleNode branch) {
    	// Controllo se il parametro branch è null e lancio un'eccezione
        if(branch == null)
        	throw new IllegalArgumentException("GMP2: parametro branch null");
        
        // Ottengo l'hash del nodo branch
        String hash = branch.getHash();
        // Lista per memorizzare i MerkleProofHash trovati
        List<MerkleProof.MerkleProofHash> hashesProva = new ArrayList<MerkleProof.MerkleProofHash>();
        
        // Creo il MerkleProof verificando se il nodo corrispondente si trova nell'albero
        if(!buildMerkleProofRec(this.root, hash, hashesProva))
        	throw new IllegalArgumentException("GMP2.2: output false");
        
        // Creo un oggetto MerkleProof utilizzando l'hash della radice e il numero di elementi trovati
        MerkleProof prova = new MerkleProof(this.root.getHash(), hashesProva.size());
        for(MerkleProof.MerkleProofHash provaHash : hashesProva) {
        	// Aggiungo ciascun hash al MerkleProof
        	prova.addHash(provaHash.getHash(), provaHash.isLeft());
        }
        
        // Restituisco il MerkleProof
        return prova;
    }
}