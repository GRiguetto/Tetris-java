import java.awt.Color;

public class Board {
    public static final int COLS = 10;
    public static final int ROWS = 20;

    private Color[][] grid;

    public Board() {
        grid = new Color[ROWS][COLS];
    }

    public Color getCell(int row, int col) {
        return grid[row][col];
    }

    public boolean isOccupied(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return true;
        return grid[row][col] != null;
    }

    public boolean canPlace(Tetromino t, int dx, int dy) {
        int[][] shape = t.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;
                int newRow = t.y + row + dy;
                int newCol = t.x + col + dx;
                if (newCol < 0 || newCol >= COLS) return false;
                if (newRow >= ROWS) return false;
                if (newRow >= 0 && grid[newRow][newCol] != null) return false;
            }
        }
        return true;
    }

    public void placePiece(Tetromino t) {
        int[][] shape = t.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;
                int r = t.y + row;
                int c = t.x + col;
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    grid[r][c] = t.getColor();
                }
            }
        }
    }

    public int clearLines() {
        int cleared = 0;
        for (int row = ROWS - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                removeLine(row);
                row++; // recheck same row
                cleared++;
            }
        }
        return cleared;
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < COLS; col++) {
            if (grid[row][col] == null) return false;
        }
        return true;
    }

    private void removeLine(int row) {
        for (int r = row; r > 0; r--) {
            grid[r] = grid[r - 1].clone();
        }
        grid[0] = new Color[COLS];
    }

    public boolean isGameOver() {
        for (int col = 0; col < COLS; col++) {
            if (grid[0][col] != null) return true;
        }
        return false;
    }

    public void reset() {
        grid = new Color[ROWS][COLS];
    }

    // Get ghost piece position (hard drop preview)
    public int getGhostY(Tetromino t) {
        int ghostY = t.y;
        while (canPlaceAt(t, t.x, ghostY + 1)) {
            ghostY++;
        }
        return ghostY;
    }

    private boolean canPlaceAt(Tetromino t, int x, int y) {
        int[][] shape = t.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;
                int newRow = y + row;
                int newCol = x + col;
                if (newCol < 0 || newCol >= COLS) return false;
                if (newRow >= ROWS) return false;
                if (newRow >= 0 && grid[newRow][newCol] != null) return false;
            }
        }
        return true;
    }
}
