package fenyx.engine.render.smd;

import fenyx.engine.geom.Matrix;

/**
 *
 * @author KiQDominaN
 */
public class Controller {

    public String name;
    public Matrix mat;

    public Controller() {
        mat = new Matrix();
    }

    public void rotateX(float angle) {
        mat = Matrix.mul(mat, Matrix.rotate(angle, 1, 0, 0));
    }

    public void rotateY(float angle) {
        mat = Matrix.mul(mat, Matrix.rotate(angle, 0, 1, 0));
    }

    public void rotateZ(float angle) {
        mat = Matrix.mul(mat, Matrix.rotate(angle, 0, 0, 1));
    }

    public void move(float x, float y, float z) {
        mat = Matrix.mul(mat, Matrix.translate(x, y, z));
    }
}
