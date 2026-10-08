import java.util.List;

public class Sjf{

    private int[][] matriz;
    private double mediaEspera;
    private double mediaResposta;
    private double mediaTurnaround;

    // Cria a matriz de tempo e executa a simulação do algoritmo SJF
    public Sjf(int quantidadeProcessos, int duracaoTotal, List<Processo> processos){
        matriz = new int[quantidadeProcessos][duracaoTotal];
        simular(processos);
        obterMetricas(processos);
        calcularResultados(processos);
    }

    // Percorre tempo por tempo, quando o processo da CPU termina, escolhe o próximo processo mais curto disponível
    private void simular(List<Processo> processos){
        int atual = -1;
        for(int tempo = 0; tempo < matriz[0].length; tempo++){
            if(processoTerminou(processos, atual)){
                atual = escolherProcesso(processos, tempo);
            }
            if(atual >= 0){
                executar(processos, atual, tempo);
            }
            atualizarEspera(processos, tempo);
        }
    }
    // Confirma se nenhum processo está em execução ou se o processo atual esgotou seu tempo
    private boolean processoTerminou(List<Processo> processos, int indice){
        return indice == -1 || processos.get(indice).getTempoRestante() <= 0;
    }
    // Verifica os processos que já chegaram e seleciona o que possui o menor tempo restante
    private int escolherProcesso(List<Processo> processos, int instante){
        int escolhido = -1;
        for(int i = 0; i < processos.size(); i++){
            Processo candidato = processos.get(i);
            if(!estaDisponivel(candidato, instante)){
                continue;
            }
            if(escolhido == -1){
                escolhido = i;
                continue;
            }
            Processo atual = processos.get(escolhido);
            if(candidato.getTempoRestante() < atual.getTempoRestante()){
                escolhido = i;
            }
        }
        return escolhido;
    }
    // Verifica se o processo já chegou até o instante atual e se ainda necessita da CPU
    private boolean estaDisponivel(Processo processo, int instante){
        return processo.getMomentoChegada() <= instante && processo.getTempoRestante() > 0;
    }
    // Marca (1) na matriz para registrar o uso da CPU e decrementa o tempo do processo
    private void executar(List<Processo> processos, int indice, int instante){
        matriz[indice][instante] = 1;
        processos.get(indice).decrementarTempoRestante();
    }
    // Identifica os processos prontos que não estão rodando e insere o valor (2) na matriz
    private void atualizarEspera(List<Processo> processos, int instante){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            if(estaEsperando(processo, i, instante)){
                matriz[i][instante] = 2;
            }
        }
    }
    private boolean estaEsperando(Processo processo, int indice, int instante){
        return processo.getMomentoChegada() <= instante && processo.getTempoRestante() > 0 && matriz[indice][instante] != 1;
    }

    // Descobre o tempo exato do primeiro uso da CPU e define o tempo de espera e turnaround de cada processo
    private void obterMetricas(List<Processo> processos){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            int inicio = -1;
            for(int tempo = 0; tempo < matriz[i].length; tempo++){
                if(matriz[i][tempo] == 2){
                    processo.incrementarTempoDeEspera();
                }
                if(inicio == -1 && matriz[i][tempo] == 1){
                    inicio = tempo;
                }
            }
            processo.setTempoDeResposta(inicio - processo.getMomentoChegada());
            processo.setTurnaround();
        }
    }
    // Calcula a média das métricas para todos os processos no SJF
    private void calcularResultados(List<Processo> processos){
        double espera = 0;
        double resposta = 0;
        double turnaround = 0;
        for(Processo processo : processos){
            espera += processo.getTempoDeEspera();
            resposta += processo.getTempoDeResposta();
            turnaround += processo.getTurnaround();
        }
        int quantidade = processos.size();
        mediaEspera = espera / quantidade;
        mediaResposta = resposta / quantidade;
        mediaTurnaround = turnaround / quantidade;
    }

    public double getMediaEspera(){
        return mediaEspera;
    }
    public double getMediaResposta(){
        return mediaResposta;
    }
    public double getMediaTurnaround(){
        return mediaTurnaround;
    }
}