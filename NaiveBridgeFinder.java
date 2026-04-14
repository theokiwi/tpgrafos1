import java.io.*;
import java.util.*;

/*
  Metodo naive para deteccao de pontes em grafo nao direcionado.
 */
public class NaiveBridgeFinder {

    private final int V;
    private int E;
    private final List<Set<Integer>> adj;

    public NaiveBridgeFinder(int V) {
        if (V < 0) {
            throw new IllegalArgumentException("Numero de vertices deve ser nao negativo.");
        }
        this.V = V;
        this.E = 0;
        this.adj = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            adj.add(new HashSet<>());
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        if (u == v) {
            return; // ignora laco
        }

        if (!adj.get(u).contains(v)) {
            adj.get(u).add(v);
            adj.get(v).add(u);
            E++;
        }
    }

    public void removeEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        if (adj.get(u).contains(v)) {
            adj.get(u).remove(v);
            adj.get(v).remove(u);
            E--;
        }
    }

    public boolean hasEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);
        return adj.get(u).contains(v);
    }

    public List<Aresta> getAllEdges() {
        List<Aresta> edges = new ArrayList<>();

        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                if (u < v) {
                    edges.add(new Aresta(u, v));
                }
            }
        }

        return edges;
    }

    /**
     * Conta o numero de componentes conexas do grafo usando Busca em Profundidade iterativa.
     */
    public int countConnectedComponents() {
        boolean[] visited = new boolean[V];
        int components = 0;

        for (int start = 0; start < V; start++) {
            if (!visited[start]) {
                components++;
                dfsIterative(start, visited);
            }
        }

        return components;
    }

    private void dfsIterative(int start, boolean[] visited) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;

        while (!stack.isEmpty()) {
            int u = stack.pop();

            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    stack.push(v);
                }
            }
        }
    }

    /**
      Verifica se a aresta (u, v) e ponte.
       Metodo naive
       conta componentes do grafo original
       remove a aresta
       conta componentes novamente
       restaura a aresta
     */
    public boolean isBridgeNaive(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        if (!hasEdge(u, v)) {
            return false;
        }

        int before = countConnectedComponents();

        removeEdge(u, v);
        int after = countConnectedComponents();
        addEdge(u, v);

        return after > before;
    }

    /**
      Retorna todas as pontes do grafo usando o metodo naive.
      executado para cada aresta
     
     */
    public List<Aresta> findBridgesNaive() {
        List<Aresta> bridges = new ArrayList<>();
        List<Aresta> edges = getAllEdges();
        int baseComponents = countConnectedComponents();

        for (Aresta e : edges) {
            removeEdge(e.u, e.v);
            int newComponents = countConnectedComponents();
            addEdge(e.u, e.v);

            if (newComponents > baseComponents) {
                bridges.add(e);
            }
        }

        Collections.sort(bridges);
        return bridges;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException(
                "Vertice " + v + " invalido. Deve estar entre 0 e " + (V - 1) + "."
            );
        }
    }

    /**
      Leitura de arquivo no formato
      primeira linha: V E
      proximas E linhas: u v
     */
    public static NaiveBridgeFinder fromFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String firstLine = br.readLine();
            if (firstLine == null) {
                throw new IOException("Arquivo vazio.");
            }

            String[] header = firstLine.trim().split("\\s+");
            if (header.length < 2) {
                throw new IOException("Formato invalido na primeira linha.");
            }

            int V = Integer.parseInt(header[0]);
            int E = Integer.parseInt(header[1]);

            NaiveBridgeFinder graph = new NaiveBridgeFinder(V);

            for (int i = 0; i < E; i++) {
                String line = br.readLine();
                if (line == null) {
                    throw new IOException("Arquivo terminou antes do esperado.");
                }

                String[] parts = line.trim().split("\\s+");
                if (parts.length < 2) {
                    throw new IOException("Linha de aresta invalida: " + line);
                }

                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                graph.addEdge(u, v);
            }

            return graph;
        }
    }

    public static class Aresta implements Comparable<Aresta> {
        int u;
        int v;

        public Aresta(int u, int v) {
            if (u <= v) {
                this.u = u;
                this.v = v;
            } else {
                this.u = v;
                this.v = u;
            }
        }

        @Override
        public int compareTo(Aresta other) {
            if (this.u != other.u) {
                return Integer.compare(this.u, other.u);
            }
            return Integer.compare(this.v, other.v);
        }

        @Override
        public String toString() {
            return "(" + u + ", " + v + ")";
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java NaiveBridgeFinder <arquivo.txt>");
            return;
        }

        try {
            NaiveBridgeFinder graph = NaiveBridgeFinder.fromFile(args[0]);

            long inicio = System.nanoTime();
            List<Aresta> bridges = graph.findBridgesNaive();
            long fim = System.nanoTime();

            System.out.println("Numero de vertices: " + graph.V());
            System.out.println("Numero de arestas: " + graph.E());
            System.out.println("Pontes encontradas: " + bridges.size());
            double tempoMs = (fim - inicio) / 1_000_000.0;
            System.out.printf("Tempo de execucao: %.3f ms%n", tempoMs);

            for (Aresta e : bridges) {
                System.out.println(e);
            }

            //tempoMs = (fim - inicio) / 1_000_000.0;
            //System.out.printf("Tempo de execucao: %.3f ms%n", tempoMs);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}