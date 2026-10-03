package org.example.src.service;

import java.awt.*;

public class Carro {
    private double x, y;          // posición
    private double angulo;        // dirección en grados
    private double velocidad;     // velocidad actual
    private double maxVelocidad = 6.0;
    private double aceleracion = 0.15;
    private double friccion = 0.95;
    private double giro = 3.5;
    private Color color;
    private String nombre;
    private int vuelta = 1;
    private int ultimoPunto = 0;
    private double progreso = 0;

    public Carro(double x, double y, Color color, String nombre) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.nombre = nombre;
        this.angulo = -90; // apuntando hacia arriba
    }

    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        this.angulo = -90;
        this.velocidad = 0;
        this.vuelta = 1;
        this.ultimoPunto = 0;
        this.progreso = 0;
    }

    public void acelerar() {
        velocidad = Math.min(velocidad + aceleracion, maxVelocidad);
    }

    public void frenar() {
        velocidad = Math.max(velocidad - aceleracion * 1.5, -2.0);
    }

    public void girarIzquierda() {
        angulo -= giro;
    }

    public void girarDerecha() {
        angulo += giro;
    }

    public void actualizar() {
        // Aplicar fricción
        velocidad *= friccion;
        if (Math.abs(velocidad) < 0.05) velocidad = 0;

        // Mover en la dirección del ángulo
        double rad = Math.toRadians(angulo);
        x += Math.cos(rad) * velocidad;
        y += Math.sin(rad) * velocidad;
    }

    public void dibujar(Graphics2D g2) {
        g2.setColor(color);
        // Dibujar el carro como un rectángulo rotado
        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(angulo));

        g2d.fillRect(-12, -18, 24, 36); // cuerpo del carro
        g2d.setColor(Color.BLACK);
        g2d.drawRect(-12, -18, 24, 36);
        // ventanas
        g2d.setColor(new Color(200, 220, 255));
        g2d.fillRect(-9, -10, 18, 8);
        g2d.dispose();
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public Color getColor() { return color; }
    public String getNombre() { return nombre; }
    public int getVuelta() { return vuelta; }
    public double getProgreso() { return progreso; }


    public void setPosicion(double x, double y) {
        this.x = x;
        this.y = y;
    }


    // Métodos para el sistema de vueltas
    public void setUltimoPunto(int p) { ultimoPunto = p; }
    public int getUltimoPunto() { return ultimoPunto; }

    public void completarPunto(int indice, int totalPuntos) {
        // Si pasamos al siguiente punto en orden, avanzamos progreso
        int siguiente = (ultimoPunto + 1) % totalPuntos;
        if (indice == siguiente) {
            ultimoPunto = indice;
            progreso = (double) ultimoPunto / totalPuntos;
            // Si cerramos el círculo, completamos una vuelta
            if (indice == 0 && ultimoPunto == 0) {
                vuelta++;
            }
        }
    }
}

