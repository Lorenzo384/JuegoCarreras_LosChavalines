package org.example.src;

import org.example.src.service.Juego;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Juego juego = new Juego();
            juego.setVisible(true);
        });
    }
}

