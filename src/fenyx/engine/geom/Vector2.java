package fenyx.engine.geom;

import fenyx.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public class Vector2 {

    public float x, y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void reset() {
        x = 0;
        y = 0;
    }

    public float length() {
        return MathUtils.sqrt((x * x) + (y * y));
    }

    public Vector2 normalize() {
        return new Vector2(x, y).div(length());
    }

    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 mul(float f) {
        return new Vector2(x * f, y * f);
    }

    public Vector2 div(float f) {
        return new Vector2(x / f, y / f);
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public Vector2 invert() {
        return new Vector2(-x, -y);
    }

}
