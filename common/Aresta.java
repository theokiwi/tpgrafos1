package common;

import java.util.Objects;

// To usando comparable pra dar pra ordenar
public class Aresta implements Comparable<Aresta> {
    public final int u;
    public final int v;
    boolean ativa;
    boolean ponte;

    public Aresta(int u, int v) {
        // faz com que pares sejam a mesma coisa independente da ordem, tipo (11, 92) = (11, 92)
        if (u <= v) {
            this.u = u;
            this.v = v;
        } else {
            this.u = v;
            this.v = u;
        }
        this.ativa = true;
        this.ponte = false;
    }
    
    public Aresta(int u, int v, boolean ativa, boolean ponte) {
        if (u <= v) {
            this.u = u;
            this.v = v;
        } else {
            this.u = v;
            this.v = u;
        }
        this.ativa = ativa;
        this.ponte = ponte;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void desativar() {
        this.ativa = false;
    }

    public void ativar() {
        this.ativa = true;
    }

    //Serve pra ver qual aresta vem antes da outra em uma ordenação
    @Override
    public int compareTo(Aresta other) {
        if (this.u != other.u) {
            return Integer.compare(this.u, other.u);
        }
        return Integer.compare(this.v, other.v);
    }

    //Verifica se duas arestas são iguais, olhando se tem o mesmo extermo
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Aresta other)) {
            return false;
        }
        return this.u == other.u && this.v == other.v;
    }

    // Faz a estrutura funcionar com hash (parte do comparable)
    @Override
    public int hashCode() {
        return Objects.hash(u, v);
    }

    // Transforma em string
    @Override
    public String toString() {
        return "(" + u + ", " + v + ")";
    }
}
