# Simulador de Escalonamento de Processos

Trabalho de Sistemas Operacionais. O programa lê arquivos de texto com uma lista de processos
(originalmente de um pendrive fornecido pelo professor), executa quatro algoritmos de
escalonamento de CPU e grava as médias de cada um em um arquivo de resultado.

**Autores:** Gabrielle Reis e Pierre Vinícius

## Algoritmos

| Algoritmo   | Arquivo                |
|-------------|------------------------|
| FIFO        | `src/Fifo.java`        |
| SJF         | `src/Sjf.java`         |
| SRT         | `src/Srt.java`         |
| Round Robin | `src/RoundRobin.java`  |

`src/Processo.java` representa um processo e `src/Main.java` faz a leitura, a execução e a gravação.

## Como rodar

Requer o JDK 11 ou superior (testado com `javac`/`java` no PATH).

```bash
# 1. Compilar
javac -d out src/*.java

# 2. Executar apontando para a pasta com os arquivos TESTE-XX.txt
java -cp out Main exemplos
```

O argumento é o diretório onde estão os arquivos de entrada. **Sem argumento**, o programa usa
`E:/` (o pendrive do professor no Windows), que é o comportamento original do trabalho:

```bash
java -cp out Main            # lê E:/TESTE-01.txt ... E:/TESTE-10.txt
java -cp out Main D:/pendrive
java -cp out Main /media/usuario/PENDRIVE
```

## Formato de entrada

O programa procura de `TESTE-01.txt` até `TESTE-10.txt` no diretório. Arquivos ausentes são
ignorados com um aviso. Em cada arquivo:

- **Primeira linha:** o quantum (usado no Round Robin).
- **Demais linhas:** `chegada execucao`, um processo por linha.

Exemplo (`exemplos/TESTE-01.txt`):

```
2
0 5
1 3
2 8
3 6
```

## Formato de saída

Para cada `TESTE-XX.txt` é criado `TESTE-XX-RESULTADO.txt` **no mesmo diretório**, com quatro
linhas (FIFO, SJF, SRT, Round Robin, nessa ordem). Cada linha tem três médias:
`resposta espera turnaround`, com vírgula decimal e três casas.

Saída para o exemplo acima:

```
5,750 5,750 11,250
5,250 5,250 10,750
4,250 5,000 10,500
2,000 9,750 15,250
```

Se um arquivo de entrada for inválido (número mal formatado, dados faltando), o resultado contém
`ERRO NO PROCESSAMENTO` e a descrição do problema.

## Exemplos

A pasta `exemplos/` traz 10 entradas de exemplo criadas para este repositório (não são os
arquivos do professor). Os arquivos `*-RESULTADO.txt` são gerados na própria pasta onde você roda
e estão no `.gitignore`. Para não sujar `exemplos/`, copie-a antes: `cp -r exemplos meus-testes`.
