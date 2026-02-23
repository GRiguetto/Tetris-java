import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // Dimensions
    private static final int CELL = 30;
    private static final int BOARD_W = Board.COLS * CELL;
    private static final int BOARD_H = Board.ROWS * CELL;
    private static final int SIDE_W = 160;
    private static final int PADDING = 10;
    private static final int TOTAL_W = BOARD_W + SIDE_W + PADDING * 3;
    private static final int TOTAL_H = BOARD_H + PADDING * 2;

    // Game state
    private Board board;
    private Tetromino current;
    private Tetromino held;
    private boolean canHold;
    private Queue<Tetromino> nextQueue;
    private static final int NEXT_PREVIEW = 3;

    private int score;
    private int level;
    private int lines;
    private boolean gameOver;
    private boolean paused;
    private boolean softDrop;

    private Timer timer;
    private Random random;

    // Scoring
    private static final int[] LINE_SCORES = {0, 100, 300, 500, 800};

    public GamePanel() {
        setPreferredSize(new Dimension(TOTAL_W, TOTAL_H));
        setBackground(new Color(20, 20, 30));
        addKeyListener(this);
        setFocusable(true);

        random = new Random();
        board = new Board();
        nextQueue = new LinkedList<>();
        timer = new Timer(getDelay(), this);

        startGame();
    }

    private void startGame() {
        board.reset();
        score = 0;
        level = 1;
        lines = 0;
        gameOver = false;
        paused = false;
        softDrop = false;
        held = null;
        canHold = true;
        nextQueue.clear();

        for (int i = 0; i < NEXT_PREVIEW + 1; i++) {
            nextQueue.add(randomPiece());
        }
        current = nextQueue.poll();
        nextQueue.add(randomPiece());

        timer.setDelay(getDelay());
        timer.start();
    }

    private Tetromino randomPiece() {
        return new Tetromino(random.nextInt(7));
    }

    private int getDelay() {
        // Level 1 = 800ms, decreases per level
        return Math.max(80, 800 - (level - 1) * 70);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !paused) {
            tick();
        }
    }

    private void tick() {
        if (board.canPlace(current, 0, 1)) {
            current.y++;
        } else {
            lockPiece();
        }
        repaint();
    }

    private void lockPiece() {
        board.placePiece(current);
        int cleared = board.clearLines();
        if (cleared > 0) {
            score += LINE_SCORES[cleared] * level;
            lines += cleared;
            int newLevel = lines / 10 + 1;
            if (newLevel != level) {
                level = newLevel;
                timer.setDelay(getDelay());
            }
        }
        if (board.isGameOver()) {
            gameOver = true;
            timer.stop();
        } else {
            current = nextQueue.poll();
            nextQueue.add(randomPiece());
            canHold = true;
        }
    }

    private void holdPiece() {
        if (!canHold) return;
        canHold = false;
        if (held == null) {
            held = new Tetromino(current.type);
            current = nextQueue.poll();
            nextQueue.add(randomPiece());
        } else {
            int tmp = held.type;
            held = new Tetromino(current.type);
            current = new Tetromino(tmp);
            current.x = 3;
            current.y = 0;
        }
    }

    private void hardDrop() {
        int ghostY = board.getGhostY(current);
        score += (ghostY - current.y) * 2;
        current.y = ghostY;
        lockPiece();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int boardX = PADDING;
        int boardY = PADDING;

        // Draw board background
        g2.setColor(new Color(10, 10, 20));
        g2.fillRect(boardX, boardY, BOARD_W, BOARD_H);

        // Draw grid lines
        g2.setColor(new Color(40, 40, 60));
        for (int r = 0; r <= Board.ROWS; r++) {
            g2.drawLine(boardX, boardY + r * CELL, boardX + BOARD_W, boardY + r * CELL);
        }
        for (int c = 0; c <= Board.COLS; c++) {
            g2.drawLine(boardX + c * CELL, boardY, boardX + c * CELL, boardY + BOARD_H);
        }

        // Draw placed cells
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                Color cell = board.getCell(r, c);
                if (cell != null) {
                    drawCell(g2, boardX + c * CELL, boardY + r * CELL, cell, false);
                }
            }
        }

        // Draw ghost piece
        if (!gameOver && !paused && current != null) {
            int ghostY = board.getGhostY(current);
            int[][] shape = current.getShape();
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] == 0) continue;
                    int px = boardX + (current.x + c) * CELL;
                    int py = boardY + (ghostY + r) * CELL;
                    g2.setColor(new Color(current.getColor().getRed(), current.getColor().getGreen(),
                            current.getColor().getBlue(), 60));
                    g2.fillRect(px + 1, py + 1, CELL - 2, CELL - 2);
                    g2.setColor(new Color(current.getColor().getRed(), current.getColor().getGreen(),
                            current.getColor().getBlue(), 120));
                    g2.drawRect(px + 1, py + 1, CELL - 2, CELL - 2);
                }
            }

            // Draw current piece
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] == 0) continue;
                    int px = boardX + (current.x + c) * CELL;
                    int py = boardY + (current.y + r) * CELL;
                    if (py >= boardY) {
                        drawCell(g2, px, py, current.getColor(), false);
                    }
                }
            }
        }

        // Board border
        g2.setColor(new Color(100, 100, 160));
        g2.drawRect(boardX, boardY, BOARD_W, BOARD_H);

        // Side panel
        int sideX = boardX + BOARD_W + PADDING;
        int sideY = boardY;

        // Score, Level, Lines
        drawLabel(g2, sideX, sideY, "SCORE");
        drawValue(g2, sideX, sideY + 22, String.valueOf(score));

        drawLabel(g2, sideX, sideY + 60, "LEVEL");
        drawValue(g2, sideX, sideY + 82, String.valueOf(level));

        drawLabel(g2, sideX, sideY + 120, "LINES");
        drawValue(g2, sideX, sideY + 142, String.valueOf(lines));

        // HOLD
        drawLabel(g2, sideX, sideY + 185, "HOLD");
        drawMiniBoard(g2, sideX, sideY + 205, held);

        // NEXT
        drawLabel(g2, sideX, sideY + 295, "NEXT");
        int nextY = sideY + 315;
        Object[] nextArr = nextQueue.toArray();
        for (int i = 0; i < Math.min(NEXT_PREVIEW, nextArr.length); i++) {
            drawMiniBoard(g2, sideX, nextY + i * 80, (Tetromino) nextArr[i]);
        }

        // Controls hint
        g2.setColor(new Color(100, 100, 130));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
        int hintY = TOTAL_H - 90;
        g2.drawString("←→  Move", sideX, hintY);
        g2.drawString("↑  Rotate", sideX, hintY + 12);
        g2.drawString("↓  Soft Drop", sideX, hintY + 24);
        g2.drawString("SPACE  Hard Drop", sideX, hintY + 36);
        g2.drawString("C  Hold", sideX, hintY + 48);
        g2.drawString("P  Pause", sideX, hintY + 60);

        // Overlays
        if (gameOver) {
            drawOverlay(g2, boardX, boardY, "GAME OVER", "Press R to restart");
        } else if (paused) {
            drawOverlay(g2, boardX, boardY, "PAUSED", "Press P to continue");
        }
    }

    private void drawCell(Graphics2D g2, int x, int y, Color color, boolean ghost) {
        g2.setColor(color);
        g2.fillRect(x + 1, y + 1, CELL - 2, CELL - 2);
        // Highlight top-left
        g2.setColor(color.brighter());
        g2.drawLine(x + 1, y + 1, x + CELL - 2, y + 1);
        g2.drawLine(x + 1, y + 1, x + 1, y + CELL - 2);
        // Shadow bottom-right
        g2.setColor(color.darker());
        g2.drawLine(x + 1, y + CELL - 2, x + CELL - 2, y + CELL - 2);
        g2.drawLine(x + CELL - 2, y + 1, x + CELL - 2, y + CELL - 2);
    }

    private void drawMiniBoard(Graphics2D g2, int x, int y, Tetromino t) {
        int boxSize = 4;
        int boxW = 4 * 22 + 4;
        int boxH = 3 * 22 + 4;
        g2.setColor(new Color(10, 10, 20));
        g2.fillRect(x, y, boxW, boxH);
        g2.setColor(new Color(60, 60, 90));
        g2.drawRect(x, y, boxW, boxH);

        if (t == null) return;
        int[][] shape = t.getShape();
        int pw = shape[0].length;
        int ph = shape.length;
        int offsetX = x + 2 + (4 - pw) * 11;
        int offsetY = y + 2 + (3 - ph) * 11;

        for (int r = 0; r < ph; r++) {
            for (int c = 0; c < pw; c++) {
                if (shape[r][c] == 0) continue;
                int px = offsetX + c * 22;
                int py = offsetY + r * 22;
                g2.setColor(t.getColor());
                g2.fillRect(px + 1, py + 1, 20, 20);
                g2.setColor(t.getColor().brighter());
                g2.drawLine(px + 1, py + 1, px + 20, py + 1);
                g2.drawLine(px + 1, py + 1, px + 1, py + 20);
                g2.setColor(t.getColor().darker());
                g2.drawLine(px + 1, py + 20, px + 20, py + 20);
                g2.drawLine(px + 20, py + 1, px + 20, py + 20);
            }
        }
    }

    private void drawLabel(Graphics2D g2, int x, int y, String text) {
        g2.setColor(new Color(140, 140, 180));
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString(text, x, y + 12);
    }

    private void drawValue(Graphics2D g2, int x, int y, String value) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.drawString(value, x, y + 16);
    }

    private void drawOverlay(Graphics2D g2, int bx, int by, String title, String sub) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(bx, by, BOARD_W, BOARD_H);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(title);
        g2.drawString(title, bx + (BOARD_W - tw) / 2, by + BOARD_H / 2 - 15);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 13));
        fm = g2.getFontMetrics();
        int sw = fm.stringWidth(sub);
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(sub, bx + (BOARD_W - sw) / 2, by + BOARD_H / 2 + 15);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_R) startGame();
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
            repaint();
            return;
        }

        if (paused) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (board.canPlace(current, -1, 0)) { current.x--; }
                break;
            case KeyEvent.VK_RIGHT:
                if (board.canPlace(current, 1, 0)) { current.x++; }
                break;
            case KeyEvent.VK_DOWN:
                if (board.canPlace(current, 0, 1)) {
                    current.y++;
                    score += 1;
                } else {
                    lockPiece();
                }
                break;
            case KeyEvent.VK_UP:
                // Rotate with wall kick
                current.rotate();
                if (!board.canPlace(current, 0, 0)) {
                    // Try wall kicks
                    if (board.canPlace(current, 1, 0)) current.x++;
                    else if (board.canPlace(current, -1, 0)) current.x--;
                    else if (board.canPlace(current, 2, 0)) current.x += 2;
                    else if (board.canPlace(current, -2, 0)) current.x -= 2;
                    else current.rotateBack();
                }
                break;
            case KeyEvent.VK_SPACE:
                hardDrop();
                break;
            case KeyEvent.VK_C:
            case KeyEvent.VK_SHIFT:
                holdPiece();
                break;
            case KeyEvent.VK_Z:
                // Rotate counter-clockwise
                current.rotateBack();
                if (!board.canPlace(current, 0, 0)) {
                    if (board.canPlace(current, 1, 0)) current.x++;
                    else if (board.canPlace(current, -1, 0)) current.x--;
                    else current.rotate();
                }
                break;
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
