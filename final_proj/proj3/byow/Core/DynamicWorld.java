package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DynamicWorld {
    private TETile[][] canvas;
    private int[] avatarPosition = new int[2];
    private TERenderer ter;
    private int width;
    private int height;
    public boolean winning = false;
    public boolean fail = false;
    private Boolean[][] visibleCanvas;
    public DynamicWorld(String seed, int width, int height, TERenderer ter) {
        WorldBuilder world = new WorldBuilder(seed, width, height);
        canvas = world.buildWorld();
        this.visibleCanvas = new Boolean[width][height];
        clearVisibleCanvas();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (canvas[i][j].equals(Tileset.WATER)) {
                    avatarPosition[0] = i;
                    avatarPosition[1] = j;
                }
            }
        }
        canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.AVATAR;
        this.ter = ter;
        this.ter.initialize(width, height + 2);
        this.width = width;
        this.height = height;
    }
    private void clearVisibleCanvas() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.visibleCanvas[i][j] = false;
            }
        }
    }
    private void updateVisible() {
        int xPos = Math.max(0, avatarPosition[0] - 3);
        int yPos = Math.max(0, avatarPosition[1] - 2);
        clearVisibleCanvas();
        for (int i = xPos; i < xPos + 6 && i < width; i++) {
            for (int j = yPos; j < yPos + 4 && j < height; j++) {
                if (!this.canvas[i][j].equals(Tileset.GRASS) && !this.canvas[i][j].equals(Tileset.FLOWER)) {
                    if (!((i == xPos || i == xPos + 5) && (j == yPos || j == yPos + 3))) {
                        visibleCanvas[i][j] = true;
                    }
                }
            }
        }
    }
    private TETile[][] buildCurrVisible() {
        updateVisible();
        TETile[][] curr = new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (visibleCanvas[i][j]) {
                    curr[i][j] = canvas[i][j];
                } else {
                    curr[i][j] = Tileset.NOTHING;
                }
            }
        }
        return curr;
    }
    public void move(char command) {
        if (command == 'W' || command == 'w') {
            if (canvas[avatarPosition[0]][avatarPosition[1] + 1].equals(Tileset.WATER) && avatarPosition[1] + 1 <= this.height) {
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.WATER;
                avatarPosition[1] += 1;
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.AVATAR;
            }
        } else if (command == 'A' || command == 'a') {
            if (canvas[avatarPosition[0] - 1][avatarPosition[1]].equals(Tileset.WATER) && avatarPosition[0] - 1 >= 0) {
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.WATER;
                avatarPosition[0] -= 1;
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.AVATAR;
            }
        } else if (command == 'S' || command == 's') {
            if (canvas[avatarPosition[0]][avatarPosition[1] - 1].equals(Tileset.WATER) && avatarPosition[1] - 1 >= 0) {
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.WATER;
                avatarPosition[1] -= 1;
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.AVATAR;
            }
        } else if (command == 'D' || command == 'd') {
            if (canvas[avatarPosition[0] + 1][avatarPosition[1]].equals(Tileset.WATER) && avatarPosition[0] + 1 <= this.width) {
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.WATER;
                avatarPosition[0] += 1;
                canvas[avatarPosition[0]][avatarPosition[1]] = Tileset.AVATAR;
            }
        }
    }
    private void showHelper(TETile[][] toShow) {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        int X = (int) x;
        int Y = (int) y;
        String tileName = "";
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currTime = time.format(formatter);
        if (X < width && Y < height) {
            tileName = canvas[X][Y].description();
        } else if (X == width) {
            tileName = canvas[X - 1][Y].description();
        } else if (Y == height) {
            tileName = canvas[X][Y - 1].description();
        }
        ter.renderFrame(toShow);
        StdDraw.setPenColor(Color.WHITE);
        if (winning) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
            StdDraw.text(width / 2, height / 2, "You won the game!");
            StdDraw.show();
            StdDraw.pause(3000);
            System.exit(0);
        } else if (fail) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
            StdDraw.text(width / 2, height / 2, "That's a bomb! You lost the game:(");
            StdDraw.show();
            StdDraw.pause(3000);
            System.exit(0);
        } else {
            StdDraw.text(4, this.height + 1, tileName);
            StdDraw.text(70, this.height + 1, currTime);
            StdDraw.line(0, height + 0.3, width, height + 0.3);
            StdDraw.show();
            StdDraw.pause(40);
        }
    }
    private void showHelperWithWarning(TETile[][] toShow) {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        int X = (int) x;
        int Y = (int) y;
        String tileName = "";
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currTime = time.format(formatter);
        if (X < width && Y < height) {
            tileName = canvas[X][Y].description();
        } else if (X == width) {
            tileName = canvas[X - 1][Y].description();
        } else if (Y == height) {
            tileName = canvas[X][Y - 1].description();
        }
        ter.renderFrame(toShow);
        StdDraw.setPenColor(Color.WHITE);
        if (winning) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
            StdDraw.text(width / 2, height / 2, "You won the game!");
            StdDraw.pause(3000);
            System.exit(0);
        } else if (fail) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
            StdDraw.text(width / 2, height / 2, "That's a bomb! You lost the game:(");
            StdDraw.pause(3000);
            System.exit(0);
        } else {
            StdDraw.text(40, this.height + 1, "Watch out, you just hit the wall!");
            StdDraw.text(4, this.height + 1, tileName);
            StdDraw.text(70, this.height + 1, currTime);
            StdDraw.line(0, height + 0.3, width, height + 0.3);
            StdDraw.show();
            StdDraw.pause(40);
        }
    }
    public void show() {
        showHelper(this.canvas);
    }
    public void showWithWarning() {
        showHelperWithWarning(this.canvas);
    }
    public void showVisibleWithWarning() {
        TETile[][] visible = buildCurrVisible();
        showHelperWithWarning(visible);
    }
    public int[] getAvatarPosition() {
        return avatarPosition;
    }
    public void showVisible() {
        TETile[][] visible = buildCurrVisible();
        showHelper(visible);
    }
    public TETile[][] getWorld() {
        return canvas;
    }
}
