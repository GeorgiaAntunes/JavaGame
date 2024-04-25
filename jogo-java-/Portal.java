import java.awt.Color;

public class Portal extends Thread implements ElementoMapa {
    private char simbolo;
    private Color cor1;
    private Color cor2;
    private int posX;
    private int posY;
    private Mapa mapa;
    private boolean piscando;
    private Portal outroPortal; // Armazena a referencia para o outro portal

    public Portal(char simbolo, Color cor1, Color cor2, int posX, int posY, Mapa mapa) {
        this.simbolo = simbolo;
        this.cor1 = cor1;
        this.cor2 = cor2;
        this.posX = posX;
        this.posY = posY;
        this.mapa = mapa;
        this.piscando = true; // Começa píscando
    }

    @Override
    public Character getSimbolo() {
        return simbolo;
    }
    @Override
    public Color getCor() {
        return piscando ? cor1 : cor2;
    }

    public Color getCor1() {
        return cor1;
    }

    public Color getCor2() {
        return cor2;
    }

    @Override
    public boolean podeSerAtravessado() {
        return false;
    }

    @Override
    public boolean podeInteragir() {
        return false;
    }

    @Override
    public String interage() {
        return null;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    @Override
    public synchronized void run() {
        // Lógica para fazer o portal piscar
        while (true) {
            try {
                Thread.sleep(1000); // Tempo de espera entre as mudanças de cor
                piscando = !piscando; // Alterna entre os estados
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para alternar entre as cores azul e laranja dos portais
    public void piscar() {
        Color aux = cor1;
        cor1 = cor2;
        cor2 = aux;
    }

    public boolean estaPiscando() {
        return piscando;
    }

}
