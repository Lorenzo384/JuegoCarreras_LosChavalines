package org.example.src.service;

import java.awt.*;

public class Carro {
    private double x, y;          // posición
    private double angulo;        // dirección en grados
    private double velocidad;     // velocidad actual
    private double maxVelocidad = 6.0;
    private double aceleracion = 0.15;
    private double fuerzaFreno = 0.3;
    private double maxReversa = 2.0;
    private double friccion = 0.99;      // pérdida de velocidad por frame al soltar el acelerador
    private double giro = 3.5;
    private double velocidadGiroCompleto = 2.0; // desde esta velocidad el giro es total
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
        // Si va hacia adelante frena fuerte; ya detenido, acelera en reversa
        double fuerza = velocidad > 0 ? fuerzaFreno : aceleracion;
        velocidad = Math.max(velocidad - fuerza, -maxReversa);
    }

    public void girarIzquierda() {
        angulo -= giro * factorGiro();
    }

    public void girarDerecha() {
        angulo += giro * factorGiro();
    }

    // Un carro detenido no gira, y en reversa el giro se invierte como en un carro real
    private double factorGiro() {
        double factor = Math.min(Math.abs(velocidad) / velocidadGiroCompleto, 1.0);
        return velocidad < 0 ? -factor : factor;
    }

    public void chocarConBorde() {
        velocidad *= 0.9;
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

        // Con angulo 0 el carro se mueve hacia +X, así que el frente se dibuja hacia +X
        g2d.fillRect(-18, -12, 36, 24); // cuerpo del carro
        g2d.setColor(Color.BLACK);
        g2d.drawRect(-18, -12, 36, 24);
        // parabrisas (al frente)
        g2d.setColor(new Color(200, 220, 255));
        g2d.fillRect(2, -9, 8, 18);
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
        int siguiente =(ultimoPunto + 1) % totalPuntos;
        if (indice == siguiente) {
            ultimoPunto = indice;

            // Si el siguiente punto es el 0, completaste una vuelta
            if (ultimoPunto ==  0) {
                vuelta++;
            }
            progreso = (double) ultimoPunto / totalPuntos;
        }
    }

}

