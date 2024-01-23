package byow.Core;

import byow.TileEngine.TETile;

import java.util.Random;

import static byow.Core.RandomUtils.gaussian;
import static byow.Core.RandomUtils.uniform;

public class WorldBuilder {
    private long seed;
    private int width;
    private int height;
    private Room room;
    private Random randomGenerator;
    private int pseudoRoomNum;
    private int[] curr;
    private int maxRoomWidth;
    private int maxRoomHeight;
    public WorldBuilder(String s, int w, int h) {
        this.width = w;
        this.height = h;
        this.maxRoomWidth = 12;
        this.maxRoomHeight = 6;
        this.seed = Long.parseLong(s);
        randomGenerator = new Random(this.seed);
        this.curr = getRandomPoint();
        int startWidth = getRandomNumGaussian(1, this.width - 2 - curr[0]) % maxRoomWidth + 1;
        int startHeight = getRandomNumGaussian(1, this.height - 2 - curr[1]) % maxRoomHeight + 1;
        // make sure curr[0] + startWidth <= this.width - 2, so does curr[1]
        this.room = new Room(curr[0], curr[1], this.width, this.height, startWidth, startHeight);
        pseudoRoomNum = getRandomNumGaussian(300, 600);
    }

    // BUILD WORLD - using all building methods below
    public TETile[][] buildWorld() {
        int stage = 1;
        while (stage < pseudoRoomNum) {
            if (stage % 2 == 1) {
                int[] start = getFromBoundary();
                int[] end = getNonFloorPoint();
                int length = Math.abs(end[0] - start[0]) + Math.abs(end[1] - start[1]);
                while (length >= 10 || length <= 3) {
                    start = getFromBoundary();
                    end = getNonFloorPoint();
                    length = Math.abs(end[0] - start[0]) + Math.abs(end[1] - start[1]);
                }
                if (buildHallway(start[0], start[1], end[0], end[1])) {
                    this.curr = end;
                }
            } else {
                buildRoom(this.curr[0], this.curr[1]);
            }
            stage++;
        }
        room.buildWalls();
        boolean[][] flowers = getRandomDis(1);
        this.room.buildFlowers(flowers);
        int[] doorPos = getTheDoorPos();
        this.room.buildDoor(doorPos);
        int[] bombPos = getTheDoorPos();
        this.room.buildBomb(bombPos);
        return room.getRoom();
    }

    // BUILDING METHODS - using methods from Room with random inputs
    private void buildRoom(int x, int y) {
        // the size of room is hardcoded.
        int roomWidth = getRandomNumGaussian(2, this.maxRoomWidth);
        int roomHeight = getRandomNumGaussian(2, this.maxRoomHeight);
        room.buildRoom(x, y, roomWidth, roomHeight);
    }
    private boolean buildHallway(int xStart, int yStart, int xEnd, int yEnd) {
        // use buildHallway from Room
        int[][] turningPoints = getRandomPath(xStart, yStart, xEnd, yEnd);
        return room.buildHallway(xStart, yStart, turningPoints);
    }
    // get random points of different sorts - helpers
    private int[] getRandomPoint() {
        int[] point = new int[2];
        point[0] = getRandomUniform(1, this.width - 2);
        point[1] = getRandomUniform(1, this.height - 2);
        return point;
    }
    private int[] getNonFloorPoint() {
        int[] current = getRandomPoint();
        while (true) {
            if (!room.isWater(current[0], current[1])) {
                break;
            }
            current = getRandomPoint();
        }
        return current;
    }
    private int[] getFromBoundary() {
        int[] current = getRandomPoint();
        boolean[][] currBoundary = room.getBoundary();
        while (true) {
            if (currBoundary[current[0]][current[1]] && !room.detectCorner(current[0], current[1])) {
                break;
            }
            current = getRandomPoint();
        }
        return current;
    }
    private int[] getTheDoorPos() {
        int[] curr = getFromBoundary();
        boolean[][] currBoundary = room.getBoundary();
        while (true) {
            if (currBoundary[curr[0]][curr[1]] && room.detectBoundary(curr[0], curr[1])) {
                break;
            }
            curr = getFromBoundary();
        }
        return curr;
    }
    // OTHER HELPER METHODS
    // this method provides the turningPoints array in Room
    private int[][] getRandomPath(int xStart, int yStart, int xEnd, int yEnd) {
        int numOfPoints = 2;
        double p = Math.abs(gaussian(randomGenerator, 0.5, 0.1));
        int[][] turningPoints = new int[numOfPoints][2];
        if (p > 0.5) {
            turningPoints[0][0] = xStart;
            turningPoints[0][1] = yEnd;
            turningPoints[1][0] = xEnd;
            turningPoints[1][1] = yEnd;
        } else {
            turningPoints[0][0] = xEnd;
            turningPoints[0][1] = yStart;
            turningPoints[1][0] = xEnd;
            turningPoints[1][1] = yEnd;
        }
        return turningPoints;
    }
    private int getRandomNumGaussian(int a, int b) {
        double roomNumDouble = gaussian(randomGenerator) * (b - a) / 4 + (a + b) / 2;
        while (roomNumDouble < a || roomNumDouble > b) {
            roomNumDouble = gaussian(randomGenerator) * (b - a) / 4 + (a + b) / 2;
        }
        return (int) Math.floor(roomNumDouble);
    }
    private boolean[][] getRandomDis(double p) {
        boolean[][] returned = new boolean[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                returned[i][j] = false;
                if (!this.room.isWater(i, j) && !this.room.getBoundary()[i][j]) {
                    double sample = Math.abs(gaussian(randomGenerator, 0.5, 0.2));
                    if (sample > p) {
                        returned[i][j] = true;
                    }
                }
            }
        }
        return returned;
    }
    private int getRandomUniform(int a, int b) {
        return uniform(randomGenerator, a, b);
    }
}
