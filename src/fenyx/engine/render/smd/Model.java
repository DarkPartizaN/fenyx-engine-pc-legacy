package fenyx.engine.render.smd;

import fenyx.engine.geom.Matrix;
import fenyx.engine.geom.Vector3;
import fenyx.engine.render.Polygon;
import fenyx.engine.render.Vertex;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author KiQDominaN
 */
public class Model {

    public HashMap<String, Animation> animations;
    public ArrayList<Polygon> mesh;
    public ArrayList<Bone> bones;
    public Bone root;
    public Animation current_animation;
    public Frame root_frame;

    public Model() {
        mesh = new ArrayList<>();
        bones = new ArrayList<>();
        animations = new HashMap<>();

        current_animation = new Animation();
    }

    public void addAnimation(Animation a) {
        animations.put(a.name, a);
    }

    public void setAnimation(String id) {
        current_animation = animations.get(id);

        prepare_animation();
    }

    public Bone getBone(String name) {
        for (Bone b : bones) if (b.name.equals(name)) return b;

        return null;
    }

    public void addController(Bone b, Controller c) {
        b.controller = c;
    }

    public Controller getController(String name) {
        for (Bone b : bones)
            if (b.controller != null) {
                if (b.controller.name.equals(name))
                    return b.controller;
            }

        return null;
    }

    public void prepare_animation() {
        Frame f;

        if (current_animation == null || current_animation.frames.isEmpty())
            f = root_frame;
        else
            f = current_animation.getFirstFrame();

        for (Bone b : bones) {
            Vector3 angles = f.angles.get(b.id);
            Vector3 origin = f.pos.get(b.id);

            b.pos.reset();

            b.mat = Matrix.rotate(angles.x, 1, 0, 0);
            b.mat = Matrix.mul(b.mat, Matrix.rotate(angles.y, 0, 1, 0));
            b.mat = Matrix.mul(b.mat, Matrix.rotate(angles.z, 0, 0, 1));
            b.mat = Matrix.mul(b.mat, Matrix.translate(origin.x, origin.y, origin.z));

            if (b.parent != null) {
                Bone p = b.parent;
                b.mat = Matrix.mul(b.mat, p.mat);
            }

            if (b.controller != null) b.mat = Matrix.mul(b.mat, b.controller.mat);

            b.mat.transform(b.pos);

            for (Vertex v : b.assigned) {
                v.pos.x = v.origin.x;
                v.pos.y = v.origin.y;
                v.pos.z = v.origin.z;

                b.mat.transform(v.pos);
            }
        }
    }

    public void update_bones() {
        calc_bone(root);
    }

    private void calc_bone(Bone b) {
        Frame frame;

        if (current_animation == null || current_animation.frames.isEmpty())
            frame = root_frame;
        else
            frame = current_animation.getCurrentFrame();

        Vector3 angles = frame.angles.get(b.id);
        Vector3 origin = frame.pos.get(b.id);

        b.pos.reset();

        b.mat = Matrix.rotate(angles.x, 1, 0, 0);
        b.mat = Matrix.mul(b.mat, Matrix.rotate(angles.y, 0, 1, 0));
        b.mat = Matrix.mul(b.mat, Matrix.rotate(angles.z, 0, 0, 1));
        b.mat = Matrix.mul(b.mat, Matrix.translate(origin.x, origin.y, origin.z));

        if (b.parent != null) {
            Bone p = b.parent;
            b.mat = Matrix.mul(b.mat, p.mat);
        }

        if (b.controller != null) b.mat = Matrix.mul(b.mat, b.controller.mat);

        b.mat.transform(b.pos);

        for (Vertex v : b.assigned) {
            v.pos.x = v.origin.x;
            v.pos.y = v.origin.y;
            v.pos.z = v.origin.z;

            b.mat.transform(v.pos);
        }

        for (Bone c : b.childs) calc_bone(c);
    }

    public void init_matrices() {
        Frame f = root_frame;

        for (Bone b : bones) {
            Vector3 angles = f.angles.get(b.id);
            Vector3 origin = f.pos.get(b.id);

            b.mat = Matrix.rotate(angles.x, 1, 0, 0);
            b.mat = Matrix.mul(b.mat, Matrix.rotate(angles.y, 0, 1, 0));
            b.mat = Matrix.mul(b.mat, Matrix.rotate(angles.z, 0, 0, 1));
            b.mat = Matrix.mul(b.mat, Matrix.translate(origin.x, origin.y, origin.z));

            if (b.parent != null) {
                Bone p = b.parent;
                b.mat = Matrix.mul(b.mat, p.mat);
            }

            b.mat.transform(b.pos);

            for (Vertex v : b.assigned)
                b.mat.inv_transform(v.origin);
        }
    }

}
