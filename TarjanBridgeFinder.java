// import java.io.RandomAccessFile;
// import java.util.Scanner;

import java.io.RandomAccessFile;
import java.util.Scanner;

public class TarjanBridgeFinder {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String x = "s";
        while(x.equals("s")){
            boolean loop = true;
            RandomAccessFile arq = new RandomAccessFile("error.txt", "rw");
            while (loop) {
                System.out.print("Digite o nome do arquivo (ou 'exit' para sair): ");
                String nomeArquivo = sc.nextLine();
                if (nomeArquivo.equals("exit")) {
                    return;
                }
                try {
                    arq = new RandomAccessFile(nomeArquivo, "r");
                    loop = false;
                } catch (Exception e) {
                    System.out.println("Confira o nome do arquivo.");
                }
            }
            String s = arq.readLine();
            String buffer[] = s.split("\\s+");
            int v = Integer.parseInt(buffer[0]);
            int e = Integer.parseInt(buffer[1]);
            Grafo g = new Grafo(v);
            for (int i = 0; i < e; i++) {
                s = arq.readLine();
                s = s.trim();
                String buffer2[] = s.split("\\s+");
                int pai = Integer.parseInt(buffer2[0]);
                int filho = Integer.parseInt(buffer2[1]);
                g.addAresta(pai, filho);
            }
            // g.imprimirArestas();
            Tarjan tarjan = new Tarjan(g);
            tarjan.start();

            System.out.println();

            System.out.print("Deseja testar outro grafo? (s/n): ");
            x = sc.nextLine().trim().toLowerCase();
            boolean burro = false;
            if(!x.equals("s") && !x.equals("n")){
                burro = true;
            }
            while(burro == true){
                System.out.println("Nao entendi.");
                System.out.print("Deseja testar outro grafo? (s/n): ");
                x = sc.nextLine().trim().toLowerCase();
                if(x.equals("s") || x.equals("n")){
                    burro = false;
                }
            }
        }
        sc.close();
    }
}

class No {
    int num;
    No prox;

    public No(int v) {
        this.num = v;
        this.prox = null;
    }
}

class Lista {
    No inicio;

    public Lista() {
        this.inicio = new No(-1);
    }


    public void inserirNo(int v) {
        No novo = new No(v);
        novo.prox = inicio.prox;
        inicio.prox = novo;
    }

    public void infoLista(int v) {
        No atual = inicio.prox;
        for (; atual != null; atual = atual.prox) {
            System.out.println("{" + (v) + ", " + (atual.num) + "}");
        }
        System.out.println();
    }
}

class Grafo {
    public int n;
    public Lista adj[];

    public Grafo(int v) {
        this.n = v;
        this.adj = new Lista[n];
        for (int i = 0; i < v; i++) {
            adj[i] = new Lista();
        }
    }

    public void addAresta(int pai, int filho) {
        adj[pai].inserirNo(filho);
        adj[filho].inserirNo(pai);
    }

    public void imprimirArestas() {
        System.out.println("Arestas do Grafo:");
        for (int i = 0; i < n; i++) {
            // Pula o nó cabeça 'inicio' e percorre os vizinhos
            No atual = adj[i].inicio.prox;
            while (atual != null) {
                // Condição u < v para não imprimir a mesma aresta duas vezes
                if (i < atual.num) {
                    System.out.println("{" + i + ", " + atual.num + "}");
                }
                atual = atual.prox;
            }
        }
    }
}

class Tarjan {
    Grafo g;
    int timer;
    int discovery[]; // Tempo de entrada na DFS
    int low[]; // Menor tempo de descoberta alcançável
    boolean visited[]; // Controle de visitação
    ListaArestas bridges;

    public Tarjan(Grafo g) {
        this.g = g;
        this.timer = 0;
        this.discovery = new int[g.n]; // Tempo de entrada na DFS
        this.low = new int[g.n]; // Menor tempo de descoberta alcançável
        this.visited = new boolean[g.n]; // Controle de visitação
        this.bridges = new ListaArestas();
        for (int i = 0; i < g.n; i++) {
            this.discovery[i] = -1;
            this.low[i] = Integer.MAX_VALUE;
            this.visited[i] = false;
        }
    }

    public void start() {
        for (int i = 0; i < g.n; i++) {
            if (!this.visited[i]) {
                this.tarjanDFS(i, -1);
            }
        }

        System.out.println("\n======== Pontes do Grafo ========");

        bridges.infoLista();
    }

    public void tarjanDFS(int u, int parent) {
        visited[u] = true;
        discovery[u] = low[u] = ++timer;

        for (No v = g.adj[u].inicio.prox; v != null; v = v.prox) {
            if (v.num == parent){
                continue;
            }
            if (visited[v.num]) {
                low[u] = (low[u] < discovery[v.num]) ? (low[u]) : (discovery[v.num]);
            } else {
                tarjanDFS(v.num, u);

                low[u] = (low[u] < low[v.num]) ? (low[u]) : (low[v.num]);

                if (low[v.num] > discovery[u]) {
                    bridges.inserirNo(u, v.num);
                }
            }
        }
    }
}

class Aresta {
    int v, w;
    Aresta prox;

    public Aresta(int v, int w) {
        this.v = v;
        this.w = w;
        this.prox = null;
    }
}

class ListaArestas {
    Aresta inicio;
    int num;

    public ListaArestas() {
        this.inicio = new Aresta(-1, -1);
        this.num = 0;
    }

    public void inserirNo(int v, int w) {
        Aresta nova = new Aresta(v, w);
        nova.prox = inicio.prox;
        inicio.prox = nova;
        this.num++;
    }

    public void infoLista() {
        Aresta atual = inicio.prox;
        if(atual == null){
            System.out.println("Este grafo nao possui pontes.");
        }
        for (; atual != null; atual = atual.prox) {
            System.out.println("{" + (atual.v) + ", " + (atual.w) + "}");
        }
    }
}