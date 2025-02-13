import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Classe singoletto che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@ForestDisjointSets<GraphNode<L>>}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 *
 * @param <L>
 *                il tipo delle etichette dei nodi del grafo
 */
public class UndirectedGraphConnectedComponentsComputer<L> {

    /*
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione di insiemi disgiunti.
     * 
     * @param g
     *              un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     *         insieme di nodi del grafo
     * @throws NullPointerException
     *                                      se il grafo passato è nullo
     * @throws IllegalArgumentException
     *                                      se il grafo passato è orientato
     */
    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
    	// Controllo se il grafo è null, lancio un'eccezione se lo è
        if(g == null)
            throw new NullPointerException("CCC1: parametro g null");

        // Controllo se il grafo è orientato, lancio un'eccezione se lo è
        if(g.isDirected())
            throw new IllegalArgumentException("CCC2: parametro g orientato");

        // Cancello qualsiasi stato precedente nella struttura dati
        this.f.clear();
        
        // Inizializzo una componente disgiunta per ogni nodo del grafo
        for(GraphNode<L> nodo : g.getNodes()) {
            this.f.makeSet(nodo);
        }

        // Per ogni arco del grafo, se l'arco non è orientato, unisco i due nodi collegati dall'arco
        for(GraphEdge<L> arco : g.getEdges()) {
            if(!arco.isDirected()) this.f.union(arco.getNode1(), arco.getNode2());
        }

        // Mappa che associa ogni rappresentante di insieme alla sua componente connessa
        Map<GraphNode<L>, Set<GraphNode<L>>> mappaComponenti = new HashMap<GraphNode<L>, Set<GraphNode<L>>>();
        
        // Itero su tutti i nodi del grafo per costruire le componenti connesse
        for(GraphNode<L> nodo : g.getNodes()) {
        	// Trovo il rappresentante dell'insieme a cui appartiene il nodo
            GraphNode<L> rappresentante = this.f.findSet(nodo);

            // Recupero la componente associata al rappresentante, se esiste
            Set<GraphNode<L>> componente = mappaComponenti.get(rappresentante);
            if(componente == null) {
            	// Se la componente non esiste, ne creo una nuova e la aggiungo alla mappa
            	componente = new HashSet<GraphNode<L>>();
                mappaComponenti.put(rappresentante, componente);
            }

            // Aggiungo il nodo corrente alla sua componente connessa
            componente.add(nodo);
        }

        // Ritorno un set contenente tutte le componenti connesse
        return new HashSet<Set<GraphNode<L>>>(mappaComponenti.values());
    }
}
