package fenyx.engine.world;

import fenyx.engine.api.Runtime;
import fenyx.engine.geom.Matrix;
import fenyx.engine.geom.Shape;
import fenyx.engine.geom.Vector3;
import fenyx.engine.render.Color;
import fenyx.engine.render.Renderable;
import fenyx.engine.render.Renderer;
import fenyx.engine.render.smd.Model;
import fenyx.engine.utils.ResourceUtils;
import java.util.HashSet;

/**
 *
 * @author KiQDominaN
 */
public abstract class PointObject extends Renderable {

    //Movetype
    public static final int MOVETYPE_STATIC = 0, MOVETYPE_DYNAMIC = 1;
    public int movetype;
    //Types of damage
    public static final byte DMG_GENERIC = -1, DMG_BULLET = 0, DMG_BURN = 1, DMG_ACID = 2, DMG_FROST = 3, DMG_MELEE = 4;
    //Needs collision?
    public boolean solid;
    public boolean trigger = false;
    public boolean checkout_world = true;
    //Visible flag
    public boolean visible;
    //No model?
    public boolean nomodel = true;
    //Model
    public Model model;
    //World parameters
    protected Matrix transform = new Matrix();
    protected Vector3 origin = new Vector3();
    protected Vector3 angles = new Vector3();
    public int layer;
    public float speed;
    public Vector3 current_speed = new Vector3();
    //Size & collision
    protected Shape bbox;
    protected Shape cbox;
    protected HashSet<PointObject> collides = new HashSet<>();
    public Color color = Color.green;
    //Name for triggers
    public String name = new String();
    //Parent system
    protected HashSet<PointObject> childs = new HashSet<>();
    protected PointObject parent, target;
    //Some parameters of game object
    public float life = 1f;

    public void init() {
    }

    public void setupShader() {
        if (nomodel) return;

        shader = ResourceUtils.load_shader("simple_draw");
    }

    public void update() {
    }

    public void render() {
        if (nomodel) return; //|| model == Model.empty_model

        Renderer.useShader(shader);
        shader.setUniform("ambient", Color.white);
        Renderer.drawModel(model);
        Renderer.useShader(null);
    }

    public void touch(PointObject obj) {
    }

    public void addChild(PointObject obj) {
        obj.parent = this;
        childs.add(obj);
    }

    public HashSet<PointObject> getChilds() {
        return childs;
    }

    public void checkCollisions() {
        if (movetype != MOVETYPE_DYNAMIC) return;

        for (PointObject c : collides) {
            float required_speed = current_speed.length() * 0.85f;
            Vector3 vec_speed = new Vector3(0, 0, 0);

//            if (c.getCBOX().getX() <= getCBOX().getX()) vec_speed.x = required_speed;
//            if (c.getCBOX().getX() > getCBOX().getX()) vec_speed.x = -required_speed;
//            if (c.getCBOX().getY() <= getCBOX().getY()) vec_speed.y = required_speed;
//            if (c.getCBOX().getY() > getCBOX().getY()) vec_speed.y = -required_speed;
            moveObject(vec_speed);
        }
    }

    public boolean hasCollisions() {
        return !collides.isEmpty();
    }

    public void die() {
    }

    public void setTarget(PointObject target) {
        this.target = target;
    }

    public boolean hasTarget() {
        return (target != null);
    }

    public void setBounds(float w, float h) {
        bbox = new Shape.Rect(0, 0, w, h);
    }

    public void setCollisionRect(float x, float y, float w, float h) {
        cbox = new Shape.Rect(x, y, w, h);
    }

    public void setCollisionCircle(float centerx, float centery, int radius, int precize) {
        cbox = new Shape.Circle(centerx, centery, radius, precize);
    }

    public void setModel(Model model) {
        this.model = model;
        nomodel = false;

        //setBounds(model.frame_width, model.frame_height);
    }

    public void setWorldPosition(float x, float y, float z) {
        origin.x = x;
        origin.y = y;
        origin.z = z;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void moveObject(Vector3 speed) {
        if (movetype == MOVETYPE_STATIC) return;

        speed = speed.mul(Runtime.frametime);

        origin.x += speed.x;
        origin.y += speed.y;
        origin.z += speed.z;
    }

    public void rotateObject(float x, float y, float z) {
        angles.x += x;
        angles.y += y;
        angles.z += z;
    }

    public Vector3 getAngles() {
        return angles;
    }

    public float getWorldX() {
        return origin.x;
    }

    public float getWorldY() {
        return origin.y;
    }

    public float getWorldZ() {
        return origin.z;
    }

    public Vector3 getOrigin() {
        return origin;
    }
}
