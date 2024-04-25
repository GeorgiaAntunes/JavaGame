import java.awt.Color;

public class Espada extends Thread {
    private char simbolo;
    private Color cor;
    private int posX;
    private int posY;
    private Mapa mapa;
    private boolean temEspada = false;

    public Espada(char simbolo, Color cor, int posX, int posY, Mapa mapa) {
        this.simbolo = simbolo;
        this.cor = cor;
        this.posX = posX;
        this.posY = posY;
        this.mapa = mapa;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean getTemEspada() {
        return temEspada;
    }

    public void setTemEspada(boolean temEspada) {
        temEspada = true;
        this.temEspada = temEspada;
    }

    public Color getCor() {
        return cor;
    }

    @Override
    public synchronized void run() {
        while (true) {
            if (temEspada) {
                if (mapa.estaPertoEspada()) {
                    seguirJogador(); // Atualiza a posição da espada para seguir o jogador
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void seguirJogador() {

        this.posX = mapa.getX();
        this.posY = mapa.getY();
    }
}
