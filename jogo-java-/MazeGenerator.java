import java.util.Random;

public class MazeGenerator {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;
    private static final Random random = new Random();

    public static char[][] generateMaze() {
        char[][] maze = new char[HEIGHT][WIDTH];

        // Inicializa o labirinto com paredes
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                maze[i][j] = '#';
            }
        }

        // Abre caminhos aleatórios no labirinto
        for (int i = 1; i < HEIGHT - 1; i += 2) {
            for (int j = 1; j < WIDTH - 1; j += 2) {
                maze[i][j] = ' ';

                // Decida aleatoriamente se cria um caminho para direita ou para baixo
                if (j < WIDTH - 2) {
                    maze[i][j + 1] = (random.nextBoolean()) ? ' ' : '#';
                }
                if (i < HEIGHT - 2) {
                    maze[i + 1][j] = (random.nextBoolean()) ? ' ' : '#';
                }
            }
        }

        return maze;
    }

    public static void printMaze(char[][] maze) {
        for (char[] row : maze) {
            for (char c : row) {
                System.out.print(c);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        char[][] maze = generateMaze();
        printMaze(maze);
    }
}
