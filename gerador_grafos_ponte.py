import random

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
    with open(nome_arquivo, "w") as f:
        f.write(f"{n_vertices} {len(arestas)}\n")
        f.write("\n".join(arestas))
    print(f"Ficheiro '{nome_arquivo}' gerado com {n_vertices} vértices e {n_pontes} pontes.")

# Gerar os três ficheiros solicitados
gerar_grafo_com_pontes("grafo_ponte_100.txt", 100, 1)
gerar_grafo_com_pontes("grafo_ponte_1000.txt", 1000, 3)
gerar_grafo_com_pontes("grafo_ponte_10000.txt", 10000, 5)