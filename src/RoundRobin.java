import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RoundRobin{
    private final int[][] matriz;
    private double mediaEspera;
    private double mediaResposta;
    private double mediaTurnaround;

    // Inicializa a matriz e começa a execução do algoritmo controlando a alternância com base no quantum
    public RoundRobin(int quantidade, int tempoTotal, List<Processo> processos, int quantum){
        matriz = new int[quantidade][tempoTotal];
        executar(processos, quantum);
        calcularDados(processos);
        calcularMedias(processos);
    }
    // Controla a fila circular inserindo os últimos a chegar, retira processos que esgotaram o quantum ou terminaram, e passa a CPU para o próximo
    private void executar(List<Processo> processos, int quantum){
        Queue<Integer> fila = new ArrayDeque<>();
        boolean[] entrouNaFila = new boolean[processos.size()];
        int processoAtual = -1;
        int restanteQuantum = 0;
        for(int tempo = 0; tempo < matriz[0].length; tempo++){
            colocarChegadosNaFila(processos, fila, entrouNaFila, tempo);
            if(processoAtual != -1){
                Processo atual = processos.get(processoAtual);
                if(atual.getTempoRestante() == 0 || restanteQuantum == 0){
                    if(atual.getTempoRestante() > 0){
                        fila.offer(processoAtual);
                    }
                    processoAtual = -1;
                }
            }

            if(processoAtual == -1){
                processoAtual = retirarDaFila(fila);
                if(processoAtual != -1){
                    restanteQuantum = quantum;
                }
            }
            if(processoAtual != -1){
                executarUmCiclo(processos, processoAtual, tempo);
                restanteQuantum--;
            }
            registrarEspera(processos, tempo);
        }
    }

    // Verifica quais processos atingiram seu tempo de chegada e os coloca no final da fila de espera
    private void colocarChegadosNaFila(List<Processo> processos, Queue<Integer> fila, boolean[] entrouNaFila, int tempo){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            if (!entrouNaFila[i] && processo.getMomentoChegada() <= tempo){
                fila.offer(i);
                entrouNaFila[i] = true;
            }
        }
    }

    // Remove e retorna o próximo processo da fila para assumir a execução na CPU
    private int retirarDaFila(Queue<Integer> fila){
        if(fila.isEmpty()){
            return -1;
        }
        return fila.poll();
    }
    // Marca o uso da CPU na matriz (1) neste instante de tempo e deduz o tempo restante do processo
    private void executarUmCiclo(List<Processo> processos, int indice, int tempo){
        matriz[indice][tempo] = 1;
        processos.get(indice).decrementarTempoRestante();
    }

    // Marca na matriz (2) todos os processos que já chegaram, ainda precisam executar, mas não ganharam a CPU neste ciclo
    private void registrarEspera(List<Processo> processos, int tempo){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            if(processo.getMomentoChegada() <= tempo && processo.getTempoRestante() > 0 && matriz[i][tempo] != 1){
                matriz[i][tempo] = 2;
            }
        }
    }

    // Define as métricas finais identificando o tempo de resposta
    private void calcularDados(List<Processo> processos){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            int primeiroUso = encontrarPrimeiraExecucao(i);
            contarEspera(processo, i);
            processo.setTempoDeResposta(primeiroUso - processo.getMomentoChegada());
            processo.setTurnaround();
        }
    }
    // Verifica a linha do processo na matriz para achar o momento exato em que ele executou pela primeira vez
    private int encontrarPrimeiraExecucao(int indice){
        for(int tempo = 0; tempo < matriz[indice].length; tempo++){
            if(matriz[indice][tempo] == 1){
                return tempo;
            }
        }
        return -1;
    }

    // Conta quantas vezes o número 2 aparece para o processo, somando seu tempo total de espera
    private void contarEspera(Processo processo, int indice){
        for(int tempo = 0; tempo < matriz[indice].length; tempo++){
            if(matriz[indice][tempo] == 2){
                processo.incrementarTempoDeEspera();
            }
        }
    }

    private void calcularMedias(List<Processo> processos){
        double espera = 0;
        double resposta = 0;
        double turnaround = 0;
        for(Processo processo : processos){
            espera += processo.getTempoDeEspera();
            resposta += processo.getTempoDeResposta();
            turnaround += processo.getTurnaround();
        }
        int total = processos.size();
        mediaEspera = espera / total;
        mediaResposta = resposta / total;
        mediaTurnaround = turnaround / total;
    }

    public double getMediaEspera() {
        return mediaEspera;
    }

    public double getMediaResposta() {
        return mediaResposta;
    }

    public double getMediaTurnaround() {
        return mediaTurnaround;
    }


}