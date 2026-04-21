import random
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent


def criar_grafo_vazio(n):
    return [set() for _ in range(n)]


def adicionar_aresta(grafo, u, v):
    """
    Adiciona uma aresta em um grafo simples não direcionado.
    Retorna True se adicionou, False caso contrário.
    """
    if u == v:
        return False
    if v in grafo[u]:
        return False

    grafo[u].add(v)
    grafo[v].add(u)
    return True


def gerar_base_conexa_aleatoria(n):
    """
    Gera uma base conexa e euleriana:
    um ciclo Hamiltoniano aleatório sobre todos os vértices.
    Todo vértice fica com grau 2.
    """
    if n < 3:
        raise ValueError("Para este gerador, use n >= 3.")

    grafo = criar_grafo_vazio(n)
    perm = list(range(n))
    random.shuffle(perm)

    for i in range(n):
        u = perm[i]
        v = perm[(i + 1) % n]
        adicionar_aresta(grafo, u, v)

    return grafo


def escolher_aresta_ausente(grafo, proibidos=None, max_tentativas=20000):
    """
    Escolhe aleatoriamente uma aresta que ainda não existe no grafo.
    O conjunto 'proibidos' impede o uso de certos vértices, se necessário.
    """
    n = len(grafo)
    if proibidos is None:
        proibidos = set()

    for _ in range(max_tentativas):
        u = random.randrange(n)
        v = random.randrange(n)

        if u == v:
            continue
        if u in proibidos or v in proibidos:
            continue
        if v not in grafo[u]:
            return u, v

    # fallback determinístico, caso a busca aleatória falhe
    for u in range(n):
        if u in proibidos:
            continue
        for v in range(u + 1, n):
            if v in proibidos:
                continue
            if v not in grafo[u]:
                return u, v

    raise RuntimeError("Não foi possível encontrar uma aresta ausente válida.")


def adicionar_triangulos_aleatorios(grafo, quantidade, max_tentativas_por_triangulo=500):
    """
    Adiciona triângulos aleatórios ao grafo.
    Isso preserva a paridade dos graus:
    cada vértice do triângulo recebe +2 no grau.
    Portanto, um grafo euleriano continua euleriano.
    Um semi-euleriano continua semi-euleriano.
    Um não euleriano continua não euleriano.
    """
    n = len(grafo)
    adicionados = 0
    tentativas = 0
    limite_total = max(quantidade * max_tentativas_por_triangulo, 1)

    while adicionados < quantidade and tentativas < limite_total:
        a, b, c = random.sample(range(n), 3)
        tentativas += 1

        # os 3 lados do triângulo precisam ser arestas ausentes
        if b in grafo[a]:
            continue
        if c in grafo[a]:
            continue
        if c in grafo[b]:
            continue

        adicionar_aresta(grafo, a, b)
        adicionar_aresta(grafo, b, c)
        adicionar_aresta(grafo, c, a)
        adicionados += 1

    return adicionados


def contar_vertices_impares(grafo):
    return sum(1 for vizinhos in grafo if len(vizinhos) % 2 != 0)


def eh_conexo(grafo):
    """
    Verifica conectividade com DFS iterativa.
    """
    n = len(grafo)
    visitado = [False] * n
    pilha = [0]
    visitado[0] = True
    total = 1

    while pilha:
        u = pilha.pop()
        for v in grafo[u]:
            if not visitado[v]:
                visitado[v] = True
                pilha.append(v)
                total += 1

    return total == n


def classificar_grafo(grafo):
    """
    Classifica o grafo de acordo com os graus ímpares.
    Considerando que o gerador sempre produz grafos conexos:
    - 0 ímpares => euleriano
    - 2 ímpares => semi-euleriano
    - >2 ímpares => não euleriano
    """
    if not eh_conexo(grafo):
        return "desconexo"

    impares = contar_vertices_impares(grafo)

    if impares == 0:
        return "euleriano"
    elif impares == 2:
        return "semi-euleriano"
    else:
        return "não euleriano"


def validar_grafo(grafo, classe_esperada):
    """
    Faz checagens de segurança.
    """
    if not eh_conexo(grafo):
        raise ValueError("O grafo gerado não é conexo.")

    classe = classificar_grafo(grafo)
    if classe != classe_esperada:
        raise ValueError(
            f"Grafo inválido: esperado '{classe_esperada}', mas obtido '{classe}'."
        )


def gerar_grafo_euleriano(n, triangulos_extras=None):
    """
    Gera grafo conexo e euleriano.
    Base: ciclo Hamiltoniano aleatório.
    Depois adiciona triângulos aleatórios para variar a estrutura
    sem alterar a paridade dos graus.
    """
    grafo = gerar_base_conexa_aleatoria(n)

    if triangulos_extras is None:
        triangulos_extras = max(5, n // 200)

    adicionar_triangulos_aleatorios(grafo, triangulos_extras)
    validar_grafo(grafo, "euleriano")
    return grafo


def gerar_grafo_semi_euleriano(n, triangulos_extras=None):
    """
    Gera grafo conexo e semi-euleriano.
    Estratégia:
    1) gera um euleriano;
    2) adiciona uma aresta ausente;
       isso troca a paridade de exatamente 2 vértices.
    """
    grafo = gerar_grafo_euleriano(n, triangulos_extras)

    u, v = escolher_aresta_ausente(grafo)
    adicionar_aresta(grafo, u, v)

    validar_grafo(grafo, "semi-euleriano")
    return grafo


def gerar_grafo_nao_euleriano(n, triangulos_extras=None):
    """
    Gera grafo conexo e não euleriano.
    Estratégia:
    1) gera um euleriano;
    2) adiciona duas arestas ausentes com 4 extremidades distintas;
       isso cria exatamente 4 vértices ímpares.
    """
    grafo = gerar_grafo_euleriano(n, triangulos_extras)

    u1, v1 = escolher_aresta_ausente(grafo)
    adicionar_aresta(grafo, u1, v1)

    proibidos = {u1, v1}
    u2, v2 = escolher_aresta_ausente(grafo, proibidos=proibidos)
    adicionar_aresta(grafo, u2, v2)

    validar_grafo(grafo, "não euleriano")
    return grafo


def salvar_em_txt(grafo, nome_arquivo):
    """
    Salva no formato:
    V E
    u v
    u v
    ...
    """
    n = len(grafo)
    arestas = []

    for u in range(n):
        for v in grafo[u]:
            if u < v:
                arestas.append((u, v))

    caminho_saida = BASE_DIR / nome_arquivo

    with caminho_saida.open("w", encoding="utf-8") as f:
        f.write(f"{n} {len(arestas)}\n")
        for u, v in arestas:
            f.write(f"{u} {v}\n")


def gerar_todos():
    tamanhos = [100, 1000, 10000, 100000]

    for n in tamanhos:
        print(f"\nGerando instâncias para n = {n}...")

        g_euler = gerar_grafo_euleriano(n)
        nome_arquivo = f"grafo_euleriano_{n}.txt"
        salvar_em_txt(g_euler, nome_arquivo)
        print(
            f"  {nome_arquivo} -> "
            f"{classificar_grafo(g_euler)}, "
            f"ímpares = {contar_vertices_impares(g_euler)}"
        )

        g_semi = gerar_grafo_semi_euleriano(n)
        nome_arquivo = f"grafo_semi_{n}.txt"
        salvar_em_txt(g_semi, nome_arquivo)
        print(
            f"  {nome_arquivo} -> "
            f"{classificar_grafo(g_semi)}, "
            f"ímpares = {contar_vertices_impares(g_semi)}"
        )

        g_nao = gerar_grafo_nao_euleriano(n)
        nome_arquivo = f"grafo_nao_{n}.txt"
        salvar_em_txt(g_nao, nome_arquivo)
        print(
            f"  {nome_arquivo} -> "
            f"{classificar_grafo(g_nao)}, "
            f"ímpares = {contar_vertices_impares(g_nao)}"
        )


if __name__ == "__main__":
    random.seed()  # pode trocar por um número fixo, ex.: 42, para reproduzir os mesmos grafos
    gerar_todos()
