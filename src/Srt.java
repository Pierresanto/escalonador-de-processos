import java.util.List;

public class Srt{

    private int[][] matriz;

    private double mediaEspera;
    private double mediaResposta;
    private double mediaTurnaround;

    // Inicializa a matriz e começa a simular o escalonamento preemptivo SRT
    public Srt(int quantidadeProcessos, int duracaoTotal, List<Processo> processos){
        matriz = new int[quantidadeProcessos][duracaoTotal];
        executarSrt(processos);
        analisarProcessos(processos);
        calcularMedias(processos);
    }

    // Avança o tempo instante por instante, sempre verificando quem deve assumir a CPU
    private void executarSrt(List<Processo> processos){
        for(int tempo = 0; tempo < matriz[0].length; tempo++){
            int selecionado = selecionarProcesso(processos, tempo);
            if(selecionado != -1){
                executarProcesso(processos, selecionado, tempo);
            }
            atualizarProcessosEmEspera(processos, tempo);
        }
    }

    // Busca entre os processos disponíveis aquele que tem o menor tempo restante neste exato momento
    private int selecionarProcesso(List<Processo> processos, int tempo){
        int escolhido = -1;
        for (int i = 0; i < processos.size(); i++){
            Processo candidato = processos.get(i);
            if(!podeSerExecutado(candidato, tempo)){
                continue;
            }
            if(escolhido == -1 || possuiMenorTempo(candidato, processos.get(escolhido))){
                escolhido = i;
            }
        }
        return escolhido;
    }

    // Verifica se o processo já chegou até o instante atual e se ainda tem processamento pendente
    private boolean podeSerExecutado(Processo processo, int tempo){
        return processo.getMomentoChegada() <= tempo && processo.getTempoRestante() > 0;
    }

    // Compara dois processos para definir quem termina mais rápido
    private boolean possuiMenorTempo(Processo candidato, Processo escolhido){
        return candidato.getTempoRestante() < escolhido.getTempoRestante();
    }

    // Registra o valor (1) na matriz e reduz o tempo necessário para o processo acabar
    private void executarProcesso(List<Processo> processos, int indice, int tempo){
        matriz[indice][tempo] = 1;
        processos.get(indice).decrementarTempoRestante();
    }

    // Identifica quem está aguardando e marca com o valor 2 na matriz
    private void atualizarProcessosEmEspera(List<Processo> processos, int tempo){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            if(deveEsperar(processo, i, tempo)){
                matriz[i][tempo] = 2;
            }
        }
    }

    // Verifica se o processo já chegou e tem tempo restante, mas não foi escolhido para usar a CPU agora
    private boolean deveEsperar(Processo processo, int indice, int tempo){
        boolean jaChegou = processo.getMomentoChegada() <= tempo;
        boolean aindaPossuiExecucao = processo.getTempoRestante() > 0;
        boolean naoExecutou = matriz[indice][tempo] != 1;
        return jaChegou && aindaPossuiExecucao && naoExecutou;
    }

    // Identifica a primeira vez que cada processo usou a CPU para calcular o tempo de resposta
    private void analisarProcessos(List<Processo> processos){
        for(int i = 0; i < processos.size(); i++){
            Processo processo = processos.get(i);
            int primeiroUsoCpu = localizarPrimeiraExecucao(i);
            contabilizarEspera(processo, i);
            processo.setTempoDeResposta(primeiroUsoCpu - processo.getMomentoChegada());
            processo.setTurnaround();
        }
    }
    private int localizarPrimeiraExecucao(int indice){
        for(int tempo = 0; tempo < matriz[indice].length; tempo++){
            if(matriz[indice][tempo] == 1){
                return tempo;
            }
        }
        return -1;
    }

    private void contabilizarEspera(Processo processo, int indice){
        for(int tempo = 0; tempo < matriz[indice].length; tempo++){
            if(matriz[indice][tempo] == 2){
                processo.incrementarTempoDeEspera();
            }
        }
    }


    private void calcularMedias(List<Processo> processos){
        double totalEspera = 0;
        double totalResposta = 0;
        double totalTurnaround = 0;
        for(Processo processo : processos){
            totalEspera += processo.getTempoDeEspera();
            totalResposta += processo.getTempoDeResposta();
            totalTurnaround += processo.getTurnaround();
        }
        int quantidade = processos.size();
        mediaEspera = totalEspera / quantidade;
        mediaResposta = totalResposta / quantidade;
        mediaTurnaround = totalTurnaround / quantidade;
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