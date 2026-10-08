import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class Main{
    // Diretório padrão (pendrive do professor). Pode ser trocado passando outro caminho como argumento
    private static final String DIRETORIO_PADRAO = "E:/";
    private static final int PRIMEIRO_ARQUIVO = 1;
    private static final int ULTIMO_ARQUIVO = 10;

    public static void main(String[] args) throws IOException{
        // Uso: java Main [diretorio]  (sem argumento, usa o diretório padrão)
        Path diretorio = Path.of(args.length > 0 ? args[0] : DIRETORIO_PADRAO);
        if (!Files.isDirectory(diretorio)){
            System.out.println("Diretório não encontrado: " + diretorio);
            System.out.println("Uso: java Main [diretorio-com-os-arquivos-TESTE-XX.txt]");
            return;
        }
        for (int arquivo = PRIMEIRO_ARQUIVO; arquivo <= ULTIMO_ARQUIVO; arquivo++){
            executarArquivo(diretorio, arquivo);
        }
    }

    // Faz a leitura, conversão, execução dos algoritmos e gravação do resultado
    private static void executarArquivo(Path diretorio, int numero){
        String nome = String.format("%02d", numero);
        Path entrada = diretorio.resolve("TESTE-" + nome + ".txt");
        String saida = diretorio.resolve("TESTE-" + nome + "-RESULTADO.txt").toString();
        if (!arquivoExiste(entrada)){
            System.out.println("Arquivo não localizado: " + entrada);
            return;
        }
        // Tenta executar o fluxo normal de processamento do arquivo de texto
        try{
            DadosEntrada dados = carregarDados(entrada);
            List<Processo> processos = montarProcessos(dados.linhas);
            validarProcessos(processos);
            int quantidade = processos.size();
            int tempoTotal = obterTempoTotal(processos);
            String resultado = processarAlgoritmos(processos, quantidade, tempoTotal, dados.quantum);
            gravarResultado(saida, resultado);
            System.out.println("Processamento concluído: " + entrada);

            // Identifica diferentes tipos de erros e salva no arquivo de saída
        }catch(NumberFormatException erro){
            salvarErro(saida, "Número inválido no arquivo " + entrada + ": " + erro.getMessage());

        }catch(ArrayIndexOutOfBoundsException erro){
            salvarErro(saida, "Dados incompletos no arquivo " + entrada + ": " + erro.getMessage());

        }catch(IOException erro){salvarErro(saida, "Não foi possível ler o arquivo " + entrada + ": " + erro.getMessage());

        }catch(Exception erro){
            salvarErro(saida, "Falha ao processar " + entrada + ": " + erro);
        }
    }


    private static boolean arquivoExiste(Path arquivo){
        return Files.exists(arquivo);
    }

    // Lê o arquivo de texto linha por linha, extrai os valores numéricos e identifica o valor do quantum
    private static DadosEntrada carregarDados(Path arquivo)throws IOException{
        List<String> linhas = Files.readAllLines(arquivo);
        List<String[]> dados = new ArrayList<>();
        for(String linha : linhas){
            if (linha.isBlank()){
                continue;
            }
            StringTokenizer leitor = new StringTokenizer(linha);
            String[] valores = new String[2];
            for(int i = 0; leitor.hasMoreTokens() && i < valores.length; i++){
                valores[i] = leitor.nextToken();
            }
            dados.add(valores);
        }

        if(dados.isEmpty()){
            throw new IllegalStateException("O arquivo não possui dados.");
        }
        int quantum = Integer.parseInt(dados.get(0)[0]);
        return new DadosEntrada(dados, quantum);
    }

    // Pula a primeira linha (quantum) e converte as demais linhas em uma lista de objetos do tipo Processo
    private static List<Processo> montarProcessos(List<String[]> dados){
        List<Processo> processos = new ArrayList<>();
        for(int i = 1; i < dados.size(); i++){
            int chegada = Integer.parseInt(dados.get(i)[0]);
            int execucao = Integer.parseInt(dados.get(i)[1]);
            processos.add(new Processo(execucao, chegada));
        }
        return processos;
    }


    private static void validarProcessos(List<Processo> processos){
        if(processos.isEmpty()){
            throw new IllegalStateException("Nenhum processo foi encontrado.");
        }
    }

    // Calcula o limite máximo de tempo para a matriz somando a execução de todos os processos com a última chegada
    private static int obterTempoTotal(List<Processo> processos){
        int soma = 0;
        int ultimaChegada = 0;
        for(Processo processo : processos){
            soma += processo.getTempoExecucao();
            ultimaChegada = Math.max(ultimaChegada, processo.getMomentoChegada());
        }
        return soma + ultimaChegada;
    }

    // Executa os 4 algoritmos de escalonamento enviando cópias isoladas da lista de processos para cada um
    private static String processarAlgoritmos(List<Processo> processos, int quantidade, int tempoTotal, int quantum){
        Fifo fifo = new Fifo(quantidade, tempoTotal, copiar(processos));
        Sjf sjf = new Sjf(quantidade, tempoTotal, copiar(processos));
        Srt srt = new Srt(quantidade, tempoTotal, copiar(processos));
        RoundRobin roundRobin = new RoundRobin(quantidade, tempoTotal, copiar(processos), quantum);
        return montarSaida(fifo, sjf, srt, roundRobin);
    }

    // Reúne as médias de resposta, espera e turnaround de cada algoritmo em um único bloco de texto formatado
    private static String montarSaida(Fifo fifo, Sjf sjf, Srt srt, RoundRobin roundRobin){
        StringBuilder resultado = new StringBuilder();
        adicionarLinha(resultado, fifo.getMediaResposta(), fifo.getMediaEspera(), fifo.getMediaTurnaround());
        adicionarLinha(resultado, sjf.getMediaResposta(), sjf.getMediaEspera(), sjf.getMediaTurnaround());
        adicionarLinha(resultado, srt.getMediaResposta(), srt.getMediaEspera(), srt.getMediaTurnaround());
        adicionarLinha(resultado, roundRobin.getMediaResposta(), roundRobin.getMediaEspera(), roundRobin.getMediaTurnaround());
        return resultado.toString();
    }


    private static void adicionarLinha(StringBuilder resultado, double resposta, double espera, double turnaround){
        resultado.append(String.format(Locale.forLanguageTag("pt-BR"), "%.3f %.3f %.3f", resposta, espera, turnaround));
        resultado.append(System.lineSeparator());
    }

    // Cria uma cópia independente da lista de processos para evitar que um algoritmo altere os dados do outro
    private static List<Processo> copiar(List<Processo> original){
        List<Processo> copia = new ArrayList<>();
        for(Processo processo : original){
            copia.add(new Processo(processo));
        }
        return copia;
    }

    // Escreve o texto final com todos os resultados no arquivo de saída especificado no diretório
    private static void gravarResultado(String caminho, String resultado) throws IOException{
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(caminho))){
            escritor.write(resultado);
        }
    }


    private static void salvarErro(String caminho, String mensagem){
        System.out.println(mensagem);
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(caminho))){
            escritor.write("ERRO NO PROCESSAMENTO");
            escritor.newLine();
            escritor.write(mensagem);
            escritor.newLine();
        }catch (IOException erro){
            System.out.println("Erro ao criar arquivo de erro: " + caminho);
        }
    }


    private static class DadosEntrada{
        private final List<String[]> linhas;
        private final int quantum;
        private DadosEntrada(List<String[]> linhas, int quantum){
            this.linhas = linhas;
            this.quantum = quantum;
        }
    }
}
