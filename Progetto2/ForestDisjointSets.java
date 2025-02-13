import java.util.Map;
import java.util.Set;

import java.util.HashMap;
import java.util.HashSet;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 *
 * @param <E>
 *                il tipo degli elementi degli insiemi disgiunti
 */
public class ForestDisjointSets<E> implements DisjointSets<E> {

    /*
     * Mappa che associa ad ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;
    
    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         * 
         * @param item
         *                 l'elemento conservato in questo nodo
         * 
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }

    }

    /**
     * Costruisce una foresta vuota di insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {
    	// Inizializzo la mappa corrente che tiene traccia degli elementi del gruppo disgiunto
    	this.currentElements = new HashMap<E, Node<E>>();
    }

    @Override
    public boolean isPresent(E e) {
    	// Se l'elemento passato come parametro è null, lancio un'eccezione
    	if(e == null)
            throw new NullPointerException("IP: parametro e null");
    	
    	// Restituisco true se la mappa contiene la chiave corrispondente all'elemento e
        return this.currentElements.containsKey(e);
    }

    /*
     * Crea un albero della foresta consistente di un solo nodo di rango zero il
     * cui parent è se stesso.
     */
    @Override
    public void makeSet(E e) {
    	// Se l'elemento passato come parametro è null, lancio un'eccezione
        if(e == null)
            throw new NullPointerException("MS1: parametro e null");

        // Se l'elemento è già presente, lancio un'eccezione
        if(this.isPresent(e))
            throw new IllegalArgumentException("MS2: parametro e presente");

        // Aggiungo l'elemento alla struttura associando l'elemento a un nuovo nodo
        this.currentElements.put(e, new Node<E>(e));
    }

    /*
     * L'implementazione del find-set deve realizzare l'euristica
     * "compressione del cammino". Si vedano le istruzioni o il libro di testo
     * Cormen et al. (terza edizione) Capitolo 21 Sezione 3.
     */
    @Override
    public E findSet(E e) {
    	// Se l'elemento passato come parametro è null, lancio un'eccezione
        if(e == null)
            throw new NullPointerException("FS: parametro e null");

        // Se l'elemento non è presente nell'insieme, restituisco null
        if(!this.isPresent(e)) return null;
        
        // Restituisco l'elemento rappresentato dal nodo corrente,
        // seguendo la struttura di un insieme disgiunto
        return this.findSet(this.currentElements.get(e)).item;
    }

    /*
     * L'implementazione dell'unione deve realizzare l'euristica
     * "unione per rango". Si vedano le istruzioni o il libro di testo Cormen et
     * al. (terza edizione) Capitolo 21 Sezione 3. In particolare, il
     * rappresentante dell'unione dovrà essere il rappresentante dell'insieme il
     * cui corrispondente albero ha radice con rango più alto. Nel caso in cui
     * il rango della radice dell'albero di cui fa parte e1 sia uguale al rango
     * della radice dell'albero di cui fa parte e2 il rappresentante dell'unione
     * sarà il rappresentante dell'insieme di cui fa parte e2.
     */
    @Override
    public void union(E e1, E e2) {
    	// Se uno degli elementi passati come parametro è null, lancio un'eccezione
    	if(e1 == null || e2 == null)
    		throw new NullPointerException("U1: parametro e1/e2 null.");
 
    	// Se uno degli elementi non è presente nell'insieme, lancio un'eccezione
        if(!this.isPresent(e1) || !this.isPresent(e2))
        	throw new IllegalArgumentException("U2: parametro e1/e2 non presente");

        // Trovo le radici degli insiemi di e1 ed e2
        Node<E> radice1 = this.findSet(this.currentElements.get(e1));
        Node<E> radice2 = this.findSet(this.currentElements.get(e2));

        // Se le radici sono uguali, gli insiemi sono già uniti
        if(radice1 == radice2) return;

        // Se la radice di e1 ha un rango maggiore di quella di e2,
        // faccio diventare e1 la radice di e2
        if(radice1.rank > radice2.rank) {
        	radice2.parent = radice1;
        }
        // Se la radice di e2 ha un rango maggiore di quella di e1,
        // faccio diventare e2 la radice di e1
        else {
        	radice1.parent = radice2;
        	// Se i ranghi sono uguali, incremento il rango della radice di e2
            if(radice1.rank == radice2.rank) radice2.rank++;
        }
    }

    @Override
    public Set<E> getCurrentRepresentatives() {
    	// Creo un nuovo set di tipo HashSet per contenere gli elementi rappresentanti
        Set<E> rappresentanti = new HashSet<E>();
        
        // Itero su ogni nodo della mappa currentElements
        for(Node<E> nodo : this.currentElements.values()) {
        	// Se il nodo è il proprio genitore, significa che è un rappresentante
            if(nodo.parent == nodo) rappresentanti.add(nodo.item); // Quindi lo aggiungo al set
        }
        
        // Restituisco il set di rappresentanti trovati
        return rappresentanti;
    }

    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
    	// Se l'elemento passato come parametro è null, lancio un'eccezione
    	if(e == null)
    		throw new NullPointerException("GCEOSC1: parametro e null");

    	// Se l'elemento passato come parametro non è presente, lancio un'eccezione
    	if(!this.isPresent(e))
    		throw new IllegalArgumentException("GCEOSC2: parametro e non presente");

    	// Creo un nuovo set per memorizzare gli elementi
    	// che appartengono allo stesso insieme dell'elemento e
    	Set<E> elementi = new HashSet<E>();
    	// Trovo il nodo rappresentante del set contenente l'elemento e
    	Node<E> rappresentante = this.findSet(this.currentElements.get(e));
        
    	// Itero tutti i nodi nel set corrente
    	for(Node<E> nodo : this.currentElements.values()) {
    		// Se il nodo appartiene allo stesso set del rappresentante,
    		// lo aggiungo al set di risultati
    		if(this.findSet(nodo) == rappresentante) elementi.add(nodo.item);
    	}
        
    	// Restituisco il set degli elementi che appartengono allo stesso insieme dell'elemento e
    	return elementi;
    }

    @Override
    public void clear() {
    	// Pulisco la collezione currentElements rimuovendo tutti gli elementi al suo interno
    	this.currentElements.clear();
    }
    
    /**
     * Trova e restituisce la radice dell'insieme disgiunto a cui appartiene il nodo.
     * Utilizza la tecnica di compressione del cammino per ottimizzare le future ricerche.
     *
     * @param node Il nodo di cui si vuole trovare la radice del set.
     * 
     * @return La radice del set a cui appartiene il nodo.
     * 
     */
    private Node<E> findSet(Node<E> node) {
    	// Se il nodo non è il suo stesso genitore, assegno al nodo il genitore del genitore
        if(node != node.parent) node.parent = this.findSet(node.parent);
        
        // Restituisco il genitore
        return node.parent;
    }
}
