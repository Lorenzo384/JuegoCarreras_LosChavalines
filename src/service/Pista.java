package org.example.src.service;


import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Pista {
    private int cx, cy; // centro
    private int radioExterior = 280;
    private int radioInterior = 160;
    private ArrayList<Point> puntosControl;

    public Pista(int cx, int cy) {
        this.cx = cx;
        this.cy = cy;
        generarPuntosControl();
    }

    private void generarPuntosControl() {
        puntosControl = new ArrayList<>();
        int numPuntos = 8;
        for (int i = 0; i < numPuntos; i++) {
            double ang = Math.toRadians(i * (360.0 / numPuntos));
            int px = cx + (int)((radioExterior + radioInterior) / 2 * Math.cos(ang));
            int py = cy + (int)((radioExterior + radioInterior) / 2 * Math.sin(ang));
            puntosControl.add(new Point(px, py));
        }
    }

    public void dibujar(Graphics2D g2) {
        // Asfalto (anillo exterior)
        g2.setColor(new Color(80, 80, 80));
        g2.fillOval(cx - radioExterior, cy - radioExterior, radioExterior * 2, radioExterior * 2);

        // Césped interior
        g2.setColor(new Color(34, 139, 34));
        g2.fillOval(cx - radioInterior, cy - radioInterior, radioInterior * 2, radioInterior * 2);

        // Línea de salida/meta
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(cx - 30, cy - radioInterior, cx - 30, cy - radioExterior);

        // Puntos de control (pequeños marcadores)
        g2.setColor(new Color(255, 200, 0, 150));
        for (Point p : puntosControl) {
            g2.fillOval(p.x - 4, p.y - 4, 8, 8);
        }
    }

    public void aplicarLimites(Carro c) {
        double dx = c.getX() - cx;
        double dy = c.getY() - cy;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist > radioExterior - 15) {
            double factor = (radioExterior - 15) / dist;
            c.setPosicion(cx + dx * factor, cy + dy * factor);
            c.chocarConBorde();
        }
        if (dist < radioInterior + 15) {
            double factor = (radioInterior + 15) / dist;
            c.setPosicion(cx + dx * factor, cy + dy * factor);
            c.chocarConBorde();
        }
    }


    public void verificarVuelta(Carro c) {
        // Verificar si el carro pasó por un punto de control
        for (int i = 0; i < puntosControl.size(); i++) {
            Point p = puntosControl.get(i);
            double dist = Math.hypot(c.getX() - p.x, c.getY() - p.y);
            if (dist < 25) {
                c.completarPunto(i, puntosControl.size());
                break;
            }
        }
    }

    public void reiniciarVueltas() {
        // Se reinicia el estado de vueltas a través de los carros
    }
}

