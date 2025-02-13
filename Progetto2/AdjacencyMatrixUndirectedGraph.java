import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.util.HashSet;

/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 * 
 * I nodi sono indicizzati da 0 a nodeCoount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * ad ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 * 
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa ad ogni nodo l'indice assegnato in fase di inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 * 
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i,j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 * 
 * Questa classe non supporta i metodi di cancellazione di nodi e archi, ma
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase di inserimento.
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {
    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */
    
    // Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
    // matrice di adiacenza
    protected Map<GraphNode<L>, Integer> nodesIndex;

    // Matrice di adiacenza, gli elementi sono null o oggetti della classe
    // GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
    // dimensione gradualmente ad ogni inserimento di un nuovo nodo.
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<ArrayList<GraphEdge<L>>>();
        this.nodesIndex = new HashMap<GraphNode<L>, Integer>();
    }

    @Override
    public int nodeCount() {
    	// Restituisco il numero di nodi attualmente presenti
    	return this.nodesIndex.size();
    }

    @Override
    public int edgeCount() {
    	// Inizializzo il contatore per tenere traccia del numero di archi
    	int contatore = 0;
    	
    	// Ciclo ogni riga della matrice di adiacenza
    	for(ArrayList<GraphEdge<L>> riga : this.matrix) {
    		// Ciclo ogni elemento nella riga corrente
    		for(GraphEdge<L> arco : riga) {
    			// Incremento il contatore se l'arco non è nullo
    			if(arco != null) contatore++;
    		}
    	}
    	
    	// Divido per 2 perché ogni arco è contato due volte nella matrice di adiacenza
    	return contatore / 2;
    }

    @Override
    public void clear() {
    	// Pulisco la struttura nodesIndex
    	this.nodesIndex.clear();
    	// Pulisco la struttura matrix
    	this.matrix.clear();
    }

    @Override
    public boolean isDirected() {
    	// Restituisco false, indicando che il grafo non è orientato
        return false;
    }

    @Override
    public Set<GraphNode<L>> getNodes() {
    	// Restituisco il set di chiavi dall'indice dei nodi,
        // dove le chiavi rappresentano i nodi del grafo
    	return this.nodesIndex.keySet();
    }

    @Override
    public boolean addNode(GraphNode<L> node) {    	
    	// Controllo se il parametro node è null, in tal caso lancio un'eccezione
    	if(node == null)
            throw new NullPointerException("AN: parametro node null");

    	// Verifico se il nodo esiste già nella mappa nodesIndex; se sì, ritorna false
        if(this.nodesIndex.containsKey(node)) return false;
    	
        // Calcolo un nuovo indice per il nodo basato sulla dimensione attuale di nodesIndex
        int indice = this.nodeCount();
        // Aggiungo il nodo e il suo indice alla mappa nodesIndex
    	this.nodesIndex.put(node, indice);
    	
    	// Aggiungo una nuova colonna alla matrice di adiacenza per rappresentare il nuovo nodo
    	for(ArrayList<GraphEdge<L>> colonna : this.matrix) {
    		// Aggiungo un valore null per ogni riga esistente
    		colonna.add(null);
    	}
    	
    	// Creo una nuova riga vuota per il nuovo nodo
    	ArrayList<GraphEdge<L>> nuovaRiga = new ArrayList<GraphEdge<L>>(this.nodeCount());
    	for(int i = 0; i < this.nodeCount(); i++) {
    		// Inizializzo ogni elemento della nuova riga a null
    		nuovaRiga.add(null);
    	}
    	// Aggiungo la nuova riga alla matrice di adiacenza
    	this.matrix.add(nuovaRiga);
    	
    	// Restituisco true per indicare che il nodo è stato aggiunto con successo
    	return true;
    }

    @Override
    public boolean removeNode(GraphNode<L> node) {
    	// Controllo se il parametro node è null, in tal caso lancio un'eccezione
        if(node == null)
            throw new NullPointerException("RN: parametro node null");

        // Verifico se il nodo esiste nella mappa nodesIndex
        if(!this.nodesIndex.containsKey(node)) return false;

        // Ottengo l'indice del nodo da rimuovere
        int indiceDaRimuovere = this.nodesIndex.get(node);

        // Rimuovo il nodo dalla mappa nodesIndex
        this.nodesIndex.remove(node);

        // Rimuovo la riga corrispondente al nodo dalla matrice di adiacenza
        this.matrix.remove(indiceDaRimuovere);

        // Rimuovo la colonna corrispondente al nodo da ogni riga della matrice
        for(ArrayList<GraphEdge<L>> colonna : this.matrix) {
        	colonna.remove(indiceDaRimuovere);
        }

        // Aggiorno gli indici dei nodi rimanenti nella mappa nodesIndex
        for(Map.Entry<GraphNode<L>, Integer> entry : this.nodesIndex.entrySet()) {
            if (entry.getValue() > indiceDaRimuovere) {
                // Decremento l'indice di tutti i nodi che avevano un indice maggiore del nodo rimosso
                entry.setValue(entry.getValue() - 1);
            }
        }

        // Restituisco true per indicare che il nodo è stato rimosso con successo
        return true;
    }

    @Override
    public boolean containsNode(GraphNode<L> node) {
    	// Controllo se il parametro node è null, in tal caso lancio un'eccezione
    	if(node == null)
            throw new NullPointerException("CN: parametro node null");

    	// Verifico se il nodo esiste nella mappa nodesIndex
    	return this.nodesIndex.containsKey(node);
    }

    @Override
    public GraphNode<L> getNodeOf(L label) {
    	// Controllo se il parametro label è null, in tal caso lancio un'eccezione
        if(label == null)
            throw new NullPointerException("GNO: parametro label null");

        // Itero su tutti i nodi della mappa nodesIndex
        for(GraphNode<L> nodo : this.nodesIndex.keySet()) {
        	// Confronto l'etichetta del nodo corrente con il parametro label
        	// Se trovo una corrispondenza, restituisco il nodo corrispondente
            if(nodo.getLabel().equals(label)) return nodo;
        }
        
        // Se non trovo alcun nodo con l'etichetta specificata, restituisco null
        return null;
    }

    @Override
    public int getNodeIndexOf(L label) {
    	// Controllo se il parametro label è null, in tal caso lancio un'eccezione
        if(label == null)
            throw new NullPointerException("GNIO1: parametro label null");

        // Ottengo il nodo corrispondente all'etichetta label
        GraphNode<L> nodo = getNodeOf(label);
        
        // Se il nodo è null, lancio un'eccezione
        if(nodo == null)
            throw new IllegalArgumentException("GNIO2: variabile nodo null");

        // Restituisco l'indice del nodo corrispondente nella mappa nodesIndex
        return this.nodesIndex.get(nodo);
    }

    @Override
    public GraphNode<L> getNodeAtIndex(int i) {
    	// Controllo se l'indice è fuori dai limiti validi
    	if(i < 0 || i >= this.nodeCount())
            throw new IndexOutOfBoundsException("OutOfBounds");

    	// Itero su tutti gli elementi della mappa nodesIndex
    	for(Map.Entry<GraphNode<L>, Integer> entry : this.nodesIndex.entrySet()) {
    		// Se trovo un nodo con indice corrispondente a 'i', lo restituisco
    		if(entry.getValue() == i) return entry.getKey();
    	}
    	
    	// Se nessun nodo corrisponde all'indice 'i', restituisco null
    	return null;
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {    
    	// Controllo se il parametro node è null, in tal caso lancio un'eccezione
    	if(node == null)
            throw new NullPointerException("GANO1: parametro node null");

    	// Verifico se il nodo non è contenuto nel grafo, in tal caso lancio un'eccezione
        if(!this.containsNode(node))
            throw new IllegalArgumentException("GANO2: parametro nodo non presente nel grafo");

        // Creo un insieme vuoto che conterrà i nodi adiacenti al nodo dato
    	Set<GraphNode<L>> risultato = new HashSet<GraphNode<L>>();
    	// Recupero l'indice del nodo nel grafo utilizzando l'indice dei nodi
    	int indice = this.nodesIndex.get(node);
    	
    	// Ciclo attraverso la riga della matrice di adiacenza corrispondente al nodo dato
    	for(int i = 0; i < this.matrix.get(indice).size(); i++) {
    		// Se l'elemento nella matrice di adiacenza non è null,
    		// aggiungo il nodo adiacente all'insieme dei risultati
    		if(this.matrix.get(indice).get(i) != null) risultato.add(getNodeAtIndex(i));
    	}
    	
    	// Restituisco l'insieme dei nodi adiacenti
    	return risultato;
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException("Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdges() {
    	// Creo un nuovo HashSet per memorizzare gli archi
    	Set<GraphEdge<L>> archi = new HashSet<GraphEdge<L>>();
    	
    	// Itero su ogni riga della matrice
    	for(ArrayList<GraphEdge<L>> riga : this.matrix) {
    		// Per ogni riga, itero su ogni arco presente nella riga
    		for(GraphEdge<L> arco : riga) {
    			// Se l'arco non è nullo, lo aggiungo all'insieme degli archi
    			if(arco != null) archi.add(arco);
    		}
    	}
    	
    	// Restituisco l'insieme di archi trovati
    	return archi;
    }

    @Override
    public boolean addEdge(GraphEdge<L> edge) {
    	// Controllo se il parametro edge è null, in tal caso lancio un'eccezione
    	if(edge == null)
            throw new NullPointerException("AE1: parametro edge null");

    	// Controllo se l'arco è orientato, in caso contrario lancio un'eccezione
        if(edge.isDirected())
            throw new IllegalArgumentException("AE2: parametro edge orientato");

        // Recupero i nodi dell'arco: u (nodo di partenza) e v (nodo di arrivo)
        GraphNode<L> u = edge.getNode1();
        GraphNode<L> v = edge.getNode2();
        
        // Controllo se entrambi i nodi esistono nel grafo, in caso contrario lancio un'eccezione
        if(!this.containsNode(u) || !this.containsNode(v))
            throw new IllegalArgumentException("AE3: variabile u/v non presente nel grafo");

        // Recupero gli indici dei nodi u e v nella matrice di adiacenza
        int indiceU = this.nodesIndex.get(u);
        int indiceV = this.nodesIndex.get(v);
        
        // Controllo se l'arco tra i nodi u e v è già presente nella matrice di adiacenza,
        // in tal caso restituisco false
        if(this.matrix.get(indiceU).get(indiceV) != null) return false;

        // Creo un nuovo arco non orientato tra u e v con il peso dell'arco originale
        GraphEdge<L> nuovoArco = new GraphEdge<L>(u, v, false, edge.getWeight());
        
        // Aggiungo il nuovo arco nella matrice di adiacenza per entrambe le direzioni
        this.matrix.get(indiceU).set(indiceV, nuovoArco);
        this.matrix.get(indiceV).set(indiceU, nuovoArco);
        
        // Restituisco true per indicare che l'arco è stato aggiunto con successo
        return true;
    }

    @Override
    public boolean removeEdge(GraphEdge<L> edge) {
    	// Controllo se il parametro edge è null, in tal caso lancio un'eccezione
        if(edge == null)
            throw new NullPointerException("RE1: parametro edge null");

        // Controllo se l'arco è orientato, in caso contrario lancio un'eccezione
        if(edge.isDirected())
            throw new IllegalArgumentException("RE2: parametro edge orientato");

        // Recupero i nodi dell'arco: u (nodo di partenza) e v (nodo di arrivo)
        GraphNode<L> u = edge.getNode1();
        GraphNode<L> v = edge.getNode2();

        // Controllo se entrambi i nodi esistono nel grafo, in caso contrario lancio un'eccezione
        if(!this.containsNode(u) || !this.containsNode(v))
            throw new IllegalArgumentException("RE3: variabile u/v non presente nel grafo");

        // Recupero gli indici dei nodi u e v nella matrice di adiacenza
        int indiceU = this.nodesIndex.get(u);
        int indiceV = this.nodesIndex.get(v);

        // Controllo se l'arco esiste nella matrice di adiacenza
        if(this.matrix.get(indiceU).get(indiceV) == null) return false;

        // Rimuovo l'arco dalla matrice di adiacenza in entrambe le direzioni
        this.matrix.get(indiceU).set(indiceV, null);
        this.matrix.get(indiceV).set(indiceU, null);

        // Restituisco true per indicare che l'arco è stato rimosso con successo
        return true;
    }

    @Override
    public boolean containsEdge(GraphEdge<L> edge) {
    	// Controllo se il parametro edge è null, in tal caso lancio un'eccezione
    	if(edge == null)
            throw new NullPointerException("CE1: parametro edge null");

    	// Controllo se l'arco è orientato, in caso contrario lancio un'eccezione
        if(edge.isDirected())
            throw new IllegalArgumentException("CE2: parametro edge orientato");

        // Recupero i nodi dell'arco: u (nodo di partenza) e v (nodo di arrivo)
        GraphNode<L> u = edge.getNode1();
        GraphNode<L> v = edge.getNode2();
        
        // Controllo se entrambi i nodi esistono nel grafo, in caso contrario lancio un'eccezione
        if(!this.containsNode(u) || !this.containsNode(v))
            throw new IllegalArgumentException("CE3: variabile u/v non presente nel grafo");

        // Recupero gli indici dei nodi u e v nella matrice di adiacenza
        int indiceU = this.nodesIndex.get(u);
        int indiceV = this.nodesIndex.get(v);
        
        // Restituisco true se esiste un arco, altrimenti restituisco false
        return this.matrix.get(indiceU).get(indiceV) != null;
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
    	// Controllo se il parametro node è null, in tal caso lancio un'eccezione
    	if(node == null)
            throw new NullPointerException("GEO1: parametro node null");

    	// Verifico che il nodo esista nel grafo, altrimenti lancio un'eccezione
        if(!this.containsNode(node))
            throw new IllegalArgumentException("GEO2: parametro node non presente nel grafo");

        // Creo un insieme per contenere gli archi connessi al nodo
    	Set<GraphEdge<L>> archi = new HashSet<GraphEdge<L>>();
    	// Recupero l'indice del nodo nel grafo tramite la mappa di indici
        int indice = this.nodesIndex.get(node);
        
        // Scorro gli archi connessi al nodo tramite la matrice
        for(GraphEdge<L> arco : this.matrix.get(indice)) {
        	// Se l'arco non è nullo, lo aggiungo all'insieme
            if(arco != null) archi.add(arco);
        }
        
        // Ritorno l'insieme di archi connessi al nodo
        return archi;
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException("Operazione non supportata in un grafo non orientato");
    }
}
