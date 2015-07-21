package fenyx.engine.api;

import java.util.HashMap;

import fenyx.engine.geom.Shape;
import fenyx.engine.geom.Point;

/**
 *
 * @author KiQDominaN
 */
public final class Runtime {

    public static int screen_width, screen_height;
    public static float screen_aspect;
    public static float frametime; //For stable work with different FPS

    public static Point mouse_pos = new Point(0, 0);
    public static Point mouse_world = new Point(0, 0);
    private static final Shape.Rect mouse_rect = new Shape.Rect(0, 0, 16, 16);

    private static int last_key;
    private static final HashMap<Integer, Long> keychain = new HashMap<>(1); //Pressed buttons will be stored here
    private static char current_char; //Current input

    static {
        resetKeys();
        resetMouse();
        resetInput();
    }

    public static void setupScreen(int width, int height) {
        screen_width = width;
        screen_height = height;
        screen_aspect = (float) width / (float) height;
    }

    public static int lastKey() {
        if (keychain.isEmpty()) return -999;

        return last_key;
    }

    public static boolean keyPressed(int key) {
        if (key == Controllable.KEY_ANY)
            return !keychain.isEmpty();
        return (keychain.containsKey(key));
    }

    public static float keyTime(int key) {
        if (keyPressed(key))
            return (System.currentTimeMillis() - keychain.get(key)) / 1000;
        else
            return 0;
    }

    public static void inputChar(char c) {
        current_char = c;
    }

    public static String getInput() {
        String c = (current_char == 0) ? "" : String.valueOf(current_char);
        resetInput();

        return c;
    }

    public static void pressKey(int key) {
        last_key = key;
        keychain.put(key, System.currentTimeMillis());
    }

    public static void resetKey(int key) {
        keychain.remove(key);
    }

    public static void resetKeys() {
        keychain.clear();
    }

    public static void resetMouse() {
        mouse_pos.x = screen_width / 2;
        mouse_pos.y = screen_height / 2;
    }

    public static void resetInput() {
        current_char = 0;
    }

    public static boolean mouseInRect(Shape rect) {
        return Runtime.mouse_pos.x > rect.getX()
                && Runtime.mouse_pos.x < rect.getX() + rect.getBounds().x
                && Runtime.mouse_pos.y > rect.getY()
                && Runtime.mouse_pos.y < rect.getY() + rect.getBounds().y;
    }

    public static boolean worldMouseInRect(Shape rect) {
        mouse_rect.translate(mouse_world.x - 8, mouse_world.y - 8);
        mouse_rect.refresh();

        return Shape.intersects(mouse_rect, rect);
    }
}
