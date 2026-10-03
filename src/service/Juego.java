package org.example.src.service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Juego extends JFrame implements ActionListener {

    private Timer timer;
    private Pista pista;
    private Carro carro1; // Jugador 1 (WASD)
    private Carro carro2; // Jugador 2 (Flechas)
    private boolean[] teclas = new boolean[256];

    public Juego() {
        setTitle("Carreras de Carros - 3 Vueltas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setResizable(false);
        setLocationRelativeTo(null);

        // Crear pista (circuito ovalado)
        pista = new Pista(450, 350);

        // Crear carros
        carro1 = new Carro(300, 500, Color.RED, "Jugador 1 (WASD)");
        carro2 = new Carro(600, 500, Color.BLUE, "Jugador 2 (Flechas)");

        // Panel de dibujo
        PanelJuego panel = new PanelJuego();
        add(panel);

        // Control de teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclas[e.getKeyCode()] = true;
                // Tecla R para reiniciar
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    reiniciar();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclas[e.getKeyCode()] = false;
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        // Bucle del juego (60 FPS)
        timer = new Timer(16, this);
        timer.start();
    }

    private void reiniciar() {
        carro1.reiniciar(300, 500);
        carro2.reiniciar(600, 500);
        pista.reiniciarVueltas();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Controles Jugador 1 (WASD)
        if (teclas[KeyEvent.VK_W]) carro1.acelerar();
        if (teclas[KeyEvent.VK_S]) carro1.frenar();
        if (teclas[KeyEvent.VK_A]) carro1.girarIzquierda();
        if (teclas[KeyEvent.VK_D]) carro1.girarDerecha();

        // Controles Jugador 2 (Flechas)
        if (teclas[KeyEvent.VK_UP]) carro2.acelerar();
        if (teclas[KeyEvent.VK_DOWN]) carro2.frenar();
        if (teclas[KeyEvent.VK_LEFT]) carro2.girarIzquierda();
        if (teclas[KeyEvent.VK_RIGHT]) carro2.girarDerecha();

        // Actualizar física
        carro1.actualizar();
        carro2.actualizar();

        // Colisiones con los límites de la pista
        pista.aplicarLimites(carro1);
        pista.aplicarLimites(carro2);

        // Detectar paso por puntos de control (vueltas)
        pista.verificarVuelta(carro1);
        pista.verificarVuelta(carro2);

        // Repintar
        repaint();
    }

    // Panel interno que dibuja todo
    private class PanelJuego extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo
            g2.setColor(new Color(34, 139, 34)); // verde césped
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Pista
            pista.dibujar(g2);

            // Carros
            carro1.dibujar(g2);
            carro2.dibujar(g2);

            // HUD - vueltas y posición
            dibujarHUD(g2);
        }

        private void dibujarHUD(Graphics2D g2) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));

            // Jugador 1
            g2.setColor(Color.WHITE);
            g2.fillRect(10, 10, 320, 60);
            g2.setColor(Color.BLACK);
            g2.drawRect(10, 10, 320, 60);
            g2.setColor(carro1.getColor());
            g2.drawString(carro1.getNombre() + " - Vuelta " + carro1.getVuelta() + "/3", 20, 35);
            g2.drawString("Posición: " + posicion(carro1), 20, 55);

            // Jugador 2
            g2.setColor(Color.WHITE);
            g2.fillRect(getWidth() - 330, 10, 320, 60);
            g2.setColor(Color.BLACK);
            g2.drawRect(getWidth() - 330, 10, 320, 60);
            g2.setColor(carro2.getColor());
            g2.drawString(carro2.getNombre() + " - Vuelta " + carro2.getVuelta() + "/3", getWidth() - 320, 35);
            g2.drawString("Posición: " + posicion(carro2), getWidth() - 320, 55);

            // Mensaje de victoria
            if (carro1.getVuelta() > 3 || carro2.getVuelta() > 3) {
                g2.setFont(new Font("Arial", Font.BOLD, 32));
                g2.setColor(Color.YELLOW);
                String ganador = carro1.getVuelta() > 3 ? carro1.getNombre() : carro2.getNombre();
                g2.drawString("🏆 " + ganador + " GANA!", getWidth()/2 - 180, getHeight()/2);
                g2.setFont(new Font("Arial", Font.PLAIN, 16));
                g2.drawString("Presiona R para reiniciar", getWidth()/2 - 110, getHeight()/2 + 30);
            }

            // Instrucciones
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(Color.WHITE);
            g2.drawString("J1: W/A/S/D  |  J2: Flechas  |  R: Reiniciar", getWidth()/2 - 170, getHeight() - 10);
        }

        private int posicion(Carro c) {
            // Comparar vueltas y distancia al siguiente punto de control
            Carro otro = (c == carro1) ? carro2 : carro1;
            if (c.getVuelta() != otro.getVuelta()) {
                return c.getVuelta() > otro.getVuelta() ? 1 : 2;
            }
            return c.getProgreso() > otro.getProgreso() ? 1 : 2;
        }
    }
}

