package fenyx.engine.render;

/**
 *
 * @author KiQDominaN
 */
public class Color {

    public static final Color black = new Color(0, 0, 0, 1);
    public static final Color white = new Color(1, 1, 1, 1);
    public static final Color red = new Color(1, 0, 0, 1);
    public static final Color green = new Color(0, 1, 0, 1);
    public static final Color blue = new Color(0, 0, 1, 1);
    public static final Color cyan = new Color(0, 0.5f, 1, 1);
    public static final Color yellow = new Color(1, 1, 0, 1);
    public static final Color orange = new Color(1, 0.5f, 0, 1);
    public static final Color violet = new Color(1, 0, 1, 1);
    public static final Color brown = new Color(0.5f, 0.25f, 0, 1);

    public float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int rgba) {
        r = (0xff & (rgba >> 16)) / 255f;
        g = (0xff & (rgba >> 8)) / 255f;
        b = (0xff & rgba) / 255f;
        a = (0xff & (rgba >> 24)) / 255f;
    }

    public Color blend(Color color) {
        return new Color((r + color.r) / 2, (g + color.g) / 2, (b + color.b) / 2, a);
    }

}
