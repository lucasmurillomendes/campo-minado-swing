/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.lucas.cm.visao;

import br.com.lucas.cm.modelo.Tabuleiro;
import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 *
 * @author lucas
 */
public class TelaPrincipal extends JFrame{

    public TelaPrincipal() throws HeadlessException {
        Tabuleiro tabuleiro = new Tabuleiro(16, 30, 1);
        PainelTabuleiro painelTabuleiro =  new PainelTabuleiro(tabuleiro);       
        add(painelTabuleiro);
        
        setTitle("Campo Minado");
        setSize(690,438);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new TelaPrincipal();
    }
    
}
