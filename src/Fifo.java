import java.util.List;

public class Fifo{

    private int[][] matriz;
    private double mediaEspera;
    private double mediaResposta;
    private double mediaTurnaround;

    // Inicializa a matriz e chama a sequência de execução e cálculo das métricas
    public Fifo(int quantidadeProcessos, int duracaoTotal, List<Processo> processos){
        matriz = new int[quantidadeProcessos][duracaoTotal];
        executarFila(processos);
        calcularMetricas(processos);
        calcularMedias(processos);
    }

    // Percorre todos os processos e cada instante de tempo para simular a fila Fifo
    private void executarFila(List<Processo> processos){
        for (int processo = 0; processo < processos.size(); processo++){
            Processo atual = processos.get(processo);
            for (int tempo = 0; tempo < matriz[processo].length; tempo++){
                if (!estaDisponivel(atual, tempo)){
                    continue;
                }

                // Se a CPU está livre, marca 1 na matriz (executando) e reduz o tempo restante do processo
                if (cpuLivre(processo, tempo)){
                    matriz[processo][tempo] = 1;
                    executarUmInstante(atual);

                    // Se a CPU está ocupada, marca 2 na matriz (processo aguardando na fila)
                }else{
                    matriz[processo][tempo] = 2;
                }
            }
        }
    }

    // Verifica se o processo já chegou no tempo atual e se ainda tem tempo de execução pendente
    private boolean estaDisponivel(Processo processo, int tempo){
        boolean jaChegou = processo.getMomentoChegada() <= tempo;
        boolean aindaPrecisaExecutar = processo.getTempoRestante() > 0;
        return jaChegou && aindaPrecisaExecutar;
    }

    // Checa os processos anteriores para garantir que nenhum deles está usando a CPU neste exato instante
    private boolean cpuLivre(int processoAtual, int tempo){
        for (int anterior = 0; anterior < processoAtual; anterior++){
            if (matriz[anterior][tempo] != 0){
                return false;
            }
        }
        return true;
    }

    // Reduz em 1 unidade o tempo que falta para o processo ser concluído
    private void executarUmInstante(Processo processo){
        processo.decrementarTempoRestante();
    }

    // Define os tempos finais de espera, resposta e turnaround de cada processo
    private void calcularMetricas(List<Processo> processos){
        for (int i = 0; i < processos.size(); i++) {
            Processo processo = processos.get(i);
            contarTempoEspera(processo, i);
            processo.setTurnaround();
            processo.setTempoDeResposta(processo.getTempoDeEspera());
        }
    }

    // Conta quantas vezes o número 2 aparece na matriz deste processo, indicando o tempo total de espera
    private void contarTempoEspera(Processo processo, int indice){
        int espera = 0;
        for (int tempo = 0; tempo < matriz[indice].length; tempo++){
            if (matriz[indice][tempo] == 2){
                espera++;
            }
        }
        processo.setTempoDeEspera(espera);
    }

    // Soma as métricas de todos os processos e divide pelo total para encontrar as médias finais
    private void calcularMedias(List<Processo> processos){
        double totalEspera = 0;
        double totalResposta = 0;
        double totalTurnaround = 0;
        for (Processo processo : processos){
            totalEspera += processo.getTempoDeEspera();
            totalResposta += processo.getTempoDeResposta();
            totalTurnaround += processo.getTurnaround();
        }
        int quantidade = processos.size();
        mediaEspera = totalEspera / quantidade;
        mediaResposta = totalResposta / quantidade;
        mediaTurnaround = totalTurnaround / quantidade;
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