from pathlib import Path


def _importar_matplotlib():
    try:
        import matplotlib.pyplot as plt
    except ModuleNotFoundError as exc:
        raise ModuleNotFoundError(
            "matplotlib nao esta instalado. Instale com 'pip install matplotlib'."
        ) from exc

    return plt


def _ordenar_medicoes(medicoes):
    return sorted(medicoes, key=lambda medicao: medicao[0])


def _nome_seguro(nome):
    caracteres = []

    for caractere in nome.lower():
        if caractere.isalnum():
            caracteres.append(caractere)
        else:
            caracteres.append("_")

    nome_limpo = "".join(caracteres).strip("_")
    while "__" in nome_limpo:
        nome_limpo = nome_limpo.replace("__", "_")

    return nome_limpo or "algoritmo"


def gerar_grafico_desempenho(
    resultados,
    caminho_saida,
    titulo="Desempenho dos algoritmos",
    eixo_x="Numero de vertices",
    eixo_y="Tempo (ms)",
):
    """
    Gera um grafico de linhas comparando o desempenho dos algoritmos.

    Espera um dicionario no formato:
    {
        "Tarjan": [(100, 0.7), (1000, 3.2), (10000, 41.8)],
        "Naive": [(100, 8.2), (1000, 90.1), (10000, 1200.4)],
        "Fleury + Tarjan": [(100, 1.4), (1000, 4.1)]
    }

    Em cada tupla:
    - o primeiro valor representa o tamanho da entrada
    - o segundo valor representa o tempo de execucao em milissegundos
    """
    plt = _importar_matplotlib()

    if not resultados:
        raise ValueError("Nenhum resultado foi informado para o grafico.")

    caminho_saida = Path(caminho_saida)
    caminho_saida.parent.mkdir(parents=True, exist_ok=True)

    plt.figure(figsize=(10, 6))

    for algoritmo, medicoes in resultados.items():
        if not medicoes:
            continue

        medicoes_ordenadas = _ordenar_medicoes(medicoes)
        tamanhos = [medicao[0] for medicao in medicoes_ordenadas]
        tempos = [medicao[1] for medicao in medicoes_ordenadas]

        plt.plot(
            tamanhos,
            tempos,
            marker="o",
            linewidth=2,
            markersize=5,
            label=algoritmo,
        )

    plt.title(titulo)
    plt.xlabel(eixo_x)
    plt.ylabel(eixo_y)
    plt.grid(True, linestyle="--", alpha=0.4)
    plt.legend()
    plt.tight_layout()
    plt.savefig(caminho_saida, dpi=200)
    plt.close()

    return caminho_saida


def gerar_graficos_por_algoritmo(
    resultados,
    diretorio_saida,
    eixo_x="Numero de vertices",
    eixo_y="Tempo (ms)",
):
    """
    Gera um grafico individual para cada algoritmo, comparando o tempo
    entre diferentes quantidades de vertices.

    Retorna um dicionario no formato:
    {
        "Tarjan": Path("graficos/tarjan.png"),
        "Naive": Path("graficos/naive.png"),
    }
    """
    plt = _importar_matplotlib()

    if not resultados:
        raise ValueError("Nenhum resultado foi informado para os graficos.")

    diretorio_saida = Path(diretorio_saida)
    diretorio_saida.mkdir(parents=True, exist_ok=True)

    arquivos_gerados = {}

    for algoritmo, medicoes in resultados.items():
        if not medicoes:
            continue

        medicoes_ordenadas = _ordenar_medicoes(medicoes)
        tamanhos = [medicao[0] for medicao in medicoes_ordenadas]
        tempos = [medicao[1] for medicao in medicoes_ordenadas]
        caminho_saida = diretorio_saida / f"{_nome_seguro(algoritmo)}.png"

        plt.figure(figsize=(10, 6))
        plt.plot(
            tamanhos,
            tempos,
            marker="o",
            linewidth=2,
            markersize=5,
            color="#1f77b4",
        )
        plt.title(f"Desempenho do algoritmo {algoritmo}")
        plt.xlabel(eixo_x)
        plt.ylabel(eixo_y)
        plt.grid(True, linestyle="--", alpha=0.4)
        plt.tight_layout()
        plt.savefig(caminho_saida, dpi=200)
        plt.close()

        arquivos_gerados[algoritmo] = caminho_saida

    return arquivos_gerados


def gerar_relatorio_graficos(
    resultados,
    diretorio_saida="graficos",
):
    """
    Gera:
    - um grafico geral comparando todos os algoritmos
    - um grafico individual para cada algoritmo ao longo dos tamanhos de entrada
    """
    diretorio_saida = Path(diretorio_saida)
    diretorio_saida.mkdir(parents=True, exist_ok=True)

    grafico_geral = gerar_grafico_desempenho(
        resultados,
        diretorio_saida / "comparativo_geral.png",
    )

    graficos_individuais = gerar_graficos_por_algoritmo(
        resultados,
        diretorio_saida / "por_algoritmo",
    )

    return {
        "comparativo_geral": grafico_geral,
        "por_algoritmo": graficos_individuais,
    }


if __name__ == "__main__":
    exemplo_resultados = {
        "Tarjan": [(100, 0.7), (1000, 3.2), (10000, 41.8)],
        "Naive": [(100, 8.2), (1000, 90.1), (10000, 1200.4)],
        "Fleury + Tarjan": [(100, 1.4), (1000, 4.1), (10000, 75.0)],
        "Fleury + Naive": [(100, 13.4), (1000, 210.0), (10000, 5200.0)],
    }

    destino = gerar_relatorio_graficos(
        exemplo_resultados,
        "graficos",
    )
    print(f"Graficos salvos em: {destino}")
