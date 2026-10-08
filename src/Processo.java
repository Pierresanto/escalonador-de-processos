public class Processo{

    private int tempoExecucao;
    private int momentoDeChegada;
    private int tempoRestante;
    private int tempoDeEspera;
    private int tempoDeResposta;
    private int turnaround;

    // Recebe o tempo total e a chegada, e prepara as variáveis zerando os contadores
    public Processo(int tempoExecucao, int momentoDeChegada){
        this.tempoExecucao = tempoExecucao;
        this.momentoDeChegada = momentoDeChegada;
        prepararProcesso();
    }
    private void prepararProcesso(){
        tempoRestante = tempoExecucao;
        tempoDeEspera = 0;
        tempoDeResposta = 0;
        turnaround = 0;
    }

    // Construtor de cópia que clona um processo existente para não alterar os dados do original durante as simulações
    public Processo(Processo processo){
        tempoExecucao = processo.getTempoExecucao();
        momentoDeChegada = processo.getMomentoChegada();
        prepararProcesso();
    }

    // Reduz 1 unidade do tempo restante sempre que este processo executar na CPU
    public void decrementarTempoRestante(){
        if(tempoRestante != 0){
            tempoRestante--;
        }
    }

    // Aumenta o contador em 1 a cada instante de tempo que o processo passa aguardando
    public void incrementarTempoDeEspera(){
        tempoDeEspera = tempoDeEspera + 1;
    }
    public void incrementarTempoDeResposta(){
        tempoDeResposta = tempoDeResposta + 1;
    }

    public void setTempoDeResposta(int tempo){
        tempoDeResposta = tempo;
    }

    public void setTempoDeEspera(int tempo){
        tempoDeEspera = tempo;
    }

    // O tempo total do turnaround é a soma do tempo necessário de execução com o tempo gasto na fila de espera
    public void setTurnaround(){
        turnaround = calcularTurnaround();
    }
    private int calcularTurnaround(){
        return tempoExecucao + tempoDeEspera;
    }

    public int getTempoExecucao(){
        return tempoExecucao;
    }

    public int getMomentoChegada(){
        return momentoDeChegada;
    }

    public int getTempoRestante(){
        return tempoRestante;
    }

    public int getTempoDeEspera(){
        return tempoDeEspera;
    }

    public int getTempoDeResposta(){
        return tempoDeResposta;
    }

    public int getTurnaround(){
        return turnaround;
    }
}