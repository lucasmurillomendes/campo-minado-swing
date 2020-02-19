/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.lucas.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Tabuleiro precisa detectar se existe algum evento no campo por isso temos que
 * implementar a interface CampoObservador
 *
 * @author lucas
 */
public class Tabuleiro implements CampoObservador {

    private final int linhas;
    private final int colunas;
    private final int minas;

    private final List<Campo> campos = new ArrayList<>();
    //Usando interface para dizer quando ganhou e quando perdeu
    private final List<Consumer<ResultadoEvento>> observadores = new ArrayList<>();

    public Tabuleiro(int linhas, int colunas, int minas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.minas = minas;

        gerarCampos();
        associarVizinhos();
        sortearMinas();
    }
    
    /**
     * Função responsavel por percorrer todos os
     * campos e fazer alguma ação
     * @param funcao 
     */
    public void paraCadaCampo (Consumer<Campo> funcao){
       campos.forEach(funcao);
    }

    /**
     * Função responsável por registrar os observadores
     *
     * @param observador
     */
    public void registrarObservador(Consumer<ResultadoEvento> observador) {
        observadores.add(observador);
    }

    /**
     * Função responsável para notificar os observadores que o evento aconteceu
     *
     * @param evento
     */
    private void notificasrObservadores(boolean resultado) {
        observadores.stream()
                // Passando true ou false do for each para o elemento
                .forEach(observador -> observador.accept(new ResultadoEvento(resultado)));
    }
    
    /**
     * Função responsável pór abrir os campos 
     * nas coordenadas passadas por parametro
     * @param linha
     * @param coluna 
     */
    public void abrir(int linha, int coluna) {
        campos.parallelStream()
                .filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
                .findFirst()
                .ifPresent(c -> c.abrir());

    }

    public void marcar(int linha, int coluna) {
        campos.parallelStream()
                .filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
                .findFirst()
                .ifPresent(c -> c.alternarMarcacao());
    }

    /**
     * Função responsável por gferar os campos no tabuleiro do jogo Tenho que
     * informar aqui que sou interessado em observar o que vai acontecer neles
     */
    private void gerarCampos() {
        for (int linha = 0; linha < linhas; linha++) {
            for (int coluna = 0; coluna < colunas; coluna++) {
                Campo campo = new Campo(linha, coluna);
                /**
                 * Como a classe atual implemeta a interface posso passar minha
                 * instancia como parametro
                 */
                campo.registrarObservador(this);
                campos.add(campo);
            }
        }
    }

    private void associarVizinhos() {
        for (Campo c1 : campos) {
            for (Campo c2 : campos) {
                c1.adicionarVizinho(c2);
            }
        }
    }

    private void sortearMinas() {
        long minasArmadas = 0;
        Predicate<Campo> minado = c -> c.isMinado();
        do {
            //Tem que colocasr entre parenteses a operação para o
            //cast transformar em int apenas após a operação
            int aleatorio = (int) (Math.random() * campos.size());
            campos.get(aleatorio).minar();
            minasArmadas = campos.stream().filter(minado).count();
        } while (minasArmadas < minas);
    }

    public boolean objetivoAlcancado() {
        return campos.stream().allMatch(c -> c.objetivoAlcancado());
    }

    public void reinicializar() {
        campos.stream().forEach(c -> c.reiniciar());
        sortearMinas();
    }
    
    /**
     * Função que notifica o evento que ocorreu com o jogo
     * @param campo
     * @param evento 
     */
    @Override
    public void eventoOcorreu(Campo campo, CampoEvento evento) {
        if (evento == CampoEvento.EXPLODIR) {
            //Mostrando minas
            mostrarMinas();
            //Notificando os obvservadores que perdeu
            notificasrObservadores(false);
        } else if (objetivoAlcancado()) {
            //Notificando os obvservadores que ganhou
            notificasrObservadores(true);
        }
    }
    
    /**
     * Função responsável por mostrar todas as minas após jogo encerrado
     */
    private void mostrarMinas() {
        campos.stream()
                .filter(c -> c.isMinado())
                .filter(c -> !c.isMarcado())
                .forEach(c -> c.setAberto(true));
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public int getMinas() {
        return minas;
    }
    
    

}
