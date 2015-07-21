package fenyx.engine.geom;

import fenyx.engine.utils.MathUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 *
 * @author KiQDominaN
 */
public final class Matrix {

    public float[] m = new float[16];
    private FloatBuffer buff;

    public Matrix() {
        reset();

        buff = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public void set(float[] m) {
        System.arraycopy(m, 0, this.m, 0, m.length);
    }

    public FloatBuffer get() {
        buff.put(m);
        buff.flip();

        return buff;
    }

    public void reset() {
        m[0] = 1;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = 1;
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = 1;
        m[11] = 0;

        m[12] = 0;
        m[13] = 0;
        m[14] = 0;
        m[15] = 1;
    }

    public static Matrix rotate(float angle, int x, int y, int z) {
        Matrix tmp = new Matrix();

        float sin = MathUtils.sin(angle);
        float cos = MathUtils.cos(angle);

        if (x == 1) {
            tmp.m[5] = cos;
            tmp.m[6] = -sin;
            tmp.m[9] = sin;
            tmp.m[10] = cos;
        }
        if (y == 1) {
            tmp.m[0] = cos;
            tmp.m[2] = sin;
            tmp.m[8] = -sin;
            tmp.m[10] = cos;
        }
        if (z == 1) {
            tmp.m[0] = cos;
            tmp.m[1] = -sin;
            tmp.m[4] = sin;
            tmp.m[5] = cos;
        }

        return tmp;
    }

    public static Matrix translate(float x, float y, float z) {
        Matrix tmp = new Matrix();

        tmp.m[12] = x;
        tmp.m[13] = y;
        tmp.m[14] = z;

        return tmp;
    }

    public static Matrix scale(float x, float y, float z) {
        Matrix tmp = new Matrix();

        tmp.m[0] = x;
        tmp.m[5] = y;
        tmp.m[10] = z;

        return tmp;
    }

    public Matrix invert() {
        int i, j, k;
        int size = 4;
        float[] mass = new float[16];
        float[] tmp = new float[16];
        System.arraycopy(m, 0, mass, 0, m.length);

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++)
                tmp[i + j * size] = 0;
            tmp[i + i * size] = 1;
        }

        //прямой ход методом Гаусса
        float a, b;
        for (i = 0; i < size; i++) {
            a = mass[i + i * size];
            for (j = i + 1; j < size; j++) {
                b = mass[j + i * size];
                for (k = 0; k < size; k++) {
                    mass[j + k * size] = mass[i + k * size] * b - mass[j + k * size] * a;
                    tmp[j + k * size] = tmp[i + k * size] * b - tmp[j + k * size] * a;
                }
            }
        }

        //обратный ход вычисления элементов обратной матрицы
        float sum;
        for (i = 0; i < size; i++) {
            for (j = size - 1; j >= 0; j--) {
                sum = 0;
                for (k = size - 1; k > j; k--)
                    sum += mass[j + k * size] * tmp[k + i * size];

                if (mass[j + j * size] == 0) return null;

                tmp[j + i * size] = (tmp[j + i * size] - sum) / mass[j + j * size];
            }
        }

        //создание единичной матрицы
        Matrix inv = new Matrix();
        inv.set(tmp);

        return inv;
    }

    public static Matrix mul(Matrix m1, Matrix m2) {
        float[] f = new float[16];

        f[0] = m1.m[0] * m2.m[0] + m1.m[1] * m2.m[4] + m1.m[2] * m2.m[8] + m1.m[3] * m2.m[12];
        f[1] = m1.m[0] * m2.m[1] + m1.m[1] * m2.m[5] + m1.m[2] * m2.m[9] + m1.m[3] * m2.m[13];
        f[2] = m1.m[0] * m2.m[2] + m1.m[1] * m2.m[6] + m1.m[2] * m2.m[10] + m1.m[3] * m2.m[14];
        f[3] = m1.m[0] * m2.m[3] + m1.m[1] * m2.m[7] + m1.m[2] * m2.m[11] + m1.m[3] * m2.m[15];

        f[4] = m1.m[4] * m2.m[0] + m1.m[5] * m2.m[4] + m1.m[6] * m2.m[8] + m1.m[7] * m2.m[12];
        f[5] = m1.m[4] * m2.m[1] + m1.m[5] * m2.m[5] + m1.m[6] * m2.m[9] + m1.m[7] * m2.m[13];
        f[6] = m1.m[4] * m2.m[2] + m1.m[5] * m2.m[6] + m1.m[6] * m2.m[10] + m1.m[7] * m2.m[14];
        f[7] = m1.m[4] * m2.m[3] + m1.m[5] * m2.m[7] + m1.m[6] * m2.m[11] + m1.m[7] * m2.m[15];

        f[8] = m1.m[8] * m2.m[0] + m1.m[9] * m2.m[4] + m1.m[10] * m2.m[8] + m1.m[11] * m2.m[12];
        f[9] = m1.m[8] * m2.m[1] + m1.m[9] * m2.m[5] + m1.m[10] * m2.m[9] + m1.m[11] * m2.m[13];
        f[10] = m1.m[8] * m2.m[2] + m1.m[9] * m2.m[6] + m1.m[10] * m2.m[10] + m1.m[11] * m2.m[14];
        f[11] = m1.m[8] * m2.m[3] + m1.m[9] * m2.m[7] + m1.m[10] * m2.m[11] + m1.m[11] * m2.m[15];

        f[12] = m1.m[12] * m2.m[0] + m1.m[13] * m2.m[4] + m1.m[14] * m2.m[8] + m1.m[15] * m2.m[12];
        f[13] = m1.m[12] * m2.m[1] + m1.m[13] * m2.m[5] + m1.m[14] * m2.m[9] + m1.m[15] * m2.m[13];
        f[14] = m1.m[12] * m2.m[2] + m1.m[13] * m2.m[6] + m1.m[14] * m2.m[10] + m1.m[15] * m2.m[14];
        f[15] = m1.m[12] * m2.m[3] + m1.m[13] * m2.m[7] + m1.m[14] * m2.m[11] + m1.m[15] * m2.m[15];

        Matrix tmp = new Matrix();
        tmp.set(f);

        return tmp;
    }

    public void transform(Point[] points) {
        float x, y;

        for (Point p : points) {
            x = p.x;
            y = p.y;

            p.x = x * m[0] + y * m[1] + m[12];
            p.y = x * m[4] + y * m[5] + m[13];
        }
    }

    public void transform(Vector3 v) {
        float x, y, z;

        x = v.x;
        y = v.y;
        z = v.z;

        v.x = x * m[0] + y * m[4] + z * m[8] + m[12];
        v.y = x * m[1] + y * m[5] + z * m[9] + m[13];
        v.z = x * m[2] + y * m[6] + z * m[10] + m[14];
    }

    public void inv_transform(Vector3 v) {
        float x, y, z;

        x = v.x - m[12];
        y = v.y - m[13];
        z = v.z - m[14];

        v.x = x * m[0] + y * m[1] + z * m[2];
        v.y = x * m[4] + y * m[5] + z * m[6];
        v.z = x * m[8] + y * m[9] + z * m[10];
    }

    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("Matrix:\n");
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                b.append(m[x + y * 4]);
                b.append(", ");
            }
            b.append("\n");
        }
        b.append("End matrix\n");

        return b.toString();
    }

}
