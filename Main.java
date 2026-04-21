import Fleury.Fleury;
import NaiveBridge.NaiveBridgeFinder;
import Tarjan.TarjanBridgeFinder;
import common.Aresta;
import common.Grafo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private static final Path EXEMPLOS_DIR = Paths.get("exemplosGrafos");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            imprimirMenu();
            int opcao = lerOpcao(scanner);

            if (opcao == 0) {
                System.out.println("Encerrando.");
                scanner.close();
                return;
            }

            List<String> arquivos = escolherArquivos(scanner);
            if (arquivos.isEmpty()) {
                System.out.println("Nenhum arquivo selecionado.");
                System.out.println();
                continue;
            }

            for (String arquivo : arquivos) {
                executarOpcao(opcao, arquivo);
            }

            System.out.println();
        }
    }

    private static void imprimirMenu() {
        System.out.println("===== Menu de Execucao =====");
        System.out.println("1. Executar Tarjan");
        System.out.println("2. Executar Naive");
        System.out.println("3. Executar Fleury com Naive");
        System.out.println("4. Executar Fleury com Tarjan");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opcao: ");
    }

    private static int lerOpcao(Scanner scanner) {
        while (true) {
            String entrada = scanner.nextLine().trim();
            try {
                int opcao = Integer.parseInt(entrada);
                if (opcao >= 0 && opcao <= 4) {
                    return opcao;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.print("Opcao invalida. Digite um numero entre 0 e 4: ");
        }
    }

    private static List<String> escolherArquivos(Scanner scanner) {
        List<String> arquivosDisponiveis = listarArquivosExemplo();

        System.out.println();
        System.out.println("Arquivos disponiveis em exemplosGrafos:");
        for (int i = 0; i < arquivosDisponiveis.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, arquivosDisponiveis.get(i));
        }
        System.out.println("Digite numeros separados por virgula, 'all' para todos,");
        System.out.print("ou informe um caminho manual para um arquivo .txt: ");

        while (true) {
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.print("Entrada vazia. Tente novamente: ");
                continue;
            }

            if (entrada.equalsIgnoreCase("all")) {
                return arquivosDisponiveis;
            }

            if (entrada.matches("\\d+(\\s*,\\s*\\d+)*")) {
                try {
                    return converterSelecaoEmArquivos(entrada, arquivosDisponiveis);
                } catch (IllegalArgumentException e) {
                    System.out.print(e.getMessage() + " Tente novamente: ");
                    continue;
                }
            }

            return List.of(entrada);
        }
    }

    private static List<String> listarArquivosExemplo() {
        try {
            if (!Files.isDirectory(EXEMPLOS_DIR)) {
                return List.of();
            }

            try (var stream = Files.list(EXEMPLOS_DIR)) {
                return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .map(path -> path.getFileName().toString())
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel listar os arquivos em exemplosGrafos.", e);
        }
    }

    private static List<String> converterSelecaoEmArquivos(String entrada, List<String> arquivosDisponiveis) {
        List<String> arquivos = new ArrayList<>();
        String[] partes = entrada.split(",");

        for (String parte : partes) {
            int indice = Integer.parseInt(parte.trim()) - 1;
            if (indice < 0 || indice >= arquivosDisponiveis.size()) {
                throw new IllegalArgumentException("Indice fora da lista.");
            }
            arquivos.add(arquivosDisponiveis.get(indice));
        }

        return arquivos;
    }

    private static void executarOpcao(int opcao, String arquivo) {
        System.out.println();
        System.out.println("===== Arquivo: " + arquivo + " =====");

        try {
            Grafo grafo = new Grafo();
            grafo.leGrafo(arquivo);

            switch (opcao) {
                case 1 -> executarTarjan(grafo);
                case 2 -> executarNaive(grafo);
                case 3 -> executarFleuryNaive(grafo);
                case 4 -> executarFleuryTarjan(grafo);
                default -> throw new IllegalStateException("Opcao inesperada.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao executar: " + e.getMessage());
        }
    }

    private static void executarTarjan(Grafo grafo) {
        long inicio = System.nanoTime();
        Set<Aresta> pontes = TarjanBridgeFinder.findBridges(grafo);
        long fim = System.nanoTime();

        imprimirResumoGrafo(grafo);
        System.out.println("Algoritmo: Tarjan");
        System.out.println("Pontes encontradas: " + pontes.size());
        System.out.printf("Tempo de execucao: %.3f ms%n", (fim - inicio) / 1_000_000.0);
        System.out.println("Saida: " + pontes);
    }

    private static void executarNaive(Grafo grafo) {
        long inicio = System.nanoTime();
        Set<Aresta> pontes = NaiveBridgeFinder.findBridges(grafo);
        long fim = System.nanoTime();

        imprimirResumoGrafo(grafo);
        System.out.println("Algoritmo: Naive");
        System.out.println("Pontes encontradas: " + pontes.size());
        System.out.printf("Tempo de execucao: %.3f ms%n", (fim - inicio) / 1_000_000.0);
        System.out.println("Saida: " + pontes);
    }

    private static void executarFleuryNaive(Grafo grafo) {
        Fleury fleury = new Fleury();
        long inicio = System.nanoTime();
        List<Integer> caminho = fleury.fleuryNaive(grafo);
        long fim = System.nanoTime();

        imprimirResumoGrafo(grafo);
        System.out.println("Algoritmo: Fleury com Naive");
        System.out.println("Tipo euleriano: " + tipoEuleriano(grafo));
        System.out.println("Numero de vertices no caminho: " + caminho.size());
        System.out.printf("Tempo de execucao: %.3f ms%n", (fim - inicio) / 1_000_000.0);
        System.out.println("Saida: " + caminho);
    }

    private static void executarFleuryTarjan(Grafo grafo) {
        Fleury fleury = new Fleury();
        long inicio = System.nanoTime();
        List<Integer> caminho = fleury.fleuryTarjan(grafo);
        long fim = System.nanoTime();

        imprimirResumoGrafo(grafo);
        System.out.println("Algoritmo: Fleury com Tarjan");
        System.out.println("Tipo euleriano: " + tipoEuleriano(grafo));
        System.out.println("Numero de vertices no caminho: " + caminho.size());
        System.out.printf("Tempo de execucao: %.3f ms%n", (fim - inicio) / 1_000_000.0);
        System.out.println("Saida: " + caminho);
    }

    private static void imprimirResumoGrafo(Grafo grafo) {
        System.out.println("Numero de vertices: " + grafo.getQntdVertices());
        System.out.println("Numero de arestas: " + grafo.getQntdArestas());
    }

    private static String tipoEuleriano(Grafo grafo) {
        int impares = 0;

        for (int i = 0; i < grafo.getQntdVertices(); i++) {
            if (grafo.ehImpar(i)) {
                impares++;
            }
        }

        if (impares == 0) {
            return "Ciclo Euleriano";
        }
        if (impares == 2) {
            return "Caminho Euleriano";
        }
        return "Nao Euleriano";
    }
}
