/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.lucas.cm.visao;

import br.com.lucas.cm.modelo.Tabuleiro;
import java.awt.GridLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author lucas
 */
public class PainelTabuleiro extends JPanel {

    public PainelTabuleiro(Tabuleiro tabuleiro) {

        setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));

        tabuleiro.paraCadaCampo(c -> add(new BotaoCampo(c)));
        tabuleiro.registrarObservador(e -> {

            /**
             * Interface invocar depois, faça depoois que executar essa ação
             * Atualizara toda a interface do jogo e só depois mostra a mensagem
             */
            SwingUtilities.invokeLater(() -> {
                if (e.isGanhou()) {
                    JOptionPane.showMessageDialog(this, "Você Ganhou :)");
                } else {
                    JOptionPane.showMessageDialog(this, "Você Perdeu :(");
                }
                tabuleiro.reinicializar();
            });
        });

    }
}
