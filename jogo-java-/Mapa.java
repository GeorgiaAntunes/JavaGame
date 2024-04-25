import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Mapa {
    private List<String> mapa;
    private Jogo jogo;
    private List<Inimigo> inimigos;
    private Espada espada;
    private List<Portal> portais;
    private Map<Character, ElementoMapa> elementos;
    private int x = 50; // Posição inicial X do personagem
    private int y = 50; // Posição inicial Y do personagem
    private int raioAlcance = 3; // Raio de alcance do jogador, para interagir e Atacar
    private final int TAMANHO_CELULA = 10; // Tamanho de cada célula do mapa
    private boolean[][] areaRevelada; // Rastreia quais partes do mapa foram reveladas
    private final Color brickColor = new Color(153, 76, 0); // Cor marrom para tijolos
    private final Color vegetationColor = new Color(34, 139, 34); // Cor verde para vegetação
    private final int RAIO_VISAO = 15; // Raio de visão do personagem

    public Mapa(String arquivoMapa, Jogo jogo) {
        this.jogo = jogo;
        mapa = new ArrayList<>();
        elementos = new HashMap<>();
        portais = new ArrayList<>();
        registraElementos();
        carregaMapa(arquivoMapa);
        areaRevelada = new boolean[mapa.size() + 1000][mapa.get(0).length() + 1000];
        atualizaCelulasReveladas();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTamanhoCelula() {
        return TAMANHO_CELULA;
    }

    public int getNumLinhas() {
        return mapa.size();
    }

    public int getNumColunas() {
        return mapa.get(0).length();
    }

    public ElementoMapa getElemento(int x, int y) {
        Character id = mapa.get(y).charAt(x);
        return elementos.get(id);
    }

    public int getRaioAlcance() {
        return raioAlcance;
    }

    public Espada getEspada() {
        return espada;
    }

    public boolean estaRevelado(int x, int y) {
        return areaRevelada[y][x];
    }

    // Move conforme enum Direcao
    public boolean move(Direcao direcao) {
        int dx = 0, dy = 0;

        switch (direcao) {
            case CIMA:
                dy = -TAMANHO_CELULA;
                break;
            case BAIXO:
                dy = TAMANHO_CELULA;
                break;
            case ESQUERDA:
                dx = -TAMANHO_CELULA;
                break;
            case DIREITA:
                dx = TAMANHO_CELULA;
                break;
            default:
                return false;
        }

        if (!podeMover(x + dx, y + dy)) {
            System.out.println("Não pode mover");
            return false;
        }

        x += dx;
        y += dy;

        // Atualiza as células reveladas
        atualizaCelulasReveladas();
        return true;
    }

    private boolean podeMover(int nextX, int nextY) {
        int mapX = nextX / TAMANHO_CELULA;
        int mapY = nextY / TAMANHO_CELULA - 1;

        if (mapa == null || mapY >= mapa.size() || mapX >= mapa.get(0).length())
            return false;

        if (mapX >= 0 && mapX < mapa.get(0).length() && mapY >= 1 && mapY <= mapa.size()) {
            char id;

            try {
                id = mapa.get(mapY).charAt(mapX);
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }

            if (id == ' ')
                return true;

            ElementoMapa elemento = elementos.get(id);
            if (elemento != null) {
                // System.out.println("Elemento: " + elemento.getSimbolo() + " " +
                // elemento.getCor());
                return elemento.podeSerAtravessado();
            }
            // Verifica se a próxima posição está ocupada por um inimigo
            for (Inimigo inimigo : getInimigos()) {
                if (inimigo.getPosX() == nextX && inimigo.getPosY() == nextY) {
                    return false;
                }
            }
        }

        return false;
    }

    // CRIAR ESPADA
    public void criarEspada() {
        // Gera posições aleatórias para a espada dentro dos limites do mapa
        Random random = new Random();
        int posX = random.nextInt(getNumColunas());
        int posY = random.nextInt(getNumLinhas());

        // Define o símbolo da espada
        char simbolo = '⚔';

        // Escolhe uma cor para a espada
        Color cor = Color.BLUE;

        // Cria a instância da espada com as coordenadas e propriedades aleatórias
        espada = new Espada(simbolo, cor, posX, posY, this);

        // Inicia a thread da espada
        espada.start();

    }


    // metodo para iniciar e iniciar as threads dos inimigos
    public void criarInimigos() {
        inimigos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            char simbolo = 'X';
            Color cor = Color.RED;
            int posX = 10 * (i + 1); // posicao X inicial do inimigo
            int posY = 10 * (i + 1); // posicao Y inicial do inimigo
            Inimigo inimigo = new Inimigo(simbolo, cor, posX, posY, this);
            inimigo.start(); // inicia a thread do inimigo
            inimigos.add(inimigo); // adiciona o inimigo na lista de inimigos
        }
    }

    public boolean podeMoverInimigo(int nextX, int nextY) {
        if (nextX >= 0 && nextX < getNumColunas() && nextY >= 0 && nextY < getNumLinhas()) {
            return mapa.get(nextY).charAt(nextX) == ' '; // Assume que espaços vazios podem ser percorridos
        }
        return false;
    }

    // Método para obter a lista de inimigos
    public List<Inimigo> getInimigos() {
        if (inimigos == null) {
            inimigos = new ArrayList<>();
        }
        return inimigos;
    }

    // INICIALIZA PORTAL
    public List<Portal> getPortais() {
        return portais;
    }

    public void criarPortal() {
        Random random = new Random();
        int posX;
        int posY;
        char simbolo = '@'; // Símbolo que representa o portal
        Color cor1 = Color.BLUE;
        Color cor2 = Color.ORANGE;

        posX = random.nextInt(getNumColunas());
        posY = random.nextInt(getNumLinhas());

        // Crie o portal com referência para o portal anterior
        Portal portal = new Portal(simbolo, cor1, cor2, posX, posY, this);
        portais.add(portal); // Adicione o portal à lista de portais


        portal.start(); // Inicie a thread do portal para fazer piscar

    }

    public void entrarPortal(int posXOut, int posYOut) {
        setX(posXOut);
        setY(posYOut);
    }

    public void setElemento(int x, int y, ElementoMapa elemento) {
        // Verifica se as coordenadas estão dentro dos limites do mapa
        if (x >= 0 && x < mapa.get(0).length() && y >= 0 && y < mapa.size()) {
            // Substitui o elemento na posição especificada pelo novo elemento
            StringBuilder linha = new StringBuilder(mapa.get(y));
            linha.setCharAt(x, elemento != null ? elemento.getSimbolo() : ' ');
            mapa.set(y, linha.toString());
        }
    }

    public String interage() {
        if (estaPertoEspada()) {
            pegarEspada();
            return "Você pegou uma espada!";
        }

        // INTERAÇÃO PORTAL
        for (Portal portal : getPortais()) {
            // Verifica se a posição atual do jogador está sobre um portal

            // Calcula a distância entre o jogador e o portal
            int distX = Math.abs(x - portal.getPosX() * 10); // Multiplica por 10 para converter para pixels
            int distY = Math.abs(y - portal.getPosY() * 10 + 10); // Multiplica por 10 para converter para pixels
            double distancia = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)); // Calcula a distância euclidiana

            // Verifica se o jogador está dentro do raio de alcance do portal
            if (distancia <= getRaioAlcance() * TAMANHO_CELULA) {

                // Move o jogador para a posição de saída do portal
                entrarPortal(portal.getPosX() * 20, portal.getPosY() * 20 );

                // Retorna a mensagem indicando o teletransporte
                return "Você entra no portal!";
            }
        }

        // INTERAÇÃO COM INIMIGO
        for (Inimigo inimigo : inimigos) {
            // Calcula a distância entre o jogador e o inimigo
            int distX = Math.abs(x - inimigo.getPosX() * 10);
            int distY = Math.abs(y - inimigo.getPosY() * 10 + 10);
            double distancia = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

            // Se o jogador estiver dentro do raio de alcance do inimigo, realiza a interação
            if (distancia <= raioAlcance * TAMANHO_CELULA) {
                // Se o jogador tiver a espada, retorna a mensagem de ataque
                if (espada.getTemEspada()) {
                    return "Um inimigo! Ataque!";
                } else {
                    // Se o jogador não tiver a espada, retorna a mensagem para procurar uma espada
                    return "Um inimigo! Procure uma espada!";
                }
            }
        }
        // Caso não tenha nada por perto, retornar mensagem padrão
        return "Você não vê nada próximo...";
    }

    private Inimigo inimigoProximo() {
        for (Inimigo inimigo : inimigos) {
            if (estaRevelado(inimigo.getPosX() / TAMANHO_CELULA, inimigo.getPosY() / TAMANHO_CELULA)) {
                int distX = Math.abs(x - inimigo.getPosX());
                int distY = Math.abs(y - inimigo.getPosY());
                double distancia = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
                if (distancia <= raioAlcance * TAMANHO_CELULA) {
                    return inimigo;
                }
            }
        }
        return null;
    }

    public String atacarInimigo() {
        if (espada != null && espada.getTemEspada()) {
            Inimigo inimigo = inimigoProximo();
            if (inimigo != null) {
                // Altera a cor do inimigo para branco
                inimigo.setCor(Color.WHITE);
                // Remove o inimigo da lista
                inimigos.remove(inimigo);
                return "INIMIGO ATACADO!"; // Retorna mensagem de sucesso
            }
        }
        return "Nenhum inimigo próximo."; // Retorna mensagem se nenhum inimigo estiver próximo
    }

    public String atacar() {

        // Verifica se o jogador está perto de algum inimigo
        for (Inimigo inimigo : inimigos) {
            // Calcula a distância entre o jogador e o inimigo
            int distX = Math.abs(x - inimigo.getPosX() * 10);
            int distY = Math.abs(y - inimigo.getPosY() * 10 + 10);
            double distancia = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

            // Se o jogador estiver dentro do raio de alcance do inimigo, realiza a interação
            if (distancia <= raioAlcance * TAMANHO_CELULA) {
                // Se o jogador tiver a espada, retorna a mensagem de ataque
                if (espada.getTemEspada()) {
                    inimigo.setCor(new Color(0, 0, 0, 0));
                    return "Você atacou o inimigo com sucesso!";
                } else {
                    // Se o jogador não tiver a espada, retorna que é necessário ter a espada para atacar
                    return "Você precisa ter a espada para atacar!";
                }
            }
        }

        // Se não houver inimigos por perto, retorna uma mensagem indicando que não há inimigos para atacar
        return "Não há inimigos próximos para atacar.";
    }

    public boolean estaPertoEspada() {
        if (espada == null)
            return false;

        // Verifica se a distância entre o jogador e a espada é menor que o raio de alcance
        int distX = Math.abs(x - espada.getPosX());
        int distY = Math.abs(y - espada.getPosY());
        double distancia = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
        return distancia <= raioAlcance * TAMANHO_CELULA;
    }

    private void pegarEspada() {
        // Verifica se o jogador está próximo da espada
        if (estaPertoEspada()) {
            // Define que o jogador agora possui a espada
            espada.setTemEspada(true);
        }
    }

    private void carregaMapa(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                mapa.add(line);
                // Se character 'P' está contido na linha atual, então define a posição inicial
                // do personagem
                if (line.contains("P")) {
                    x = line.indexOf('P') * TAMANHO_CELULA;
                    y = mapa.size() * TAMANHO_CELULA;
                    // Remove o personagem da linha para evitar que seja desenhado
                    mapa.set(mapa.size() - 1, line.replace('P', ' '));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar as células reveladas
    private void atualizaCelulasReveladas() {
        if (mapa == null)
            return;
        for (int i = Math.max(0, y / TAMANHO_CELULA - RAIO_VISAO); i < Math.min(mapa.size(),
                y / TAMANHO_CELULA + RAIO_VISAO + 1); i++) {
            for (int j = Math.max(0, x / TAMANHO_CELULA - RAIO_VISAO); j < Math.min(mapa.get(i).length(),
                    x / TAMANHO_CELULA + RAIO_VISAO + 1); j++) {
                areaRevelada[i][j] = true;
            }
        }
    }

    // Registra os elementos do mapa
    private void registraElementos() {
        // Parede
        elementos.put('#', new Parede('▣', brickColor));
        // Vegetação
        elementos.put('V', new Vegetacao('♣', vegetationColor));
    }
}
