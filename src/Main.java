package org.example.src;

import org.example.src.service.Juego;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.HeadlessException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Juego juego = new Juego();
                juego.setVisible(true);
            } catch (HeadlessException e) {
                System.err.println("No se encontró una pantalla para mostrar el juego.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se pudo iniciar el juego: " + e.getMessage());
            }
        });
    }
}