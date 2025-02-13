import java.util.Set;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * Classe singoletto che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione di
 * insiemi disgiunti di nodi del grafo.
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class KruskalMSP<L> {

    /*
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private ForestDisjointSets<GraphNode<L>> disjointSets;

    // TODO inserire eventuali variabili istanza o classi interne necessarie

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     *         copertura minimo trovato
     * @throw NullPointerException se il grafo g è null
     * @throw IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
    	// Controllo se il grafo fornito è null, in tal caso lancio un'eccezione
        if(g == null)
            throw new NullPointerException("CMSP1: parametro g null");

        // Controllo se il grafo è orientato, in tal caso lancio un'eccezione
        if(g.isDirected())
            throw new IllegalArgumentException("CMSP2: parametro g orientato");

        // Controllo che tutti gli archi abbiano un peso valido (non negativo)
        for(GraphEdge<L> arco : g.getEdges()) {
            if(!arco.hasWeight() || arco.getWeight() < 0)
                throw new IllegalArgumentException("CMSP3: grafo non pesato/pesi negativi.");
        }

        // Svuoto la struttura disjointSets per prepararla all'elaborazione
        this.disjointSets.clear();
        
        // Creo un insieme disgiunto per ogni nodo del grafo
        for(GraphNode<L> nodo : g.getNodes()) {
            this.disjointSets.makeSet(nodo);
        }

        // Ottengo una lista di tutti gli archi del grafo e li ordino con Quicksort
        List<GraphEdge<L>> archi = new ArrayList<GraphEdge<L>>(g.getEdges());
        quickSort(archi, 0, archi.size() - 1);

        // Insieme per memorizzare gli archi che fanno parte dell'albero di copertura minimo
        Set<GraphEdge<L>> archiACM = new HashSet<GraphEdge<L>>();

        // Itero attraverso gli archi ordinati
        for(GraphEdge<L> arco : archi) {
            // Ottengo i due nodi connessi dall'arco
            GraphNode<L> u = arco.getNode1();
            GraphNode<L> v = arco.getNode2();
            
            // Se i due nodi appartengono a insiemi disgiunti diversi
            if(this.disjointSets.findSet(u) != this.disjointSets.findSet(v)) {
                // Aggiungo l'arco all'insieme dell'ACM
                archiACM.add(arco);
                // Unisco i due insiemi disgiunti
                this.disjointSets.union(u, v);
            }
        }

        // Restituisco l'insieme degli archi che costituiscono l'albero di copertura minimo
        return archiACM;
    }

    /**
     * Metodo per ordinare una lista di archi utilizzando l'algoritmo Quicksort
     * 
     * @param archi
     *                lista degli archi da ordinare
     * @param low
     *                indice inferiore
     * @param high
     *                indice superiore
     */
    private void quickSort(List<GraphEdge<L>> archi, int left, int right) {
    	// Controllo se la porzione di lista è valida per essere ordinata
        if(left < right) {
        	// Calcolo la posizione del pivot e partiziono la lista
            int p = partition(archi, left, right);
            // Eseguo ricorsivamente il quickSort sulla parte sinistra
            quickSort(archi, left, p - 1);
            // Eseguo ricorsivamente il quickSort sulla parte destra
            quickSort(archi, p + 1, right);
        }
    }

    /**
     * Metodo per partizionare la lista per l'algoritmo Quicksort
     * 
     * @param archi
     *                lista degli archi da partizionare
     * @param low
     *                indice inferiore
     * @param high
     *                indice superiore
     * @return indice di partizione
     */
    private int partition(List<GraphEdge<L>> archi, int left, int right) {
    	// Scelgo l'elemento pivot come l'ultimo elemento della lista
        double pivot = archi.get(right).getWeight();
        // Inizializzo l'indice per gli elementi minori del pivot
        int i = left - 1;

        // Itero sulla porzione della lista per posizionare gli elementi rispetto al pivot
        for(int j = left; j < right; j++) {
        	// Verifico se l'elemento corrente è minore del pivot
            if(archi.get(j).getWeight() <= pivot) {
            	// Incremento l'indice e scambio gli elementi
                i++;
                swap(archi, i, j);
            }
        }

        // Scambio il pivot con l'elemento nella posizione corretta
        swap(archi, i + 1, right);

        // Ritorno l'indice del pivot
        return i + 1;
    }
    
    /**
     * Metodo per scambiare due elementi in una lista di archi
     * 
     * @param archi
     *                lista degli archi
     * @param i
     *                primo indice
     * @param j
     *                secondo indice
     */
    private void swap(List<GraphEdge<L>> archi, int i, int j) {
    	// Salvo temporaneamente l'elemento nella posizione j
        GraphEdge<L> temp = archi.get(j);
        // Posiziono l'elemento nella posizione i in j
        archi.set(j, archi.get(i));
        // Posiziono l'elemento temporaneo in i
        archi.set(i, temp);
    }
}
