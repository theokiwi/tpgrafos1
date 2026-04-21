# Projeto de Grafos

<p align="center">
  <img src="https://img.shields.io/badge/Java-Algorithms-orange?style=for-the-badge&logo=openjdk" alt="Java badge" />
  <img src="https://img.shields.io/badge/Python-Support%20Scripts-blue?style=for-the-badge&logo=python" alt="Python badge" />
  <img src="https://img.shields.io/badge/Graphs-Forward%20Star-0a7ea4?style=for-the-badge" alt="Forward Star badge" />
  <img src="https://img.shields.io/badge/Status-Academic%20Project-2ea44f?style=for-the-badge" alt="Status badge" />
</p>

<p align="center">
  Implementacao em Java de algoritmos classicos de grafos, com foco em deteccao de pontes,
  classificacao euleriana e execucao do algoritmo de Fleury sobre instancias de teste.
</p>

## Visao Geral

Este repositorio reune uma implementacao de estruturas e algoritmos estudados na disciplina de Grafos.
O projeto trabalha com grafos nao direcionados e usa uma representacao baseada em `forward star`
para armazenar adjacencias de forma compacta.

Os principais componentes implementados sao:

- `Tarjan`: deteccao de pontes usando DFS com vetores `discovery` e `low`.
- `Naive`: deteccao de pontes por comparacao do numero de componentes conexas.
- `Fleury`: construcao de caminho ou ciclo euleriano, evitando pontes quando possivel.
- `Grafo`: leitura de instancias, armazenamento das arestas e montagem do `forward star`.
- Scripts em Python para geracao de instancias e visualizacao de desempenho.

## O Que o Projeto Faz

O sistema permite:

- Ler arquivos `.txt` com instancias de grafos.
- Executar Tarjan para encontrar pontes.
- Executar o metodo Naive para encontrar pontes.
- Executar Fleury apoiado por Tarjan ou por Naive.
- Classificar grafos como `Ciclo Euleriano`, `Caminho Euleriano` ou `Nao Euleriano`.
- Gerar e organizar conjuntos de exemplos para testes.
- Gerar graficos de desempenho a partir dos resultados.

## Estrutura do Repositorio

```text
.
|-- Main.java
|-- common/
|   |-- Grafo.java
|   |-- Aresta.java
|   `-- Vertice.java
|-- Tarjan/
|   `-- TarjanBridgeFinder.java
|-- NaiveBridge/
|   `-- NaiveBridgeFinder.java
|-- Fleury/
|   `-- Fleury.java
|-- exemplosGrafos/
|   |-- gerador_grafos.py
|   |-- gerador_grafos_ponte.py
|   `-- grafo_*.txt
|-- graficos/
|   |-- comparativo_geral.png
|   `-- por_algoritmo/
`-- graficos_desempenho.py
```

## Representacao do Grafo

O projeto usa uma combinacao de estruturas para manter a implementacao organizada:

- `pointer` e `arcDest` representam a adjacencia com `forward star`.
- `arcArestas` associa cada arco do `forward star` a sua aresta real.
- `arestas` guarda o conjunto de arestas unicas do grafo.
- `indiceArestas` permite recuperar rapidamente a aresta correspondente ao par `(u, v)`.
- `graus` armazena o grau de cada vertice para apoiar a classificacao euleriana.

Essa escolha deixa a travessia mais simples para os algoritmos, especialmente nos casos em que
precisamos percorrer vizinhos e, ao mesmo tempo, consultar o estado da aresta associada.

## Algoritmos Implementados

### Tarjan

Detecta pontes em tempo linear sobre a estrutura do grafo usando busca em profundidade.
Cada vertice recebe um tempo de descoberta e um valor `low`, o que permite verificar se uma
aresta e uma ponte.

### Naive

Testa se uma aresta e ponte comparando o numero de componentes conexas antes e depois de sua
remocao logica. E mais simples conceitualmente, mas tende a ser mais custoso.

### Fleury

Constroi um caminho euleriano ou ciclo euleriano removendo arestas gradualmente.
Quando ha mais de uma opcao de movimento, o algoritmo tenta evitar pontes para nao quebrar
o restante do percurso.

## Arquivos de Exemplo

A pasta [`exemplosGrafos`](./exemplosGrafos) contem instancias prontas para testes, incluindo:

- grafos eulerianos
- grafos semi-eulerianos
- grafos nao eulerianos
- grafos com pontes

Os tamanhos atualmente presentes no repositorio incluem:

- `100`
- `1000`
- `10000`
- `100000`

## Como Compilar

Requisito minimo:

- JDK instalado e disponivel no terminal

Compilacao:

```bash
javac Main.java Fleury/*.java NaiveBridge/*.java Tarjan/*.java common/*.java
```

## Como Executar

Executar o menu principal:

```bash
java Main
```

Pelo menu, voce pode:

1. executar Tarjan
2. executar Naive
3. executar Fleury com Naive
4. executar Fleury com Tarjan

Tambem e possivel executar algumas classes diretamente:

```bash
java NaiveBridge.NaiveBridgeFinder exemplosGrafos/grafo_ponte_100.txt
java Tarjan.TarjanBridgeFinder exemplosGrafos/grafo_ponte_100.txt
```

## Scripts Auxiliares

### Geracao de grafos

Gerar grafos eulerianos, semi-eulerianos e nao eulerianos:

```bash
python3 exemplosGrafos/gerador_grafos.py
```

Gerar grafos com pontes:

```bash
python3 exemplosGrafos/gerador_grafos_ponte.py
```

### Graficos de desempenho

O script [`graficos_desempenho.py`](./graficos_desempenho.py) organiza a geracao de graficos
a partir de medicoes de tempo em milissegundos.

## Saidas Visuais

Comparativo geral ja presente no repositorio:

![Comparativo geral](./graficos/comparativo_geral.png)

## Pontos de Destaque

- Estrutura de dados enxuta para adjacencia com `forward star`
- Dois metodos diferentes para deteccao de pontes
- Integracao entre deteccao de pontes e algoritmo de Fleury
- Conjunto de exemplos escalando ate `100000` vertices
- Scripts auxiliares para experimentos e relatorio

## Proximos Passos Possiveis

- Automatizar a coleta de tempos em lote para todos os grafos
- Exportar resultados em CSV para uso direto em relatorios
- Adicionar testes automatizados para validar as saidas dos algoritmos
- Criar comparativos de memoria alem do tempo de execucao

## Autores

Projeto desenvolvido para a disciplina de Grafos.

- Theo D. Viana
- Alex C. M. Marques
- Welbert J. A. de Almeida

## Link para o relatório no overleaf
https://www.overleaf.com/read/ckhjnyfrscvz#a3c47b
