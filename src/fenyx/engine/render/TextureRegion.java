package fenyx.engine.render;

/**
 *
 * @author KiQDominaN
 */
public class TextureRegion {

    public int id;
    public float u, v, u2, v2;

    public TextureRegion(float x, float y, float x2, float y2) {
        u = x;
        v = y;
        u2 = x2;
        v2 = y2;
    }
}
