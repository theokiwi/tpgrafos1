package common;

import java.util.Objects;

public class Vertice implements Comparable<Vertice> {

    public final int valor;

    public Vertice(int valor) {
        this.valor = valor;
    }

    // Serve pra comparar valores de vertices
    @Override
    public int compareTo(Vertice other) {
        return Integer.compare(this.valor, other.valor);
    }

    // Verifica se dois vertices representam o mesmo valor
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vertice other)) {
            return false;
        }
        return this.valor == other.valor;
    }

    // Faz a estrutura funcionar com hash junto do equals
    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    // Transforma em string
    @Override
    public String toString() {
        return "(" + valor + ")";
    }
}
