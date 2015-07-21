package fenyx.engine.api;

import static org.lwjgl.glfw.GLFW.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * @author KiQDominaN
 */
public final class Controllable {

    private static final HashMap<Integer, String> key_names = new HashMap<>();

    static {
        // Use reflection to find out key names
        Field[] fields = org.lwjgl.glfw.GLFW.class.getFields();
        try {
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        && Modifier.isPublic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType().equals(int.class)
                        && field.getName().startsWith("GLFW_KEY_")
                        && !field.getName().endsWith("WIN")) { /* Don't use deprecated names */

                    int key = field.getInt(null);
                    int trim = field.getName().indexOf("_", field.getName().indexOf("_") + 1);

                    String name = field.getName().substring(trim + 1, field.getName().length());
                    name = name.replaceAll("_", " ");

                    key_names.put(key, name);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
    }

    //Developer stuff
    public static int KEY_ANY = -999;
    public static int TAKE_SCREENSHOT = GLFW_KEY_F5;
    //GUI & menu
    public static int BACK = GLFW_KEY_ESCAPE;
    //Game actions
    public static int USE = GLFW_KEY_E;
    //Movement
    public static int MOVE_FORWARD = GLFW_KEY_W;
    public static int MOVE_BACKWARD = GLFW_KEY_S;
    public static int MOVE_LEFT = GLFW_KEY_A;
    public static int MOVE_RIGHT = GLFW_KEY_D;
    public static int SPRINT = GLFW_KEY_LEFT_SHIFT;
    //Combat
    public static int SHOOT = GLFW_MOUSE_BUTTON_1;
    public static int RELOAD = GLFW_KEY_R;
    //Inventory
    public static int OPEN_INVENTORY = GLFW_KEY_I;
    public static int FLASHLIGHT = GLFW_KEY_F;
    //Input
    public static int CONSOLE = GLFW_KEY_GRAVE_ACCENT;
    public static int SHIFT = GLFW_KEY_LEFT_SHIFT;
    public static int ENTER = GLFW_KEY_ENTER;
    public static int BACKSPACE = GLFW_KEY_BACKSPACE;
    public static int ARROWUP = GLFW_KEY_UP;
    public static int ARROWDOWN = GLFW_KEY_DOWN;
    public static int ARROWLEFT = GLFW_KEY_LEFT;
    public static int ARROWRIGHT = GLFW_KEY_RIGHT;
    

    public static String getKeyName(int key) {
        return key_names.get(key);
    }
    
    public static void setKeyMap() {
        
    }
}
