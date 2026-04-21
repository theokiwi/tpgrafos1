package common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

// obs: todos os grafos foram gerados na ordem crescente de par de vertices
public class Grafo {
    private static final String EXEMPLOS_DIR = "exemplosGrafos";

    //Guarda as ligações com forward star
    private int[] pointer;
    private int[] arcDest;
    private final List<Aresta> arcArestas;
    //Guarda os vertices e suas informações
    private final Set<Vertice> vertices;
    //Guarda as arestas e suas informações lidando com arestas repetidas
    private final Set<Aresta> arestas;
    private final Map<Aresta, Aresta> indiceArestas;
    //Guarda os graus de cada vertice
    private int[] graus;
    private int qntdVertices;
    private int qntdArestas;

    public Grafo() {
        this.pointer = new int[0];
        this.arcDest = new int[0];
        this.arcArestas = new ArrayList<>();
        this.vertices = new HashSet<>();
        this.arestas = new HashSet<>();
        this.indiceArestas = new HashMap<>();
        this.qntdVertices = 0;
        this.qntdArestas = 0;
    }

    public Grafo(Grafo outro) {
        this.pointer = outro.pointer.clone();
        this.arcDest = outro.arcDest.clone();
        this.arcArestas = new ArrayList<>();
        this.vertices = new HashSet<>(outro.vertices);
        this.arestas = new HashSet<>();
        this.indiceArestas = new HashMap<>();
        for (Aresta aresta : outro.arestas) {
            Aresta copia = new Aresta(aresta.u, aresta.v, aresta.ativa);
            this.arestas.add(copia);
            this.indiceArestas.put(copia, copia);
        }
        for (Aresta arestaDoArco : outro.arcArestas) {
            this.arcArestas.add(this.indiceArestas.get(arestaDoArco));
        }
        this.graus = outro.graus.clone();
        this.qntdVertices = outro.qntdVertices;
        this.qntdArestas = outro.qntdArestas;
    }

    public void leGrafo(String arquivo) {
        pointer = new int[0];
        arcDest = new int[0];
        arcArestas.clear();
        vertices.clear();
        arestas.clear();
        indiceArestas.clear();

        try (Scanner sc = new Scanner(resolverArquivo(arquivo))) {
            qntdVertices = sc.nextInt();
            qntdArestas = sc.nextInt();
            this.graus = new int[qntdVertices];

            for (int i = 0; i < qntdVertices; i++) {
                adicionaVertices(i);
            }

            while (sc.hasNextInt()) {
                int origem = sc.nextInt();
                if (!sc.hasNextInt()) {
                    throw new IOException("Aresta incompleta no arquivo.");
                }
                int destino = sc.nextInt();
                adicionaAresta(origem, destino);
            }
            preencheGraus();
            montaForwardStar();
            qntdArestas = arestas.size();
        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao ler arquivo: " + e.getMessage(), e);
        }
    }

    //como eles são sempre adicionados na ordem
    public void adicionaVertices(int origem) {
        vertices.add(new Vertice(origem));
    }

    public void adicionaAresta(int origem, int destino) {
        Aresta aresta = new Aresta(origem, destino);
        if (indiceArestas.containsKey(aresta)) {
            return;
        }
        arestas.add(aresta);
        indiceArestas.put(aresta, aresta);
    }

    public void removeAresta(Aresta aresta) {
        Aresta arestaReal = indiceArestas.get(aresta);
        if (arestaReal == null || !arestaReal.isAtiva()) {
            return;
        }

        arestaReal.desativar();
        graus[arestaReal.u]--;
        graus[arestaReal.v]--;
        qntdArestas--;
    }

    public void preencheGraus() {
        for (Aresta aresta : arestas) {
            this.graus[aresta.u]++;
            this.graus[aresta.v]++;
        }
    }

    public int consultaGrau(int vertice) {
        return graus[vertice];
    }

    public boolean ehImpar(int vertice) {
        return graus[vertice] % 2 != 0;
    }

    public int[] getPointer() {
        return pointer;
    }

    public int[] getArcDest() {
        return arcDest;
    }

    public int getQntdVertices() {
        return qntdVertices;
    }

    public int getQntdArestas() {
        return qntdArestas;
    }

    public Set<Aresta> getArestas() {
        Set<Aresta> arestasAtivas = new HashSet<>();
        for (Aresta aresta : arestas) {
            if (aresta.isAtiva()) {
                arestasAtivas.add(aresta);
            }
        }
        return Collections.unmodifiableSet(arestasAtivas);
    }

    public Aresta getArestaDoArco(int indice) {
        return arcArestas.get(indice);
    }

    private void montaForwardStar() {
        //pra cada vértice eu guardo seus adjacentes
        List<List<Integer>> adjacencias = new ArrayList<>();
        //lista das arestas que eu sei que existem
        List<Aresta> arestasOrdenadas = new ArrayList<>(arestas);
        List<Integer> destinosTemporarios = new ArrayList<>(arestas.size() * 2);
        arcArestas.clear();
        pointer = new int[qntdVertices + 1];

        //inicializa a lista das adjacencias pra cada vertice
        for (int i = 0; i < qntdVertices; i++) {
            adjacencias.add(new ArrayList<>());
        }

        //ordeno as arestas que eu sei que existem
        Collections.sort(arestasOrdenadas);
        //adiciono as arestas nos dois sentidos, tipo se (u,v) v é vizinho de u e u vizinho de v
        for (Aresta aresta : arestasOrdenadas) {
            adjacencias.get(aresta.u).add(aresta.v);
            adjacencias.get(aresta.v).add(aresta.u);
        }

        //ordeno a lista de vizinhos de cada vertice
        for (List<Integer> vizinhos : adjacencias) {
            Collections.sort(vizinhos);
        }

        //faço os vetores de pointer e arcDest conforme o forwardstar
        for (int vertice = 0; vertice < qntdVertices; vertice++) {
            pointer[vertice] = destinosTemporarios.size();
            for (int destino : adjacencias.get(vertice)) {
                destinosTemporarios.add(destino);
                arcArestas.add(indiceArestas.get(new Aresta(vertice, destino)));
            }
        }
        pointer[qntdVertices] = destinosTemporarios.size();
        arcDest = new int[destinosTemporarios.size()];
        for (int i = 0; i < destinosTemporarios.size(); i++) {
            arcDest[i] = destinosTemporarios.get(i);
        }
    }

    private File resolverArquivo(String arquivo) throws FileNotFoundException {
        Path[] candidatos = new Path[] {
            Paths.get(arquivo),
            Paths.get(EXEMPLOS_DIR, arquivo),
            Paths.get("..", EXEMPLOS_DIR, arquivo)
        };

        for (Path candidato : candidatos) {
            Path caminhoNormalizado = candidato.normalize().toAbsolutePath();
            if (Files.isRegularFile(caminhoNormalizado)) {
                return caminhoNormalizado.toFile();
            }
        }

        throw new FileNotFoundException("Arquivo nao encontrado: " + arquivo);
    }
}
