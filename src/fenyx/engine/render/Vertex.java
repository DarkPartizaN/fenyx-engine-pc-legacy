package fenyx.engine.render;

import fenyx.engine.geom.Vector2;
import fenyx.engine.geom.Vector3;

/**
 *
 * @author KiQDominaN
 */
public class Vertex {

    public Vector3 origin;
    public Vector3 pos;
    public Vector3 norm;
    public Vector2 uv;
    public Color color;

    public Vertex() {
        origin = new Vector3();
        pos = new Vector3();
        norm = new Vector3();
        uv = new Vector2();
    }

    public Vertex(float x, float y, float z) {
        origin = new Vector3();
        pos = new Vector3(x, y, z);
        norm = new Vector3();
        uv = new Vector2();
    }
}
