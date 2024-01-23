package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Room {
    private TETile[][] room;
    private boolean[][] boundaries;
    private int worldWidth;
    private int worldHeight;
    private boolean[][] pureRoom;
    public Room(int x, int y, int worldWidth, int worldHeight, int width, int height) {
        room = new TETile[worldWidth][worldHeight];
        pureRoom = new boolean[worldWidth][worldHeight];
        this.worldHeight = worldHeight;
        this.worldWidth = worldWidth;
        // initialize room
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                room[i][j] = Tileset.GRASS;
            }
        }
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                pureRoom[i][j] = false;
            }
        }
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                room[i][j] = Tileset.WATER;
                pureRoom[i][j] = true;
            }
        }
        // initialize boundaries
        boundaries = new boolean[worldWidth][worldHeight];
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                boundaries[i][j] = false;
            }
        }
        for (int j = y - 1; j < y + height + 1; j++) {
            boundaries[x - 1][j] = true;
            boundaries[x + width][j] = true;
        }
        for (int i = x - 1; i < x + width + 1; i++) {
            boundaries[i][y - 1] = true;
            boundaries[i][y + height] = true;
        }
    }

    // helpers
    public boolean[][] getBoundary() {
        return this.boundaries;
    }

    public TETile[][] getRoom() {
        return this.room;
    }

    public boolean isWater(int x, int y) {
        if (room[x][y].equals(Tileset.WATER) || room[x][y].equals(Tileset.FLOOR)) {
            return true;
        }
        return false;
    }
    private int[][] getNeighbor(int x, int y) {
        int[][] neighbors = new int[8][2];
        neighbors[0][0] = x - 1;
        neighbors[0][1] = y - 1;

        neighbors[1][0] = x;
        neighbors[1][1] = y - 1;

        neighbors[2][0] = x + 1;
        neighbors[2][1] = y - 1;
        neighbors[3][0] = x - 1;
        neighbors[3][1] = y;
        neighbors[4][0] = x + 1;
        neighbors[4][1] = y;
        neighbors[5][0] = x - 1;
        neighbors[5][1] = y + 1;
        neighbors[6][0] = x;
        neighbors[6][1] = y + 1;
        neighbors[7][0] = x + 1;
        neighbors[7][1] = y + 1;
        return neighbors;
    }

    private void updateBoundaries(int x, int y) {
        boundaries[x][y] = false;
        int[][] neighbors = getNeighbor(x, y);
        for (int i = 0; i < 8; i++) {
            if (!isWater(neighbors[i][0], neighbors[i][1])) {
                boundaries[neighbors[i][0]][neighbors[i][1]] = true;
            }
        }
    }

    private int[][] getDirectNeighbors(int x, int y) {
        int[][] neighbors = new int[8][2];
        neighbors[0][0] = x;
        neighbors[0][1] = y - 1;

        neighbors[1][0] = x;
        neighbors[1][1] = y + 1;

        neighbors[2][0] = x + 1;
        neighbors[2][1] = y;
        neighbors[3][0] = x - 1;
        neighbors[3][1] = y;
        return neighbors;
    }
    public boolean detectCorner(int x, int y) {
        int numOfWater = 0;
        int[][] neighbors = getDirectNeighbors(x, y);
        for (int i = 0; i < 4; i++) {
            if (isWater(neighbors[i][0], neighbors[i][1])) {
                numOfWater++;
            }
        }
        if (numOfWater == 0) {
            return true;
        }
        return false;
    }
    public boolean detectBoundary(int x, int y) {
        int numOfGrass = 0;
        int[][] neighbors = getDirectNeighbors(x, y);
        for (int i = 0; i < 4; i++) {
            if (room[neighbors[i][0]][neighbors[i][1]].equals(Tileset.GRASS)) {
                numOfGrass++;
            }
        }
        if (numOfGrass >= 1) {
            return true;
        }
        return false;
    }
    // building methods
    public boolean buildHallway(int xStart, int yStart, int[][] turningPoints) {
        int numOfTurns = turningPoints.length;
        int currX = xStart;
        int currY = yStart;
        int count = 0;
        for (int i = 0; i < numOfTurns; i++) {
            if (turningPoints[i][0] == currX) {
                int start = Math.min(turningPoints[i][1], currY);
                int end = Math.max(turningPoints[i][1], currY);
                for (int j = start; j <= end; j++) {
                    if (isWater(currX, j)) {
                        count++;
                    }
                    if (isWater(currX - 1, j)) {
                        count++;
                    }
                    if (isWater(currX + 1, j)) {
                        count++;
                    }
                }
                currY = turningPoints[i][1];
            } else {
                int start = Math.min(turningPoints[i][0], currX);
                int end = Math.max(turningPoints[i][0], currX);
                for (int j = start; j <= end; j++) {
                    if (isWater(j, currY)) {
                        count++;
                    }
                    if (isWater(j, currY + 1)) {
                        count++;
                    }
                    if (isWater(j, currY - 1)) {
                        count++;
                    }
                }
                currX = turningPoints[i][0];
            }
        }
        if (count > 0) {
            return false;
        }
        currX = xStart;
        currY = yStart;
        for (int i = 0; i < numOfTurns; i++) {
            if (turningPoints[i][0] == currX) {
                // extend vertically
                int start = Math.min(turningPoints[i][1], currY);
                int end = Math.max(turningPoints[i][1], currY);
                for (int j = start; j <= end; j++) {
                    room[currX][j] = Tileset.WATER;
                    updateBoundaries(currX, j);
                }
                currY = turningPoints[i][1];
            } else {
                // extends horizontally
                int start = Math.min(turningPoints[i][0], currX);
                int end = Math.max(turningPoints[i][0], currX);
                for (int j = start; j <= end; j++) {
                    room[j][currY] = Tileset.WATER;
                    updateBoundaries(j, currY);
                }
                currX = turningPoints[i][0];
            }
        }
        return true;
    }

    public void buildRoom(int x, int y, int width, int height) {
        int indicator = 0;
        for (int i = x; i < x + width && i < this.worldWidth - 1; i++) {
            for (int j = y; j < y + height && j < this.worldHeight - 1; j++) {
                int[][] neighbors = getDirectNeighbors(i, j);
                for (int a = 0; a < neighbors.length; a++) {
                    if (pureRoom[neighbors[a][0]][neighbors[a][1]]) {
                        indicator++;
                    }
                }
            }
        }
        if (indicator <= 2) {
            for (int i = x; i < x + width && i < this.worldWidth - 1; i++) {
                for (int j = y; j < y + height && j < this.worldHeight - 1; j++) {
                    room[i][j] = Tileset.WATER;
                    pureRoom[i][j] = true;
                    updateBoundaries(i, j);
                }
            }
        }
    }

    public void buildWalls() {
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                if (boundaries[i][j]) {
                    room[i][j] = Tileset.WALL;
                }
            }
        }
    }
    public void buildFlowers(boolean[][] flowers) {
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                if (flowers[i][j]) {
                    room[i][j] = Tileset.FLOWER;
                }
            }
        }
    }
    public void buildDoor(int[] pos) {
        room[pos[0]][pos[1]] = Tileset.LOCKED_DOOR;
    }
    public void buildBomb(int[] pos) {room[pos[0]][pos[1]] = Tileset.BOMB;}
}

