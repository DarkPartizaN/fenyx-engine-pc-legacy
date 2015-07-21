package fenyx.engine.render.smd;

import fenyx.engine.geom.Matrix;
import fenyx.engine.geom.Vector3;
import fenyx.engine.render.Vertex;
import java.util.ArrayList;

/**
 *
 * @author KiQDominaN
 */
public class Bone {

    public int id;
    public Bone parent;
    public String name;

    public Matrix mat;
    public Vector3 pos;
    public Controller controller;
    public ArrayList<Vertex> assigned;
    public ArrayList<Bone> childs;

    public Bone() {
        mat = new Matrix();
        pos = new Vector3();
        assigned = new ArrayList<>();
        childs = new ArrayList<>();
    }
}
