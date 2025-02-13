/**
 * Una classe che rappresenta una prova di Merkle per un determinato albero di
 * Merkle ed un suo elemento o branch. Oggetti di questa classe rappresentano un
 * proccesso di verifica auto-contenuto, dato da una sequenza di oggetti
 * MerkleProofHash che rappresentano i passaggi necessari per validare un dato
 * elemento o branch in un albero di Merkle decisi al momento di costruzione
 * della prova.
 */
public class MerkleProof {

    /**
     * La prova di Merkle, rappresentata come una lista concatenata di oggetti
     * MerkleProofHash.
     */
    private final HashLinkedList<MerkleProofHash> proof;

    /**
     * L'hash della radice dell'albero di Merkle per il quale la prova è stata
     * costruita.
     */
    private final String rootHash;

    /**
     * Lunghezza massima della prova, dato dal numero di hash che la compongono
     * quando completa. Serve ad evitare che la prova venga modificata una volta
     * che essa sia stata completamente costruita.
     */
    private final int length;

    /**
     * Costruisce una nuova prova di Merkle per un dato albero di Merkle,
     * specificando la radice dell'albero e la lunghezza massima della prova. La
     * lunghezza massima della prova è il numero di hash che la compongono
     * quando completa, oltre il quale non è possibile aggiungere altri hash.
     *
     * @param rootHash
     *                     l'hash della radice dell'albero di Merkle.
     * @param length
     *                     la lunghezza massima della prova.
     */
    public MerkleProof(String rootHash, int length) {
        if (rootHash == null)
            throw new IllegalArgumentException("The root hash is null");
        this.proof = new HashLinkedList<>();
        this.rootHash = rootHash;
        this.length = length;
    }

    /**
     * Restituisce la massima lunghezza della prova, dato dal numero di hash che
     * la compongono quando completa.
     *
     * @return la massima lunghezza della prova.
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Aggiunge un hash alla prova di Merkle, specificando se esso dovrebbe
     * essere concatenato a sinistra o a destra durante la verifica della prova.
     * Se la prova è già completa, ovvero ha già raggiunto il massimo numero di
     * hash deciso alla sua costruzione, l'hash non viene aggiunto e la funzione
     * restituisce false.
     *
     * @param hash
     *                   l'hash da aggiungere alla prova.
     * @param isLeft
     *                   true se l'hash dovrebbe essere concatenato a sinistra,
     *                   false altrimenti.
     * @return true se l'hash è stato aggiunto con successo, false altrimenti.
     */
    public boolean addHash(String hash, boolean isLeft) {
    	// Controllo se l'hash fornito è null, se lo è lancio un'eccezione 
    	if(hash == null)
    		throw new IllegalArgumentException("AH: parametro hash null");

    	// Verifico se la lunghezza massima della prova è già stata raggiunta
        if(this.proof.getSize() >= this.length) return false;

        // Aggiungo un nuovo MerkleProofHash alla coda della lista concatenata
        this.proof.addAtTail(new MerkleProofHash(hash, isLeft));
        // Restituisco true per indicare che l'hash è stato aggiunto con successo
        return true;
    }

    /**
     * Rappresenta un singolo step di una prova di Merkle per la validazione di
     * un dato elemento.
     */
    public static class MerkleProofHash {
        /**
         * L'hash dell'oggetto.
         */
        private final String hash;

        /**
         * Indica se l'hash dell'oggetto dovrebbe essere concatenato a sinistra
         * durante la verifica della prova.
         */
        private final boolean isLeft;

        public MerkleProofHash(String hash, boolean isLeft) {
            if (hash == null)
                throw new IllegalArgumentException("The hash cannot be null");

            this.hash = hash;
            this.isLeft = isLeft;
        }

        /**
         * Restituisce l'hash dell'oggetto MerkleProofHash.
         *
         * @return l'hash dell'oggetto MerkleProofHash.
         */
        public String getHash() {
            return hash;
        }

        /**
         * Restituisce true se, durante la verifica della prova, l'hash
         * dell'oggetto dovrebbe essere concatenato a sinistra, false
         * altrimenti.
         *
         * @return true se l'hash dell'oggetto dovrebbe essere concatenato a
         *         sinistra, false altrimenti.
         */
        public boolean isLeft() {
            return isLeft;
        }

        /*
         * Due MerkleProofHash sono uguali se hanno lo stesso hash e lo
         * stesso flag isLeft
         */
        @Override
        public boolean equals(Object obj) {
        	// Controllo se l'oggetto fornito è null, se lo è restituisco false
            if(obj == null) return false;
            
            // Controllo se l'oggetto corrente e l'oggetto passato sono lo stesso riferimento
            if(this == obj) return true;
            
            // Controllo se l'oggetto passato non è un'istanza di MerkleProofHash
            if(!(obj instanceof MerkleProofHash)) return false;
            
            // Effettuo un cast sicuro a MerkleProofHash
            MerkleProofHash altroMerkleProofHash = (MerkleProofHash) obj;
            // Controllo l'uguaglianza dell'hash e del valore del flag isLeft
            return this.hash.equals(altroMerkleProofHash.getHash()) && this.isLeft == altroMerkleProofHash.isLeft();
        }

        @Override
        public String toString() {
            return hash + (isLeft ? "L" : "R");
        }

        /*
         * Implementare in accordo a equals
         */
        @Override
        public int hashCode() {
        	// Definisco un numero primo da usare nella generazione del codice hash
        	final int primo = 31;
        	// Ottenengo il valore hash dell'attributo 'hash'
        	int risultato = this.hash.hashCode();
        	
        	// Moltiplico il risultato per 31 e sommo il valore corrispondente al flag 'isLeft'            
            // e restituisco il risultato finale del calcolo dell'hash
            return primo * risultato + (this.isLeft ? 1 : 0);            
        }
    }

    /**
     * Valida un dato elemento per questa prova di Merkle. La verifica avviene
     * combinando l'hash del dato con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, e controllando che l'hash finale coincida con quello
     * del nodo radice dell'albero di Merkle orginale.
     *
     * @param data
     *                 l'elemento da validare.
     * @return true se il dato è valido secondo la prova; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se il dato è null.
     */
    public boolean proveValidityOfData(Object data) {
    	// Controllo se l'oggetto fornito è null, se lo è lancio un'eccezione
    	if(data == null)
    		throw new IllegalArgumentException("PVD: parametro data null");

    	// Calcolo l'hash del dato utilizzando il metodo di hashing definito in HashUtil
        String hashCorrente = HashUtil.dataToHash(data);

        // Itero su ciascun oggetto MerkleProofHash nella prova
        for(MerkleProofHash hashProva : this.proof) {
        	// Se l'hash deve essere concatenato a sinistra, calcolo il nuovo hash concatenando
            // prima l'hash del proofHash e poi l'hash corrente
            if(hashProva.isLeft()) hashCorrente = HashUtil.computeMD5((hashProva.getHash() + hashCorrente).getBytes());
            // Altrimenti, se l'hash deve essere concatenato a destra, calcolo il nuovo hash concatenando
            // prima l'hash corrente e poi l'hash del proofHash
            else hashCorrente = HashUtil.computeMD5((hashCorrente + hashProva.getHash()).getBytes());
        }

        // Confronto l'hash risultante con l'hash della radice dell'albero Merkle. Se coincidono, il dato è valido
        return hashCorrente.equals(this.rootHash);
    }

    /**
     * Valida un dato branch per questa prova di Merkle. La verifica avviene
     * combinando l'hash del branch con l'hash del primo oggetto MerkleProofHash
     * in un nuovo hash, il risultato con il successivo e così via fino
     * all'ultimo oggetto, e controllando che l'hash finale coincida con quello
     * del nodo radice dell'albero di Merkle orginale.
     *
     * @param branch
     *                   il branch da validare.
     * @return true se il branch è valido secondo la prova; false altrimenti.
     * @throws IllegalArgumentException
     *                                      se il branch è null.
     */
    public boolean proveValidityOfBranch(MerkleNode branch) {
    	// Controllo se l'oggetto fornito è null, se lo è lancio un'eccezione
    	if(branch == null)
    		throw new IllegalArgumentException("PVB: parametro branch null");

    	// Ottengo l'hash del ramo da validare
        String hashCorrente = branch.getHash();

        // Itero su ciascun oggetto MerkleProofHash nella prova
        for(MerkleProofHash hashProva : this.proof) {
        	// Se l'hash deve essere concatenato a sinistra, calcolo il nuovo hash concatenando
            // prima l'hash del proofHash e poi l'hash corrente
            if(hashProva.isLeft()) hashCorrente = HashUtil.computeMD5((hashProva.getHash() + hashCorrente).getBytes());
            // Altrimenti, se l'hash deve essere concatenato a destra, calcolo il nuovo hash concatenando
            // prima l'hash corrente e poi l'hash del proofHash
            else hashCorrente = HashUtil.computeMD5((hashCorrente + hashProva.getHash()).getBytes());
        }

        // Confronto l'hash risultante con l'hash della radice dell'albero Merkle. Se coincidono, il dato è valido
        return hashCorrente.equals(this.rootHash);
    }
}
