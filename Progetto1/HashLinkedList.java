package it.unicam.cs.asdl2425.mp1;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Una classe che rappresenta una lista concatenata con il calcolo degli hash
 * MD5 per ciascun elemento. Ogni nodo della lista contiene il dato originale di
 * tipo generico T e il relativo hash calcolato utilizzando l'algoritmo MD5.
 *
 * <p>
 * La classe supporta le seguenti operazioni principali:
 * <ul>
 * <li>Aggiungere un elemento in testa alla lista</li>
 * <li>Aggiungere un elemento in coda alla lista</li>
 * <li>Rimuovere un elemento dalla lista in base al dato</li>
 * <li>Recuperare una lista ordinata di tutti gli hash contenuti nella
 * lista</li>
 * <li>Costruire una rappresentazione testuale della lista</li>
 * </ul>
 *
 * <p>
 * Questa implementazione include ottimizzazioni come il mantenimento di un
 * riferimento all'ultimo nodo della lista (tail), che rende l'inserimento in
 * coda un'operazione O(1).
 *
 * <p>
 * La classe utilizza la classe HashUtil per calcolare l'hash MD5 dei dati.
 *
 * @param <T>
 *                il tipo generico dei dati contenuti nei nodi della lista.
 * 
 * @author Luca Tesei, Marco Caputo (template), CASTIGNANI LEONARDO
 * 											    leonardo.castignani@studenti.unicam.it
 * 
 */
public class HashLinkedList<T> implements Iterable<T> {
    private Node head; // Primo nodo della lista

    private Node tail; // Ultimo nodo della lista

    private int size; // Numero di nodi della lista

    private int numeroModifiche; // Numero di modifiche effettuate sulla lista
                                 // per l'implementazione dell'iteratore
                                 // fail-fast

    public HashLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.numeroModifiche = 0;
    }

    /**
     * Restituisce il numero attuale di nodi nella lista.
     *
     * @return il numero di nodi nella lista.
     */
    public int getSize() {
        return size;
    }

    /**
     * Rappresenta un nodo nella lista concatenata.
     */
    private class Node {
        String hash; // Hash del dato

        T data; // Dato originale

        Node next;

        Node(T data) {
            this.data = data;
            this.hash = HashUtil.dataToHash(data);
            this.next = null;
        }
    }

    /**
     * Aggiunge un nuovo elemento in testa alla lista.
     *
     * @param data
     *                 il dato da aggiungere.
     */
    public void addAtHead(T data) {
    	// Verifico che il dato non sia nullo
    	if(data == null)
    		throw new NullPointerException("AAH: parametro data null");
    	
    	// Creo un nuovo nodo con l'hash calcolato
    	Node nuovoNodo = new Node(data);
    	
    	// Collego il nuovo nodo alla testa attuale
        nuovoNodo.next = this.head;
        // Aggiorno la testa della lista
        this.head = nuovoNodo;
        // Se la lista è vuota, aggiorno la coda
        if(this.tail == null) this.tail = nuovoNodo;
        
        // Incrementao la dimensione della lista
        this.size++;
        // Aumento il contatore delle modifiche
        this.numeroModifiche++;
    }

    /**
     * Aggiunge un nuovo elemento in coda alla lista.
     *
     * @param data
     *                 il dato da aggiungere.
     */
    public void addAtTail(T data) {
    	// Verifico che il dato non sia nullo
    	if(data == null)
    		throw new NullPointerException("AAT: parametro data null");
    	
    	// Creo un nuovo nodo con l'hash calcolato
    	Node nuovoNodo = new Node(data);
    	
    	// Collego il nuovo nodo alla coda attuale
        if(this.tail != null) this.tail.next = nuovoNodo;
        // Aggiorno la coda della lista
        this.tail = nuovoNodo;
        // Se la lista è vuota, aggiorno anche la testa
        if(this.head == null) this.head = nuovoNodo;
        
        // Incremento la dimensione della lista
        this.size++;
        // Aumento il contatore delle modifiche
        this.numeroModifiche++;
    }

    /**
     * Restituisce un'ArrayList contenente tutti gli hash nella lista in ordine.
     *
     * @return una lista con tutti gli hash della lista.
     */
    public ArrayList<String> getAllHashes() {
    	// Creo un ArrayList per contenere gli hash
    	ArrayList<String> hashLista = new ArrayList<String>();
    	
    	// Creo un iteratore per attraversare la lista
        Iterator<T> iterator = this.iterator();
        // Inizio dal primo nodo
        Node corrente = this.head;
        
        // Itero attraverso tutti i nodi della lista
        while(iterator.hasNext()) {
        	// Ottengo il prossimo dato
            T data = iterator.next();
            
            // Ciclo finché il nodo corrente non è nullo
            while(corrente != null) {
            	// Quando trovo il nodo con il dato corretto
                if(corrente.data.equals(data)) {
                	// Aggiungo l'hash alla lista
                	hashLista.add(corrente.hash);
                	// Esco dal ciclo interno
                    break;
                }
                // Proseguo al prossimo nodo
                corrente = corrente.next;
            }
        }
        
        // Restituisco la lista contenente tutti gli hash
        return hashLista;
    }

    /**
     * Costruisce una stringa contenente tutti i nodi della lista, includendo
     * dati e hash. La stringa dovrebbe essere formattata come nel seguente
     * esempio:
     * 
     * <pre>
     *     Dato: StringaDato1, Hash: 5d41402abc4b2a76b9719d911017c592
     *     Dato: SteringaDato2, Hash: 7b8b965ad4bca0e41ab51de7b31363a1
     *     ...
     *     Dato: StringaDatoN, Hash: 2c6ee3d301aaf375b8f026980e7c7e1c
     * </pre>
     *
     * @return una rappresentazione testuale di tutti i nodi nella lista.
     */
    public String buildNodesString() {
    	// Utilizzo StringBuilder per concatenare i risultati
    	StringBuilder sb = new StringBuilder();
        
    	// Creo un iteratore per attraversare la lista
        Iterator<T> iterator = this.iterator();
        // Inizio dal primo nodo
        Node corrente = this.head;
        
        // Itero attraverso tutti i nodi della lista
        while(iterator.hasNext()) {
        	// Ottengo il prossimo dato
            T data = iterator.next();
            
            // Ciclo finché il nodo corrente non è nullo
            while(corrente != null) {
            	// Quando trovo il nodo con il dato corretto
                if(corrente.data.equals(data)) {
                	// Aggiungo il dato alla stringa
                    sb.append("Dato: ").append(corrente.data)
                      // Aggiungo l'hash del nodo
                      .append(", Hash: ").append(corrente.hash)
                      // Aggiungo una nuova riga per il prossimo nodo
                      .append("\n");
                    // Esco dal ciclo interno
                    break;
                }
                // Proseguo al prossimo nodo
                corrente = corrente.next;
            }
        }
        
        // Restituisco la stringa finale
        return sb.toString();
    }

    /**
     * Rimuove il primo elemento nella lista che contiene il dato specificato.
     *
     * @param data
     *                 il dato da rimuovere.
     * @return true se l'elemento è stato trovato e rimosso, false altrimenti.
     */
	public boolean remove(T data) {
		// Verifico che il dato non sia nullo
		if(data == null)
			throw new NullPointerException("R: parametro data null");

		// Creo un iteratore per attraversare la lista
	    Iterator<T> iterator = this.iterator();
	    // Inizio dal primo nodo
	    Node corrente = this.head;
	    // Inizializzo il nodo precedente come null
	    Node precedente = null;

	    // Itero attraverso tutti i nodi della lista
	    while(iterator.hasNext()) {
	    	// Ottengo il prossimo dato
	        T dataCorrente = iterator.next();
	        
	        // Se il dato corrente è uguale al dato da rimuovere
	        if(dataCorrente.equals(data)) {
	        	// Inizio a scorrere i nodi per trovare e rimuovere l'elemento
	            while(corrente != null) {
	            	// Se il dato del nodo corrente è quello da rimuovere
	                if(corrente.data.equals(data)) {
	                	// Se è il primo nodo, aggiorno la testa
	                    if(precedente == null) this.head = corrente.next;
	                    // Altrimenti aggiorno il nodo precedente
	                    else precedente.next = corrente.next;	                    
	                    
	                    // Se è l'ultimo nodo, aggiorno la coda
	                    if(corrente == this.tail) this.tail = precedente;

	                    // Decremento la dimensione della lista
	                    this.size--;
	                    // Aumento il contatore delle modifiche
	                    this.numeroModifiche++;
	                    // Ritorno true per indicare che l'elemento è stato rimosso
	                    return true;
	                }
	                // Sposto il precedente al corrente
	                precedente = corrente;
	                // Sposto il corrente al prossimo nodo
	                corrente = corrente.next;
	            }
	        }
	    }
	    
	    // Se l'elemento non è stato trovato, ritorno false
	    return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    /**
     * Classe che realizza un iteratore fail-fast per HashLinkedList.
     */
    private class Itr implements Iterator<T> {
    	
    	// Riferimento al nodo corrente dell'iterazione
    	private Node corrente;
    	// Numero di modifiche attese
        private final int numModificheAttese;
        
        private Itr() {
        	// Inizio l'iterazione dal primo nodo
        	this.corrente = head;
        	// Memorizzo il numero di modifiche della lista
            this.numModificheAttese = numeroModifiche;
        }

        @Override
        public boolean hasNext() {
        	// Verifico che non ci siano modifiche concorrenti
        	if(this.numModificheAttese != numeroModifiche)
        		throw new ConcurrentModificationException("NMA diverso NM");
        	
        	// Restituisco true se il nodo corrente non è nullo
            return this.corrente != null;
        }

        @Override
        public T next() {
        	// Verifico la presenza di un prossimo elemento,
        	// se non ci sono più elementi lancio un'eccezione
        	if(!hasNext())
        		throw new NoSuchElementException("No elemento successivo");
        	
        	// Ottengo il dato del nodo corrente
            T data = this.corrente.data;
            // Passo al prossimo nodo
            this.corrente = this.corrente.next;
            // Restituisco il dato
            return data;
        }
    }
}