/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.lucas.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * @author lucas
 */
public class Campo {

    private final int linha;
    private final int coluna;

    private boolean aberto = false;
    private boolean minado = false;
    private boolean marcado = false;

    private List<Campo> vizinhos = new ArrayList<>();
    //Lista de observadores
    private List<CampoObservador> observadores = new ArrayList<>();

    /**
     * outra opção seria usar BiConsumer private
     * List<BiConsumer<Campo, CampoEvento>> observadores = new ArrayList<>();
     */
    Campo(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    /**
     * Função responsavel por adicionar nosso observador de evento
     *
     * @param observador
     */
    public void registrarObservador(CampoObservador observador) {
        observadores.add(observador);
    }

    /**
     * Função responsável para notificar os observadores que o evento aconteceu
     *
     * @param evento
     */
    private void notificasrObservadores(CampoEvento evento) {
        observadores.stream()
                .forEach(observador -> observador.eventoOcorreu(this, evento));
    }

    /**
     * Função responsável por adicionar os vizinhos, ou dizer quem são eles.
     *
     * @param vizinho
     * @return
     */
    boolean adicionarVizinho(Campo vizinho) {
        boolean linhaDiferente = linha != vizinho.linha;
        boolean colunaDiferente = coluna != vizinho.coluna;
        boolean diagonal = linhaDiferente && colunaDiferente;

        int deltaLinha = Math.abs(linha - vizinho.linha);
        int deltaColuna = Math.abs(coluna - vizinho.coluna);
        int deltaGeral = deltaColuna + deltaLinha;

        if (deltaGeral == 1 && !diagonal) {
            vizinhos.add(vizinho);
            return true;
        } else if (deltaGeral == 2 & diagonal) {
            vizinhos.add(vizinho);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Função responsável por alterar a marcação do campo (sinal de mina)
     */
    public void alternarMarcacao() {
        if (!aberto) {
            marcado = !marcado;

            if (marcado) {
                //Notificando que o campo está marcado
                notificasrObservadores(CampoEvento.MARCAR);
            } else {
                //Notificando que o campo está desmarcado
                notificasrObservadores(CampoEvento.DESMARCAR);
            }
        }
    }

    /**
     * Funcao responsavel por abrir os campos
     *
     * @return
     */
    public boolean abrir() {
        if (!aberto && !marcado) {

            if (minado) {
                //Notificando que o campo tem mina e explodiu
                notificasrObservadores(CampoEvento.EXPLODIR);
                return true; //O campo abriu, porém explodiu.
            }
            //Notificando que o campo abriu
            setAberto(true);
            notificasrObservadores(CampoEvento.ABRIR);

            if (vizinhancaSegura()) {
                vizinhos.forEach(v -> v.abrir());
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean vizinhancaSegura() {
        return vizinhos.stream().noneMatch(campo -> campo.minado);
    }

    void minar() {
        minado = true;
    }

    public boolean isMarcado() {
        return marcado;
    }

    void setAberto(boolean aberto) {
        this.aberto = aberto;

        if (aberto) {
            notificasrObservadores(CampoEvento.ABRIR);
        }
    }

    public boolean isMinado() {
        return minado;
    }

    public boolean isAberto() {
        return aberto;
    }

    public boolean isFechado() {
        return !isAberto();
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    boolean objetivoAlcancado() {
        boolean desvendado = !minado && aberto;
        boolean protegido = minado && marcado;
        return desvendado || protegido;
    }

    public int minasdaVizinhanca() {
        return (int) vizinhos.stream().filter(v -> v.minado).count();
    }

    void reiniciar() {
        aberto = false;
        minado = false;
        marcado = false;
        notificasrObservadores(CampoEvento.REINICIAR);
    }

}
