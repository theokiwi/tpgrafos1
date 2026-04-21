package Tarjan;

import common.Aresta;
import common.Grafo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class TarjanBridgeFinder {
    private final Grafo grafo;

    public TarjanBridgeFinder(Grafo grafo) {
        this.grafo = grafo;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            exibirPontesDoArquivo(args[0]);
            return;
        }

        Scanner sc = new Scanner(System.in);
        String x = "s";
        while (x.equals("s")) {
            boolean loop = true;
            while (loop) {
                System.out.print("Digite o nome do arquivo (ou 'exit' para sair): ");
                String nomeArquivo = sc.nextLine();
                if (nomeArquivo.equals("exit")) {
                    sc.close();
                    return;
                }
                try {
                    exibirPontesDoArquivo(nomeArquivo);
                    loop = false;
                } catch (Exception e) {
                    System.out.println(
                        "Confira o nome do arquivo. Voce pode informar so o nome do .txt ou um caminho completo."
                    );
                }
            }

            System.out.println();

            System.out.print("Deseja testar outro grafo? (s/n): ");
            x = sc.nextLine().trim().toLowerCase();
            boolean burro = false;
            if (!x.equals("s") && !x.equals("n")) {
                burro = true;
            }
            while (burro) {
                System.out.println("Nao entendi.");
                System.out.print("Deseja testar outro grafo? (s/n): ");
                x = sc.nextLine().trim().toLowerCase();
                if (x.equals("s") || x.equals("n")) {
                    burro = false;
                }
            }
        }
        sc.close();
    }

    public Set<Aresta> findBridges() {
        return new Tarjan(grafo).findBridges();
    }

    public static TarjanBridgeFinder fromFile(String fileName) {
        Grafo grafo = new Grafo();
        grafo.leGrafo(fileName);
        return new TarjanBridgeFinder(grafo);
    }

    public static Set<Aresta> findBridges(Grafo grafo) {
        return new TarjanBridgeFinder(grafo).findBridges();
    }

    public static Set<Aresta> findBridgesFromFile(String fileName) {
        return fromFile(fileName).findBridges();
    }

    private static void exibirPontesDoArquivo(String nomeArquivo) {
        Set<Aresta> pontes = findBridgesFromFile(nomeArquivo);

        System.out.println("\n======== Pontes do Grafo ========");
        if (pontes.isEmpty()) {
            System.out.println("Este grafo nao possui pontes.");
            return;
        }

        for (Aresta ponte : pontes) {
            System.out.println("{" + ponte.u + ", " + ponte.v + "}");
        }
    }
}

class Tarjan {
    private final Grafo g;
    private int timer;
    private final int[] discovery;
    private final int[] low;
    private final boolean[] visited;
    private final Set<Aresta> bridges;

    public Tarjan(Grafo g) {
        this.g = g;
        this.timer = 0;
        this.discovery = new int[g.getQntdVertices()];
        this.low = new int[g.getQntdVertices()];
        this.visited = new boolean[g.getQntdVertices()];
        this.bridges = new LinkedHashSet<>();

        for (int i = 0; i < g.getQntdVertices(); i++) {
            this.discovery[i] = -1;
            this.low[i] = Integer.MAX_VALUE;
            this.visited[i] = false;
        }
    }

    public Set<Aresta> findBridges() {
        for (int i = 0; i < g.getQntdVertices(); i++) {
            if (!visited[i]) {
                tarjanDFS(i, -1);
            }
        }

        ArrayList<Aresta> orderedBridges = new ArrayList<>(bridges);
        Collections.sort(orderedBridges);
        return new LinkedHashSet<>(orderedBridges);
    }

    private void tarjanDFS(int u, int parent) {
        visited[u] = true;
        discovery[u] = low[u] = ++timer;

        for (int i = g.getPointer().get(u); i < g.getPointer().get(u + 1); i++) {
            Aresta aresta = g.getArestaDoArco(i);
            if (!aresta.isAtiva()) {
                continue;
            }

            int v = g.getArcDest().get(i);

            if (v == parent) {
                continue;
            }
            if (visited[v]) {
                low[u] = Math.min(low[u], discovery[v]);
            } else {
                tarjanDFS(v, u);
                low[u] = Math.min(low[u], low[v]);

                if (low[v] > discovery[u]) {
                    bridges.add(new Aresta(u, v));
                }
            }
        }
    }
}
