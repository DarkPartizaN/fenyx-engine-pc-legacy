package fenyx.engine.render.smd;

import fenyx.engine.geom.Vector3;
import java.util.HashMap;

/**
 *
 * @author KiQDominaN
 */
public class Frame {

    public HashMap<Integer, Vector3> pos;
    public HashMap<Integer, Vector3> angles;

    public Frame() {
        pos = new HashMap<>();
        angles = new HashMap<>();
    }
}
