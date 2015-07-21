package fenyx.engine.geom;

import fenyx.engine.utils.MathUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 *
 * @author KiQDominaN
 */
public class Vector3 {

    public float x, y, z;
    private FloatBuffer buff;

    public Vector3() {
        x = 0;
        y = 0;
        z = 0;

        buff = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void reset() {
        x = 0;
        y = 0;
        z = 0;
    }

    public float length() {
        return MathUtils.sqrt((x * x) + (y * y) + (z * z));
    }

    public Vector3 normalize() {
        return new Vector3(x, y, z).div(length());
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 mul(float f) {
        return new Vector3(x * f, y * f, z * f);
    }

    public Vector3 mul(Vector3 v) {
        return new Vector3(x * v.x, y * v.y, z * v.z);
    }

    public Vector3 div(float f) {
        return new Vector3(x / f, y / f, z / f);
    }

    public float dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3((y * v.z) - (z * v.y), (z * v.x) - (x * v.z), (x * v.y) - (y * v.x));
    }

    public Vector3 invert() {
        return new Vector3(-x, -y, -z);
    }

    public FloatBuffer get() {
        buff.put(new float[]{x, y, z, 1});
        buff.flip();

        return buff;
    }
}
