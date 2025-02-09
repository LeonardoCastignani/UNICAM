package it.unicam.cs.asdl2425.mp1;

/**
 * Rappresenta un nodo di un albero di Merkle.
 */
public class MerkleNode {
    private final String hash; // Hash associato al nodo.

    private final MerkleNode left; // Figlio sinistro del nodo.

    private final MerkleNode right; // Figlio destro del nodo.

    /**
     * Costruisce un nodo Merkle foglia con un valore di hash, quindi,
     * corrispondente all'hash di un dato.
     *
     * @param hash
     *                 l'hash associato al nodo.
     */
    public MerkleNode(String hash) {
        this(hash, null, null);
    }

    /**
     * Costruisce un nodo Merkle con un valore di hash e due figli, quindi,
     * corrispondente all'hash di un branch.
     *
     * @param hash
     *                  l'hash associato al nodo.
     * @param left
     *                  il figlio sinistro.
     * @param right
     *                  il figlio destro.
     */
    public MerkleNode(String hash, MerkleNode left, MerkleNode right) {
        this.hash = hash;
        this.left = left;
        this.right = right;
    }

    /**
     * Restituisce l'hash associato al nodo.
     *
     * @return l'hash associato al nodo.
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * Restituisce il figlio sinistro del nodo.
     *
     * @return il figlio sinistro del nodo.
     */
    public MerkleNode getLeft() {
        return this.left;
    }

    /**
     * Restituisce il figlio destro del nodo.
     *
     * @return il figlio destro del nodo.
     */
    public MerkleNode getRight() {
        return this.right;
    }

    /**
     * Restituisce true se il nodo è una foglia, false altrimenti.
     *
     * @return true se il nodo è una foglia, false altrimenti.
     */
    public boolean isLeaf() {
    	// Un nodo è una foglia se entrambi i figli sono null
    	return this.left == null && this.right == null;
    }

    @Override
    public String toString() {
        return this.hash;
    }

    /* due nodi sono uguali se hanno lo stesso hash */
    @Override
    public boolean equals(Object obj) {
    	// Verifico se l'oggetto passato è null
        if(obj == null) return false;
        
        // Controllo se l'istanza corrente e l'oggetto passato sono lo stesso oggetto
        if(this == obj) return true;
        
        // Verifico se l'oggetto passato è un'istanza di MerkleNode
        if(!(obj instanceof MerkleNode)) return false;
        
        // Effettuo il cast dell'oggetto passato a MerkleNode
        MerkleNode altroNodo = (MerkleNode) obj;
        
        // Confronto l'hash dell'istanza corrente con l'hash dell'oggetto passato
        // Due nodi sono uguali se i loro hash sono uguali
        return this.hash.equals(altroNodo.getHash());
    }

    /* implementare in accordo a equals */
    @Override
    public int hashCode() {
    	// Definisco un numero primo da usare nella generazione del codice hash
    	final int primo = 31;
    	// Inizio il calcolo del codice hash con un valore iniziale
        int risultato = 1;
        
        // Calcolo il codice hash sull'hash dell'oggetto corrente
        // Se l'hash è null, uso 0; altrimenti, uso il codice hash dell'hash stringa
        return primo * risultato + (this.hash == null ? 0 : this.hash.hashCode());
    }
}
