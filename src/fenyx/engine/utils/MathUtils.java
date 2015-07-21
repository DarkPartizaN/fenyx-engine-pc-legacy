package fenyx.engine.utils;

import java.util.Random;
import fenyx.engine.render.Color;

/**
 *
 * @author KiQDominaN
 */
public final class MathUtils {

    private static final Random rnd = new Random();

    public static float random_chance() {
        return random_int(0, 100) / 100f;
    }

    public static Color random_color() {
        int r = random_int(0, 255);
        int g = random_int(0, 255);
        int b = random_int(0, 255);

        int color = (0xff << 24) | (r << 16) | (g << 8) | b;

        return new Color(color);
    }

    public static int random_int(int min, int max) {
        return min + rnd.nextInt(1 + max - min);
    }

    public static float random_float(float min, float max) {
        return min + rnd.nextFloat() * (1 + max - min);
    }

    public static double random_double(double min, double max) {
        return min + rnd.nextDouble() * (1 + max - min);
    }

    public static long random_long(long min, long max) {
        return min + (Math.abs(rnd.nextLong()) % (1 + max - min));
    }

    public static float deg(float angle) {
        return (float) Math.toDegrees(angle);
    }

    public static float sin(double angle) {
        return (float) Math.sin(Math.toRadians(angle));
    }

    public static float cos(double angle) {
        return (float) Math.cos(Math.toRadians(angle));
    }

    public static float sqrt(double d) {
        return (float) Math.sqrt(d);
    }

    public static float angle(double x, double y) {
        return (float) Math.toDegrees(Math.atan2(x, y));
    }

}
