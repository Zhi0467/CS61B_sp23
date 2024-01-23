package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private String currState = "";
    private DynamicWorld world;
    public static final int LARGE_FONT = 30;
    public static final int SMALL_FONT = 20;
    private boolean hitTheDoor = false;
    private boolean hitTheBomb = false;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        // create a menu with options: new game, load game, quit
        char command = menu();
        StdAudio.close();
        if (command == 'N') {
            currState = "";
            currState += 'N';
            playNewGameWithKeyboard();
        } else if (command == 'L') {
            // load previous game here
            String pre = readSave();
            this.interactWithInputString(pre);
            playCurrGameWithKeyboard();
        } else if (command == 'Q') {
            // quit the whole program
            System.exit(0);
        }

    }
    private void playNewGameWithKeyboard() {
        // create a new game here
        String seed = inputFromKeyboard();
        currState += seed;
        currState += "s";
        this.world = new DynamicWorld(seed, WIDTH, HEIGHT, ter);
        playCurrGameWithKeyboard();
    }
    private void show(int indicator) {
        if (indicator % 2 == 0) {
            this.world.showVisible();
        } else {
            this.world.show();
        }
    }
    private void showWithWarning(int indicator) {
        if (indicator % 2 == 0) {
            this.world.showVisibleWithWarning();
        } else {
            this.world.showWithWarning();
        }
    }
    private boolean terminateCondition(char c, TETile tile) {
        int[] curr = world.getAvatarPosition();
        TETile[][] canvas = world.getWorld();
        if (c == 'W' || c == 'w') {
            if (curr[1] + 1 < HEIGHT) {
                if (canvas[curr[0]][curr[1] + 1].equals(tile)) {
                    return true;
                }
            }
        } else if (c == 'a' || c == 'A') {
            if (curr[0] - 1 >= 0) {
                if (canvas[curr[0] - 1][curr[1]].equals(tile)) {
                    return true;
                }
            }
        } else if (c == 's' || c == 'S') {
            if (curr[1] - 1 >= 0) {
                if (canvas[curr[0]][curr[1] - 1].equals(tile)) {
                    return true;
                }
            }
        } else if (c == 'D' || c == 'd') {
            if (curr[0] + 1 < WIDTH) {
                if (canvas[curr[0] + 1][curr[1]].equals(tile)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean hitWall(char c) {
        int[] curr = world.getAvatarPosition();
        TETile[][] canvas = world.getWorld();
        if (c == 'W' || c == 'w') {
            if (curr[1] + 1 < HEIGHT) {
                if (canvas[curr[0]][curr[1] + 1].equals(Tileset.WALL)) {
                    return true;
                }
            }
        } else if (c == 'a' || c == 'A') {
            if (curr[0] - 1 >= 0) {
                if (canvas[curr[0] - 1][curr[1]].equals(Tileset.WALL)) {
                    return true;
                }
            }
        } else if (c == 's' || c == 'S') {
            if (curr[1] - 1 >= 0) {
                if (canvas[curr[0]][curr[1] - 1].equals(Tileset.WALL)) {
                    return true;
                }
            }
        } else if (c == 'D' || c == 'd') {
            if (curr[0] + 1 < WIDTH) {
                if (canvas[curr[0] + 1][curr[1]].equals(Tileset.WALL)) {
                    return true;
                }
            }
        }
        return false;
    }
    private void playCurrGameWithKeyboard() {
        int count = 0;
        boolean hitTheWall = false;
        while (true) {
            if (hitTheDoor) {
                world.winning = true;
                show(count);
            }
            if (hitTheBomb) {
                world.fail = true;
                show(count);
            }
            if (!hitTheWall) {
                show(count);
            } else {
                showWithWarning(count);
            }
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                hitTheWall = hitWall(c);
                if (c == 'L' || c == 'l') {
                    count++;
                }
                hitTheDoor = terminateCondition(c, Tileset.LOCKED_DOOR);
                hitTheBomb = terminateCondition(c, Tileset.BOMB);
                if (c != ':') {
                    currState += c;
                    world.move(c);
                } else {
                    int indicator = 0;
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char c1 = StdDraw.nextKeyTyped();
                            hitTheWall = hitWall(c1);
                            hitTheDoor = terminateCondition(c1, Tileset.LOCKED_DOOR);
                            hitTheBomb = terminateCondition(c1, Tileset.BOMB);
                            if (c1 == 'L' || c1 == 'l') {
                                count++;
                            }
                            if (c1 == 'Q' || c1 == 'q') {
                                save(currState);
                                System.exit(0);
                            } else {
                                indicator = 1;
                            }
                        }
                        if (indicator == 1) {
                            break;
                        }
                    }
                }
            }
        }
    }
    private void save(String str) {
        try {
            Writer writer = new FileWriter("save.txt");
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String readSave() {
        In fileReader = new In("save.txt");
        String saveState = fileReader.readString();
        return saveState;
    }

    private String inputFromKeyboard() {
        String input = "";
        // draw the UI with prompt
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monaco", Font.BOLD, LARGE_FONT));
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Input a seed ending with 's':");
            StdDraw.setFont(new Font("Monaco", Font.BOLD, SMALL_FONT));
            StdDraw.text(WIDTH / 2, HEIGHT / 2, input);
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 's') {
                    return input;
                }
                input += c;
            }
        }
    }
    private char menu() {
        // draw the menu using StdDraw
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        // set the title with larger font
        StdDraw.setFont(new Font("Monaco", Font.BOLD, LARGE_FONT));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 6, "CS61B: THE GAME");
        // set the options with smaller font
        StdDraw.setFont(new Font("Monaco", Font.BOLD, SMALL_FONT));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Quit (Q)");
        // wait for user input
        while (true) {
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'n' || c == 'l' || c == 'q' || c == 'N' || c == 'L' || c == 'Q') {
                    return Character.toUpperCase(c);
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        if (input.charAt(0) == 'N' || input.charAt(0) == 'n') {
            this.currState = "n";
            int pointer = 1;
            String seed = "";
            while (pointer < input.length()) {
                if (input.charAt(pointer) == 's' || input.charAt(pointer) == 'S') {
                    break;
                }
                seed += input.charAt(pointer);
                currState += input.charAt(pointer);
                pointer++;
            }
            pointer += 1;
            currState += 's';
            this.world = new DynamicWorld(seed, WIDTH, HEIGHT, ter);
            String movement = "";
            if (pointer < input.length() -  1) {
                while (pointer < input.length()) {
                    if (input.charAt(pointer) == ':') {
                        if (pointer == input.length() - 1) {
                            break;
                        } else if (input.charAt(pointer + 1) == 'q' || input.charAt(pointer + 1) == 'Q') {
                            break;
                        }
                    }
                    movement += input.charAt(pointer);
                    currState += input.charAt(pointer);
                    pointer++;
                }
            }
            if (pointer != input.length() - 1) {
                replay(movement);
            }
            if (pointer == input.length() - 1) {
                movement += input.charAt(pointer);
                currState += input.charAt(pointer);
                replay(movement);
                return world.getWorld();
            }
            if (pointer < input.length() - 1) {
                if (input.charAt(pointer) == ':') {
                    if (input.charAt(pointer + 1) == 'q' || input.charAt(pointer + 1) == 'Q') {
                        save(currState);
                        return world.getWorld();
                    }
                }
            }
            return world.getWorld();
        } else if (input.charAt(0) == 'L' || input.charAt(0) == 'l') {
            currState = readSave();
            interactWithInputString(currState);
            currState = readSave();
            int pointer = 1;
            String move = "";
            while (pointer < input.length() && input.charAt(pointer) != ':') {
                move += input.charAt(pointer);
                pointer++;
            }
            replay(move);
            currState += move;
            if (pointer <= input.length() - 2) {
                save(currState);
            }
            return world.getWorld();
        } else {
            return null;
        }
    }
    private void replay(String input) {
        for (int i = 0; i < input.length(); i++) {
            this.world.move(input.charAt(i));
        }
    }
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithKeyboard();
    }
}
