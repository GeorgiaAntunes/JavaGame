import java.awt.Color;
import java.util.Random;

public class Inimigo extends Thread implements ElementoMapa {
    private char simbolo;
    private Color cor;
    private int posX;
    private int posY;
    private Mapa mapa; // Referência ao mapa do jogo

    public Inimigo(char simbolo, Color cor, int posX, int posY, Mapa mapa) {
        this.simbolo = simbolo;
        this.cor = cor;
        this.posX = posX;
        this.posY = posY;
        this.mapa = mapa;
    }

    @Override
    public synchronized void run() {
        while (true) {
            moverAleatoriamente();
            try {
                Thread.sleep(500); // Espera 0,5 segundos entre os movimentos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void moverAleatoriamente() {
        Random random = new Random();
        int dx = random.nextInt(3) - 1; // Gera um valor aleatório entre -1 e 1 para movimento horizontal
        int dy = random.nextInt(3) - 1; // Gera um valor aleatório entre -1 e 1 para movimento vertical

        // Verifica se a próxima posição é válida
        int nextX = posX + dx;
        int nextY = posY + dy;
        if (mapa.podeMoverInimigo(nextX, nextY)) {
            posX = nextX;
            posY = nextY;
        }
    }

    @Override
    public Character getSimbolo() {
        return simbolo;
    }

    @Override
    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    @Override
    public boolean podeSerAtravessado() {
        return false; // Inimigos não podem ser atravessados
    }

    @Override
    public boolean podeInteragir() {
        return true; // Inimigos podem ser interagidos
    }

    @Override
    public String interage() {
        return "Você encontrou um inimigo!"; // Mensagem de interação com o inimigo
    }

    // Métodos adicionais para obter informações do inimigo

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

}
