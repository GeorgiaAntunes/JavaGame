import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Jogo extends JFrame implements KeyListener {
    private JLabel statusBar;
    private Mapa mapa;
    private final Color fogColor = new Color(192, 192, 192, 150); // Cor cinza claro com transparência para nevoa
    private final Color characterColor = Color.BLACK; // Cor preta para o personagem

    private String mensagemAtaque; // Mensagem a ser exibida quando o jogador ataca um inimigo
    private boolean mostrarMensagemAtaque; // Flag para indicar se a mensagem de ataque deve ser exibida

    public Jogo(String arquivoMapa) {
        setTitle("Jogo de Aventura");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Cria o mapa do jogo
        mapa = new Mapa(arquivoMapa,this);
        // Cria espada
        mapa.criarEspada();
        // Cria inimigos
        mapa.criarInimigos();
        //Cria portal
        mapa.criarPortal();

        // Cria um temporizador para atualizar a tela a cada 0,1 segundos
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public synchronized void actionPerformed(ActionEvent e) {
                repaint(); // Atualiza a tela
            }
        });
        timer.start(); // Inicia o temporizador

        // Painel para desenhar o mapa do jogo
        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Define a fonte para garantir que o caractere caiba em 10x10 pixels
                Font font = new Font("Roboto", Font.BOLD, 12);
                g.setFont(font);
                desenhaEspada(g);
                desenhaMapa(g);
                desenhaPersonagem(g);
                desenhaInimigos(g);
                desenhaPortal(g);

                if (mostrarMensagemAtaque) {
                    g.setColor(Color.RED);
                    g.drawString(mensagemAtaque, 10, 20);
                }
            }
        };
        mapPanel.setPreferredSize(new Dimension(800, 600));

        // Botões de movimento
        JButton btnUp = new JButton("Cima (W)");
        JButton btnDown = new JButton("Baixo (S)");
        JButton btnRight = new JButton("Direita (D)");
        JButton btnLeft = new JButton("Esquerda (A)");
        JButton btnInterect = new JButton("Interagir (E)");
        JButton btnAttack = new JButton("Atacar (J)");

        // Evita que os botões recebam o foco e interceptem os eventos de teclado
        btnUp.setFocusable(false);
        btnDown.setFocusable(false);
        btnRight.setFocusable(false);
        btnLeft.setFocusable(false);
        btnInterect.setFocusable(false);
        btnAttack.setFocusable(false);

        // Listeners para os botões
        btnUp.addActionListener(e -> move(Direcao.CIMA));
        btnDown.addActionListener(e -> move(Direcao.BAIXO));
        btnRight.addActionListener(e -> move(Direcao.DIREITA));
        btnLeft.addActionListener(e -> move(Direcao.ESQUERDA));
        btnInterect.addActionListener(e -> interage());
        btnAttack.addActionListener(e -> ataca());

        // Layout dos botões
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        buttonPanel.add(btnUp);
        buttonPanel.add(btnDown);
        buttonPanel.add(btnInterect);
        buttonPanel.add(btnRight);
        buttonPanel.add(btnLeft);
        buttonPanel.add(btnAttack);

        // Barra de status
        statusBar = new JLabel("Posição: (" + mapa.getX() + "," + mapa.getY() + ")");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setHorizontalAlignment(SwingConstants.LEFT);

        // Painel para botões e barra de status
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(buttonPanel);
        southPanel.add(statusBar);

        // Adiciona os paineis ao JFrame
        add(mapPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Ajusta o tamanho do JFrame para acomodar todos os componentes
        pack();

        // Adiciona o listener para eventos de teclado
        addKeyListener(this);

    }

    public void move(Direcao direcao) {
        if (mapa == null)
            return;

        // Modifica posição do personagem no mapa
        if (!mapa.move(direcao))
            return;

        // Atualiza a barra de status
        if (statusBar != null)
            statusBar.setText("Posição: (" + mapa.getX() + "," + mapa.getY() + ")");

        // Redesenha o painel
        repaint();
    }

    public void interage() {
        if (mapa == null)
            return;

        // Cria um diálogo para exibir a mensagem de interação
        String mensagem = mapa.interage();
        if (mensagem != null) {
            JOptionPane.showMessageDialog(this, mensagem);
        }
    }

    public void ataca() {
        String mensagem = mapa.atacar();
        if (mensagem != null) {
            // Exibir mensagem na tela
            mensagemAtaque = mensagem;
            mostrarMensagemAtaque = true;
            repaint(); // Redesenha o painel para exibir a mensagem de ataque
        }
    }

    private void desenhaMapa(Graphics g) {
        int tamanhoCelula = mapa.getTamanhoCelula();

        for (int i = 0; i < mapa.getNumLinhas(); i++) {
            for (int j = 0; j < mapa.getNumColunas(); j++) {
                int posX = j * tamanhoCelula;
                int posY = (i + 1) * tamanhoCelula;

                if (mapa.estaRevelado(j, i)) {
                    ElementoMapa elemento = mapa.getElemento(j, i);
                    if (elemento != null) {
                        g.setColor(elemento.getCor());
                        g.drawString(elemento.getSimbolo().toString(), posX, posY);
                    }
                } else {
                    g.setColor(fogColor);
                    g.fillRect(j * tamanhoCelula, i * tamanhoCelula, tamanhoCelula, tamanhoCelula);
                }
            }
        }
    }

    private void desenhaInimigos(Graphics g) {
        int tamanhoCelula = mapa.getTamanhoCelula();

        for (Inimigo inimigo : mapa.getInimigos()) {
            if (mapa.estaRevelado(inimigo.getPosX(), inimigo.getPosY())) {
                g.setColor(inimigo.getCor());
                g.drawString(Character.toString(inimigo.getSimbolo()), inimigo.getPosX() * tamanhoCelula,
                        (inimigo.getPosY() + 1) * tamanhoCelula);
            }
        }
    }

    //DESENHA ESPADA
    private void desenhaEspada(Graphics g) {
        Espada espada = mapa.getEspada(); // Obtém a instância da espada do mapa

        if (espada != null && mapa.estaRevelado(espada.getPosX(), espada.getPosY())) {
            // Verifica se a espada existe e se está dentro da área revelada do mapa

            g.setColor(espada.getCor());
            g.drawString(Character.toString(espada.getSimbolo()), espada.getPosX(), espada.getPosY());

            // Se o jogador pegou a espada, faça-a seguir o jogador
            if (espada.getTemEspada()) {
                espada.seguirJogador(); // Atualiza a posição da espada para seguir o jogador
            }
        }
    }

// DESENHA PORTAL
    private void desenhaPortal(Graphics g) {
        int tamanhoCelula = mapa.getTamanhoCelula();
        for (Portal portal : mapa.getPortais()) {
            if (mapa.estaRevelado(portal.getPosX(), portal.getPosY())) {
                g.setColor(portal.estaPiscando() ? portal.getCor1() : portal.getCor2());
                g.drawString(Character.toString(portal.getSimbolo()), portal.getPosX() * tamanhoCelula, (portal.getPosY() + 1) * tamanhoCelula);
            }
        }
    }

    private void desenhaPersonagem(Graphics g) {
        g.setColor(characterColor);
        g.drawString("☺", mapa.getX(), mapa.getY()); // Desenha o personagem principal
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Não necessário
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W: // Tecla 'W' para cima
                move(Direcao.CIMA);
                break;
            case KeyEvent.VK_S: // Tecla 'S' para baixo
                move(Direcao.BAIXO);
                break;
            case KeyEvent.VK_A: // Tecla 'A' para esquerda
                move(Direcao.ESQUERDA);
                break;
            case KeyEvent.VK_D: // Tecla 'D' para direita
                move(Direcao.DIREITA);
                break;
            case KeyEvent.VK_E: // Tecla 'E' para interagir
                interage();
                break;
            case KeyEvent.VK_J: // Tecla 'J' para ação secundária
                mapa.atacar();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Não necessário
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Jogo("mapa.txt").setVisible(true);
        });
    }
}
