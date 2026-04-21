package Fleury;

import NaiveBridge.NaiveBridgeFinder;
import Tarjan.TarjanBridgeFinder;
import common.Aresta;
import common.Grafo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

public class Fleury {
    public List<Integer> FleuryNaive(Grafo inicial) {
        return fleuryNaive(inicial);
    }

    public List<Integer> fleuryNaive(Grafo inicial) {
        return executarFleury(inicial, false);
    }

    public List<Integer> fleuryTarjan(Grafo inicial) {
        return executarFleury(inicial, true);
    }

    private List<Aresta> arestasIncidentes(Grafo g, int v) {
        Set<Aresta> incidentesUnicas = new LinkedHashSet<>();
        int[] pointer = g.getPointer();

        for (int i = pointer[v]; i < pointer[v + 1]; i++) {
            Aresta aresta = g.getArestaDoArco(i);
            if (aresta.isAtiva()) {
                incidentesUnicas.add(aresta);
            }
        }

        List<Aresta> incidentes = new ArrayList<>(incidentesUnicas);
        Collections.sort(incidentes);
        return incidentes;
    }

    private int outroExtremo(Aresta a, int v) {
        return (a.u == v) ? a.v : a.u;
    }

    public Set<Aresta> importaPontesNaive(Grafo grafo) {
        return NaiveBridgeFinder.findBridges(grafo);
    }

    public Set<Aresta> importaPontesTarjan(Grafo grafo) {
        return TarjanBridgeFinder.findBridges(grafo);
    }

    private int escolheInicio(Grafo g) {
        for (int i = 0; i < g.getQntdVertices(); i++) {
            if (g.ehImpar(i)) {
                return i;
            }
        }
        for (int i = 0; i < g.getQntdVertices(); i++) {
            if (g.consultaGrau(i) > 0) {
                return i;
            }
        }
        throw new IllegalStateException("Grafo sem arestas.");
    }

    private List<Integer> executarFleury(Grafo inicial, boolean usarTarjan) {
        String tipo = new Euleriano().tipoEuleriano(inicial);
        if (tipo.equals("Nao Euleriano")) {
            throw new IllegalArgumentException("O grafo informado nao possui caminho ou ciclo euleriano.");
        }

        Grafo auxiliar = new Grafo(inicial);
        List<Integer> caminhoVertices = new ArrayList<>();
        int atual = escolheInicio(auxiliar);
        caminhoVertices.add(atual);

        while (auxiliar.getQntdArestas() > 0) {
            List<Aresta> incidentes = arestasIncidentes(auxiliar, atual);
            if (incidentes.isEmpty()) {
                throw new IllegalStateException("Nao ha aresta disponivel a partir do vertice atual.");
            }

            Aresta escolhida;
            if (incidentes.size() == 1) {
                escolhida = incidentes.get(0);
            } else {
                Set<Aresta> pontes = usarTarjan
                    ? importaPontesTarjan(auxiliar)
                    : importaPontesNaive(auxiliar);
                escolhida = escolheNaoPonte(incidentes, pontes);
            }

            atual = outroExtremo(escolhida, atual);
            caminhoVertices.add(atual);
            auxiliar.removeAresta(escolhida);
        }

        return caminhoVertices;
    }

    private Aresta escolheNaoPonte(List<Aresta> incidentes, Set<Aresta> pontes) {
        for (Aresta aresta : incidentes) {
            if (!pontes.contains(aresta)) {
                return aresta;
            }
        }
        return incidentes.get(0);
    }
}

class Euleriano {
    public String tipoEuleriano(Grafo grafo) {
        int impares = 0;
        for (int i = 0; i < grafo.getQntdVertices(); i++) {
            if (grafo.ehImpar(i)) {
                impares++;
            }
        }

        if (impares == 0) {
            return "Ciclo Euleriano";
        } else if (impares == 2) {
            return "Caminho Euleriano";
        } else {
            return "Nao Euleriano";
        }
    }
}
