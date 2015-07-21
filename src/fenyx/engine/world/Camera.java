package fenyx.engine.world;

import fenyx.engine.api.Runtime;
import fenyx.engine.geom.Shape;
import fenyx.engine.geom.Vector3;
import fenyx.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public class Camera extends PointObject {

    public float aspect = Runtime.screen_aspect;
    public float fov = 60f;
    public float znear = 1f, zfar = 100f;
    private boolean freeze;

    public Camera(Shape.Rect viewport) {
        movetype = MOVETYPE_DYNAMIC;

        solid = false;
        visible = false;
        freeze = false;

        bbox = viewport;
        cbox = new Shape.Rect(0, 0, 0, 0);
    }

    public float getWidth() {
        return bbox.getWidth();
    }

    public float getHeight() {
        return bbox.getHeight();
    }

    public void freeze(boolean on) {
        freeze = on;
    }

    public void attach(PointObject p) {
        setTarget(p);
    }

    float v;
    float h;
    public Vector3 dir = new Vector3(), right = new Vector3(), up = new Vector3(0, 0, 1);

    public void update() {
        if (freeze) return;

        if (hasTarget()) {
            float delta_x = (target.getWorldX() - getWidth() / 2) - getWorldX();
            origin.x += delta_x / 10 + target.current_speed.x + MathUtils.cos(target.angles.x) * 3.5;

            float delta_y = (target.getWorldY() - getHeight() / 2) - getWorldY();
            origin.y += delta_y / 10 + target.current_speed.y + MathUtils.sin(target.angles.y) * 3.5;
        }

        float x = Runtime.mouse_pos.x;
        float y = Runtime.mouse_pos.y;

        float hw = (float) Runtime.screen_width / 2f;
        float hh = (float) Runtime.screen_height / 2f;

        if ((x == hw) && (y == hh)) return;

        Runtime.resetMouse();

        h += Runtime.frametime * hw - x;
        v += Runtime.frametime * hh - y;

        dir.x = MathUtils.cos(v) * MathUtils.sin(h);
        dir.y = MathUtils.sin(v);
        dir.z = MathUtils.cos(v) * MathUtils.cos(h);

        right.x = MathUtils.sin(h - Math.PI / 2.0f);
        right.y = 0;
        right.z = MathUtils.cos(h - Math.PI / 2.0f);

        up = dir.cross(right);
    }

    public void moveObject(Vector3 speed) {
        speed = speed.mul(Runtime.frametime);

        origin = origin.add(speed);
    }

    public void setFov(float fov) {
        if (fov < 1) fov = 1;
        if (fov > 179) fov = 179;

        this.fov = fov;
    }

}
