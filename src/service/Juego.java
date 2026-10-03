package org.example.src.service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Juego extends JFrame implements ActionListener {

    private Timer timer;
    private Pista pista;
    private Carro carro1; // Jugador 1 (WASD)
    private Carro carro2; // Jugador 2 (Flechas)
    private boolean[] teclas = new boolean[256];
    private boolean resultadoGuardado = false;
    private static final Path ARCHIVO_RESULTADOS = Path.of("resultados.txt");

    // Paneles para el menú y las instrucciones
    private JPanel panelMenu;
    private JPanel panelInstrucciones;
    private PanelJuego panelJuego;
    private CardLayout cardLayout;

    public Juego() {
        setTitle("Carreras de Carros - 3 Vueltas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setResizable(false);
        setLocationRelativeTo(null);

        // Crear pista (circuito ovalado)
        pista = new Pista(450, 350);

        // Crear carros
        carro1 = new Carro(250, 500, Color.RED, "Jugador 1 (WASD)");
        carro2 = new Carro(300, 500, Color.BLUE, "Jugador 2 (Flechas)");

        // Layout con tarjetas para navegar entre pantallas
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Panel del juego
        panelJuego = new PanelJuego();
        add(panelJuego, "juego");

        // Menú principal
        crearMenu();
        add(panelMenu, "menu");

        // Pantalla de instrucciones
        crearInstrucciones();
        add(panelInstrucciones, "instrucciones");

        // Control de teclado (solo aplica al panel del juego)
        panelJuego.setFocusable(true);
        panelJuego.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (esTeclaValida(e.getKeyCode())) {
                    teclas[e.getKeyCode()] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    reiniciar();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (esTeclaValida(e.getKeyCode())) {
                    teclas[e.getKeyCode()] = false;
                }
            }
        });

        // Bucle del juego (60 FPS)
        timer = new Timer(16, this);
        timer.start();

        // Mostrar el menú al inicio
        cardLayout.show(getContentPane(), "menu");
    }

    private void crearMenu() {
        panelMenu = new JPanel(new GridBagLayout());
        panelMenu.setBackground(new Color(34, 139, 34)); // verde césped

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        // Título
        JLabel titulo = new JLabel("🏁 CARRERAS DE CARROS 🏁");
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        panelMenu.add(titulo, gbc);

        // Botón Jugar
        JButton btnJugar = new JButton("🎮 JUGAR");
        btnJugar.setFont(new Font("Arial", Font.BOLD, 20));
        btnJugar.setPreferredSize(new Dimension(250, 60));
        btnJugar.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Deseas jugar 1 vs 1 (2 jugadores)?",
                    "Modo de juego", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                cardLayout.show(getContentPane(), "juego");
                panelJuego.requestFocusInWindow();
            }
        });
        gbc.gridy = 1;
        panelMenu.add(btnJugar, gbc);

        // Botón Instrucciones
        JButton btnInstrucciones = new JButton("📖 INSTRUCCIONES");
        btnInstrucciones.setFont(new Font("Arial", Font.BOLD, 20));
        btnInstrucciones.setPreferredSize(new Dimension(250, 60));
        btnInstrucciones.addActionListener(e ->
                cardLayout.show(getContentPane(), "instrucciones"));
        gbc.gridy = 2;
        panelMenu.add(btnInstrucciones, gbc);
    }

    private void crearInstrucciones() {
        panelInstrucciones = new JPanel(new BorderLayout());
        panelInstrucciones.setBackground(new Color(34, 139, 34));

        // Panel central con la información
        JPanel info = new JPanel(new GridLayout(2, 1, 10, 10));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Jugador 1
        JPanel j1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        j1.setOpaque(true);
        j1.setBackground(Color.WHITE);
        j1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel icono1 = new JLabel("🔴");
        icono1.setFont(new Font("Arial", Font.PLAIN, 40));
        JLabel texto1 = new JLabel("<html><b>JUGADOR 1</b><br>W: Acelerar<br>A: Girar izquierda<br>S: Frenar / Reversa<br>D: Girar derecha</html>");
        texto1.setFont(new Font("Arial", Font.PLAIN, 18));
        j1.add(icono1);
        j1.add(texto1);

        // Jugador 2
        JPanel j2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        j2.setOpaque(true);
        j2.setBackground(Color.WHITE);
        j2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel icono2 = new JLabel("🔵");
        icono2.setFont(new Font("Arial", Font.PLAIN, 40));
        JLabel texto2 = new JLabel("<html><b>JUGADOR 2</b><br>↑: Acelerar<br>←: Girar izquierda<br>↓: Frenar / Reversa<br>→: Girar derecha</html>");
        texto2.setFont(new Font("Arial", Font.PLAIN, 18));
        j2.add(icono2);
        j2.add(texto2);

        info.add(j1);
        info.add(j2);

        // Botón volver
        JButton btnVolver = new JButton("⬅ VOLVER AL MENÚ");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 16));
        btnVolver.setPreferredSize(new Dimension(200, 50));
        btnVolver.addActionListener(e ->
                cardLayout.show(getContentPane(), "menu"));

        panelInstrucciones.add(info, BorderLayout.CENTER);
        panelInstrucciones.add(btnVolver, BorderLayout.SOUTH);
    }


    private void reiniciar() {
        carro1.reiniciar(250, 500);
        carro2.reiniciar(300, 500);
        pista.reiniciarVueltas();
        resultadoGuardado = false;
    }

    private boolean esTeclaValida(int codigo) {
        return codigo >= 0 && codigo < teclas.length;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
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

            // Guardar el ganador una sola vez por carrera
            if (!resultadoGuardado && (carro1.getVuelta() > 3 || carro2.getVuelta() > 3)) {
                String ganador = carro1.getVuelta() > 3 ? carro1.getNombre() : carro2.getNombre();
                guardarResultado(ganador);
                resultadoGuardado = true;
            }

            // Repintar
            repaint();
        } catch (Exception ex) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Error en el juego: " + ex.getMessage());
        }
    }

    private void guardarResultado(String ganador) {
        try (BufferedWriter out = Files.newBufferedWriter(ARCHIVO_RESULTADOS,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.write(ganador + "," + LocalDateTime.now());
            out.newLine();
        } catch (IOException ex) {
            System.err.println("No se pudo guardar el resultado: " + ex.getMessage());
        }
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
            Carro otro = (c == carro1) ? carro2 : carro1;
            if (c.getVuelta() != otro.getVuelta()) {
                return c.getVuelta() > otro.getVuelta() ? 1 : 2;
            }
            return c.getProgreso() > otro.getProgreso() ? 1 : 2;
        }
    }
}
