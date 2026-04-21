package NaiveBridge;

import common.Aresta;
import common.Grafo;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
  Metodo naive para deteccao de pontes em grafo nao direcionado.
 */
public class NaiveBridgeFinder {
    private final Grafo grafo;

    public NaiveBridgeFinder(Grafo grafo) {
        this.grafo = grafo;
    }

    public int V() {
        return grafo.getQntdVertices();
    }

    public int E() {
        return grafo.getQntdArestas();
    }

    public boolean hasEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        return grafo.getArestas().contains(new Aresta(u, v));
    }

    public int countConnectedComponents() {
        return countConnectedComponents(null);
    }

    private int countConnectedComponents(Aresta ignoredEdge) {
        boolean[] visited = new boolean[V()];
        int components = 0;

        for (int start = 0; start < V(); start++) {
            if (!visited[start]) {
                components++;
                dfsIterative(start, visited, ignoredEdge);
            }
        }

        return components;
    }

    private void dfsIterative(int start, boolean[] visited, Aresta ignoredEdge) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;

        while (!stack.isEmpty()) {
            int u = stack.pop();

            for (int i = grafo.getPointer().get(u); i < grafo.getPointer().get(u + 1); i++) {
                Aresta aresta = grafo.getArestaDoArco(i);
                if (!aresta.isAtiva()) {
                    continue;
                }

                int v = grafo.getArcDest().get(i);

                if (ignoredEdge != null && aresta.equals(ignoredEdge)) {
                    continue;
                }

                if (!visited[v]) {
                    visited[v] = true;
                    stack.push(v);
                }
            }
        }
    }

    public boolean isBridgeNaive(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        Aresta edge = new Aresta(u, v);
        if (!grafo.getArestas().contains(edge)) {
            return false;
        }

        int before = countConnectedComponents();
        int after = countConnectedComponents(edge);
        return after > before;
    }

    public Set<Aresta> findBridgesNaive() {
        int baseComponents = countConnectedComponents();
        List<Aresta> edges = new ArrayList<>(grafo.getArestas());
        Collections.sort(edges);

        Set<Aresta> bridges = new LinkedHashSet<>();
        for (Aresta edge : edges) {
            if (countConnectedComponents(edge) > baseComponents) {
                bridges.add(edge);
            }
        }

        return bridges;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V()) {
            throw new IllegalArgumentException(
                "Vertice " + v + " invalido. Deve estar entre 0 e " + (V() - 1) + "."
            );
        }
    }

    public static NaiveBridgeFinder fromFile(String fileName) {
        Grafo grafo = new Grafo();
        grafo.leGrafo(fileName);
        return new NaiveBridgeFinder(grafo);
    }

    public static Set<Aresta> findBridges(Grafo grafo) {
        return new NaiveBridgeFinder(grafo).findBridgesNaive();
    }

    public static Set<Aresta> findBridgesFromFile(String fileName) {
        return fromFile(fileName).findBridgesNaive();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java NaiveBridge.NaiveBridgeFinder <arquivo.txt>");
            return;
        }

        try {
            NaiveBridgeFinder graph = NaiveBridgeFinder.fromFile(args[0]);

            long inicio = System.nanoTime();
            Set<Aresta> bridges = graph.findBridgesNaive();
            long fim = System.nanoTime();

            System.out.println("Numero de vertices: " + graph.V());
            System.out.println("Numero de arestas: " + graph.E());
            System.out.println("Pontes encontradas: " + bridges.size());
            double tempoMs = (fim - inicio) / 1_000_000.0;
            System.out.printf("Tempo de execucao: %.3f ms%n", tempoMs);

            for (Aresta edge : bridges) {
                System.out.println(edge);
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
