import java.awt.Color;

public class Tetromino {
    // All 7 tetromino shapes with all rotations
    public static final int[][][][] SHAPES = {
        // I
        {
            {{1,1,1,1}},
            {{1},{1},{1},{1}}
        },
        // O
        {
            {{1,1},{1,1}}
        },
        // T
        {
            {{0,1,0},{1,1,1}},
            {{1,0},{1,1},{1,0}},
            {{1,1,1},{0,1,0}},
            {{0,1},{1,1},{0,1}}
        },
        // S
        {
            {{0,1,1},{1,1,0}},
            {{1,0},{1,1},{0,1}}
        },
        // Z
        {
            {{1,1,0},{0,1,1}},
            {{0,1},{1,1},{1,0}}
        },
        // J
        {
            {{1,0,0},{1,1,1}},
            {{1,1},{1,0},{1,0}},
            {{1,1,1},{0,0,1}},
            {{0,1},{0,1},{1,1}}
        },
        // L
        {
            {{0,0,1},{1,1,1}},
            {{1,0},{1,0},{1,1}},
            {{1,1,1},{1,0,0}},
            {{1,1},{0,1},{0,1}}
        }
    };

    public static final Color[] COLORS = {
        new Color(0, 240, 240),   // I - Cyan
        new Color(240, 240, 0),   // O - Yellow
        new Color(160, 0, 240),   // T - Purple
        new Color(0, 240, 0),     // S - Green
        new Color(240, 0, 0),     // Z - Red
        new Color(0, 0, 240),     // J - Blue
        new Color(240, 160, 0)    // L - Orange
    };

    public int type;
    public int rotation;
    public int x, y;

    public Tetromino(int type) {
        this.type = type;
        this.rotation = 0;
        // Start near top center
        this.x = 3;
        this.y = 0;
    }

    public int[][] getShape() {
        return SHAPES[type][rotation % SHAPES[type].length];
    }

    public int getWidth() {
        return getShape()[0].length;
    }

    public int getHeight() {
        return getShape().length;
    }

    public Color getColor() {
        return COLORS[type];
    }

    public Tetromino copy() {
        Tetromino t = new Tetromino(type);
        t.rotation = rotation;
        t.x = x;
        t.y = y;
        return t;
    }

    public void rotate() {
        rotation = (rotation + 1) % SHAPES[type].length;
    }

    public void rotateBack() {
        rotation = (rotation - 1 + SHAPES[type].length) % SHAPES[type].length;
    }
}
