import random
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent


def gerar_grafo_com_pontes(nome_arquivo, n_vertices, n_pontes):
    # Dividir os vértices em (n_pontes + 1) blocos
    tamanho_bloco = n_vertices // (n_pontes + 1)
    arestas = []
    
    blocos = []
    for i in range(n_pontes + 1):
        inicio = i * tamanho_bloco
        fim = (i + 1) * tamanho_bloco if i < n_pontes else n_vertices
        blocos.append(list(range(inicio, fim)))
    
    # Criar um ciclo dentro de cada bloco para garantir que não existam pontes internas
    for bloco in blocos:
        if len(bloco) > 2:
            for i in range(len(bloco)):
                u = bloco[i]
                v = bloco[(i + 1) % len(bloco)]
                arestas.append(f"{u} {v}")
        elif len(bloco) == 2:
            arestas.append(f"{bloco[0]} {bloco[1]}")

    # Adicionar as pontes entre os blocos
    for i in range(n_pontes):
        # Escolhe um vértice aleatório do bloco atual e do próximo
        u = random.choice(blocos[i])
        v = random.choice(blocos[i+1])
        arestas.append(f"{u} {v}")

    # Escrever no ficheiro
    caminho_saida = BASE_DIR / nome_arquivo

    with caminho_saida.open("w", encoding="utf-8") as f:
        f.write(f"{n_vertices} {len(arestas)}\n")
        f.write("\n".join(arestas))
    print(f"Ficheiro '{caminho_saida.name}' gerado com {n_vertices} vértices e {n_pontes} pontes.")


def gerar_todos():
    configuracoes = [
        (100, 1),
        (1000, 3),
        (10000, 5),
        (100000, 7),
    ]

    for n_vertices, n_pontes in configuracoes:
        gerar_grafo_com_pontes(
            f"grafo_ponte_{n_vertices}.txt",
            n_vertices,
            n_pontes,
        )


if __name__ == "__main__":
    random.seed()
    gerar_todos()
