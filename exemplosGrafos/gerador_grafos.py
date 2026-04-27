import heapq
import random


def criar_grafo_vazio(n):
    return [set() for _ in range(n)]


def adicionar_aresta(grafo, u, v):
    """
    Adiciona uma aresta em um grafo simples nao direcionado.
    Retorna True se adicionou, False caso contrario.
    """
    if u == v:
        return False
    if v in grafo[u]:
        return False

    grafo[u].add(v)
    grafo[v].add(u)
    return True


def adicionar_ciclo_aleatorio(grafo, vertices):
    """
    Adiciona um ciclo simples usando exatamente os vertices informados.
    """
    if len(vertices) < 3:
        raise ValueError("Cada bloco deve ter pelo menos 3 vertices.")

    ordem = list(vertices)
    random.shuffle(ordem)

    for i in range(len(ordem)):
        u = ordem[i]
        v = ordem[(i + 1) % len(ordem)]
        adicionar_aresta(grafo, u, v)


def gerar_base_conexa_aleatoria(n):
    """
    Gera uma base conexa e euleriana:
    um ciclo Hamiltoniano aleatorio sobre todos os vertices.
    Todo vertice fica com grau 2.
    """
    if n < 3:
        raise ValueError("Para este gerador, use n >= 3.")

    grafo = criar_grafo_vazio(n)
    adicionar_ciclo_aleatorio(grafo, range(n))
    return grafo


def escolher_aresta_ausente(grafo, proibidos=None, max_tentativas=20000):
    """
    Escolhe aleatoriamente uma aresta que ainda nao existe no grafo.
    O conjunto 'proibidos' impede o uso de certos vertices, se necessario.
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

    for u in range(n):
        if u in proibidos:
            continue
        for v in range(u + 1, n):
            if v in proibidos:
                continue
            if v not in grafo[u]:
                return u, v

    raise RuntimeError("Nao foi possivel encontrar uma aresta ausente valida.")


def escolher_quantidade_blocos(n, minimo_blocos):
    """
    Define quantos blocos ciclicos o gerador vai usar.
    Mais blocos significam mais pontes entre blocos.
    """
    maximo_por_tamanho = n // 3
    if maximo_por_tamanho < minimo_blocos:
        raise ValueError(
            f"Nao e possivel gerar {minimo_blocos} blocos com apenas {n} vertices."
        )

    quantidade = max(minimo_blocos, n // 200)
    quantidade = min(quantidade, 500, maximo_por_tamanho)
    return quantidade


def particionar_vertices_em_blocos(n, quantidade_blocos, tamanho_minimo=3):
    """
    Particiona os vertices em blocos disjuntos, cada um com pelo menos 3 vertices.
    """
    if quantidade_blocos * tamanho_minimo > n:
        raise ValueError("Nao ha vertices suficientes para formar os blocos pedidos.")

    vertices = list(range(n))
    random.shuffle(vertices)

    tamanhos = [tamanho_minimo] * quantidade_blocos
    restante = n - quantidade_blocos * tamanho_minimo

    for _ in range(restante):
        tamanhos[random.randrange(quantidade_blocos)] += 1

    blocos = []
    inicio = 0
    for tamanho in tamanhos:
        fim = inicio + tamanho
        blocos.append(vertices[inicio:fim])
        inicio = fim

    random.shuffle(blocos)
    return blocos


def criar_blocos_ciclicos(n, quantidade_blocos):
    """
    Cria blocos que, individualmente, nao possuem pontes.
    As pontes do grafo final sao exatamente as arestas que conectam blocos distintos.
    """
    grafo = criar_grafo_vazio(n)
    blocos = particionar_vertices_em_blocos(n, quantidade_blocos)
    ancoras = []

    for bloco in blocos:
        adicionar_ciclo_aleatorio(grafo, bloco)
        ancoras.append(random.choice(bloco))

    return grafo, blocos, ancoras


def conectar_blocos(grafo, ancoras, arestas_blocos):
    """
    Liga blocos distintos por arestas de ponte.
    """
    for a, b in arestas_blocos:
        adicionar_aresta(grafo, ancoras[a], ancoras[b])


def gerar_arvore_aleatoria(num_vertices):
    """
    Gera uma arvore aleatoria usando a codificacao de Prufer.
    """
    if num_vertices < 1:
        return []
    if num_vertices == 1:
        return []
    if num_vertices == 2:
        return [(0, 1)]

    prufer = [random.randrange(num_vertices) for _ in range(num_vertices - 2)]
    grau = [1] * num_vertices

    for x in prufer:
        grau[x] += 1

    folhas = [i for i in range(num_vertices) if grau[i] == 1]
    heapq.heapify(folhas)
    arestas = []

    for x in prufer:
        folha = heapq.heappop(folhas)
        arestas.append((folha, x))
        grau[folha] -= 1
        grau[x] -= 1

        if grau[x] == 1:
            heapq.heappush(folhas, x)

    u = heapq.heappop(folhas)
    v = heapq.heappop(folhas)
    arestas.append((u, v))
    return arestas


def contar_vertices_grau_impar_em_arvore(num_vertices, arestas):
    graus = [0] * num_vertices

    for u, v in arestas:
        graus[u] += 1
        graus[v] += 1

    return sum(1 for grau in graus if grau % 2 != 0)


def gerar_arvore_nao_caminho(num_vertices, max_tentativas=200):
    """
    Gera uma arvore que nao seja caminho, de forma a induzir mais de 2 vertices impares.
    """
    if num_vertices < 4:
        raise ValueError("Para a classe nao euleriana com pontes, use pelo menos 4 blocos.")

    for _ in range(max_tentativas):
        arestas = gerar_arvore_aleatoria(num_vertices)
        if contar_vertices_grau_impar_em_arvore(num_vertices, arestas) > 2:
            return arestas

    return [(0, i) for i in range(1, num_vertices)]


def adicionar_triangulos_aleatorios(grafo, quantidade, max_tentativas_por_triangulo=500):
    """
    Adiciona triangulos aleatorios ao grafo.
    Isso preserva a paridade dos graus:
    cada vertice do triangulo recebe +2 no grau.
    """
    n = len(grafo)
    adicionados = 0
    tentativas = 0
    limite_total = max(quantidade * max_tentativas_por_triangulo, 1)

    while adicionados < quantidade and tentativas < limite_total:
        a, b, c = random.sample(range(n), 3)
        tentativas += 1

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


def adicionar_triangulos_em_blocos(grafo, blocos, quantidade, max_tentativas_por_triangulo=500):
    """
    Adiciona triangulos extras apenas dentro dos blocos, preservando as pontes entre blocos.
    """
    blocos_viaveis = [bloco for bloco in blocos if len(bloco) >= 3]
    if not blocos_viaveis:
        return 0

    adicionados = 0
    tentativas = 0
    limite_total = max(quantidade * max_tentativas_por_triangulo, 1)

    while adicionados < quantidade and tentativas < limite_total:
        bloco = random.choice(blocos_viaveis)
        a, b, c = random.sample(bloco, 3)
        tentativas += 1

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
    if n == 0:
        return True

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
    Classifica o grafo de acordo com os graus impares.
    Considerando grafos conexos:
    - 0 impares => euleriano
    - 2 impares => semi-euleriano
    - >2 impares => nao euleriano
    """
    if not eh_conexo(grafo):
        return "desconexo"

    impares = contar_vertices_impares(grafo)

    if impares == 0:
        return "euleriano"
    if impares == 2:
        return "semi-euleriano"
    return "nao euleriano"


def validar_grafo(grafo, classe_esperada):
    """
    Faz checagens de seguranca.
    """
    if not eh_conexo(grafo):
        raise ValueError("O grafo gerado nao e conexo.")

    classe = classificar_grafo(grafo)
    if classe != classe_esperada:
        raise ValueError(
            f"Grafo invalido: esperado '{classe_esperada}', mas obtido '{classe}'."
        )


def gerar_grafo_euleriano(n, triangulos_extras=None):
    """
    Gera grafo conexo e euleriano.
    Observacao importante: grafo conectado e euleriano nao pode ter ponte.
    """
    grafo = gerar_base_conexa_aleatoria(n)

    if triangulos_extras is None:
        triangulos_extras = max(5, n // 200)

    adicionar_triangulos_aleatorios(grafo, triangulos_extras)
    validar_grafo(grafo, "euleriano")
    return grafo


def gerar_grafo_semi_euleriano(n, triangulos_extras=None):
    """
    Gera grafo conexo e semi-euleriano com pontes.

    Estrategia:
    1) cria blocos ciclicos;
    2) conecta os blocos em cadeia com pontes;
    3) opcionalmente adiciona triangulos apenas dentro dos blocos.

    Como a "arvore de blocos" e um caminho, apenas os dois blocos extremos
    contribuem com vertices de grau impar. O resultado final tem exatamente
    2 vertices impares e varias pontes reais.
    """
    quantidade_blocos = escolher_quantidade_blocos(n, minimo_blocos=3)
    grafo, blocos, ancoras = criar_blocos_ciclicos(n, quantidade_blocos)
    arestas_blocos = [(i, i + 1) for i in range(quantidade_blocos - 1)]
    conectar_blocos(grafo, ancoras, arestas_blocos)

    if triangulos_extras is None:
        triangulos_extras = max(2, n // 500)

    adicionar_triangulos_em_blocos(grafo, blocos, triangulos_extras)
    validar_grafo(grafo, "semi-euleriano")
    return grafo


def gerar_grafo_nao_euleriano(n, triangulos_extras=None):
    """
    Gera grafo conexo e nao euleriano com pontes.

    Estrategia:
    1) cria blocos ciclicos;
    2) conecta os blocos por uma arvore aleatoria que nao seja caminho;
    3) opcionalmente adiciona triangulos apenas dentro dos blocos.

    O numero de vertices impares passa a ser maior que 2, e as conexoes
    entre blocos continuam sendo pontes reais.
    """
    quantidade_blocos = escolher_quantidade_blocos(n, minimo_blocos=4)
    grafo, blocos, ancoras = criar_blocos_ciclicos(n, quantidade_blocos)
    arestas_blocos = gerar_arvore_nao_caminho(quantidade_blocos)
    conectar_blocos(grafo, ancoras, arestas_blocos)

    if triangulos_extras is None:
        triangulos_extras = max(2, n // 500)

    adicionar_triangulos_em_blocos(grafo, blocos, triangulos_extras)
    validar_grafo(grafo, "nao euleriano")
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

    with open(nome_arquivo, "w", encoding="utf-8") as f:
        f.write(f"{n} {len(arestas)}\n")
        for u, v in arestas:
            f.write(f"{u} {v}\n")


def gerar_todos():
    tamanhos = [100, 1000, 10000, 100000]

    for n in tamanhos:
        print(f"\nGerando instancias para n = {n}...")

        g_euler = gerar_grafo_euleriano(n)
        salvar_em_txt(g_euler, f"grafo_euleriano_{n}.txt")
        print(
            f"  grafo_euleriano_{n}.txt -> "
            f"{classificar_grafo(g_euler)}, "
            f"impares = {contar_vertices_impares(g_euler)}"
        )

        g_semi = gerar_grafo_semi_euleriano(n)
        salvar_em_txt(g_semi, f"grafo_semi_{n}.txt")
        print(
            f"  grafo_semi_{n}.txt -> "
            f"{classificar_grafo(g_semi)}, "
            f"impares = {contar_vertices_impares(g_semi)}"
        )

        g_nao = gerar_grafo_nao_euleriano(n)
        salvar_em_txt(g_nao, f"grafo_nao_{n}.txt")
        print(
            f"  grafo_nao_{n}.txt -> "
            f"{classificar_grafo(g_nao)}, "
            f"impares = {contar_vertices_impares(g_nao)}"
        )


if __name__ == "__main__":
    random.seed()
    gerar_todos()
