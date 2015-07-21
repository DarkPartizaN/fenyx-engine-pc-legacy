package fenyx.engine.world;

/**
 *
 * @author KiQDominaN
 */
public class Light extends PointObject {

    public static int AMBIENT = 0, POINT = 1, DIRECT = 2;
    public int type = POINT;

    public float distance, radius;
    public boolean cast_shadows = false;

    public Light(int type) {
        this.type = type;
    }

    public void init() {
        checkout_world = false;
        solid = false;
        nomodel = true;
        visible = true;

        setBounds(distance + 16, distance + 16);
        //setCollisionCircle(getWidth() / 2, getHeight() / 2, (int) distance, 20);
    }

    public void update() {
        if (hasTarget()) {
            //rotateObject(target.angle - radius / 2);
            setWorldPosition(target.getWorldX(), target.getWorldY(), target.getWorldZ());
        }
    }

    public void attach(PointObject p) {
        p.addChild(this);
        setTarget(p);
    }

    public void render() {
        //if (!visible) return;

//        Renderer.translate(getWorldX(), getWorldY());
//
//        glDisable(GL_ALPHA_TEST);
//        glEnable(GL_BLEND);
//
//        if (type == Light.AMBIENT) {
//            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
//
//            glBegin(GL_QUADS);
//            {
//                glColor4f(color.r, color.g, color.b, color.a);
//                glVertex2d(0, 0);
//                glVertex2d(Runtime.screen_width, 0);
//                glVertex2d(Runtime.screen_width, Runtime.screen_height);
//                glVertex2d(0, Runtime.screen_height);
//            }
//            glEnd();
//        }
//
//        int a = GL_SRC_COLOR;
//        int b = GL_ONE;
//
//        if (type == Light.POINT) {
//            float precize = 32;
//            float delta = 360f / precize;
//
//            glRotated(angle, 0, 0, 1);
//            Renderer.translate(getAttachPos().x, getAttachPos().y);
//
//            glBlendFunc(a, b);
//
//            glBegin(GL_TRIANGLE_FAN);
//            {
//                glColor4f(color.r, color.g, color.b, color.a);
//                glVertex2d(Renderer.off_x + 0, Renderer.off_y + 0);
//
//                glColor4f(0f, 0f, 0f, 0f);
//
//                for (float angle = 0; angle <= 360; angle += delta)
//                    glVertex2d(Renderer.off_x + distance * MathUtils.cos(angle), Renderer.off_y + distance * MathUtils.sin(angle));
//
//                glVertex2d(Renderer.off_x + distance, Renderer.off_y + 0);
//            }
//            glEnd();
//        }
//
//        if (type == Light.DIRECT) {
//            glRotated(angle, 0, 0, 1);
//            Renderer.translate(getAttachPos().x, getAttachPos().y);
//
//            float precize = 4;
//            float delta = radius / precize;
//
//            glBlendFunc(a, b);
//
//            glBegin(GL_TRIANGLE_FAN);
//            {
//                glColor4f(color.r, color.g, color.b, color.a);
//
//                glVertex2d(Renderer.off_x + 5, Renderer.off_y + 0);
//
//                glColor4f(0f, 0f, 0f, 0f);
//
//                glVertex2d(Renderer.off_x + 5, Renderer.off_y + -8);
//                for (float angle = 0; angle <= radius; angle += delta)
//                    glVertex2d(Renderer.off_x + distance * MathUtils.cos(angle), Renderer.off_y + distance * MathUtils.sin(angle));
//                glVertex2d(Renderer.off_x + 0, Renderer.off_y + 8);
//            }
//            glEnd();
//        }
//
//        glDisable(GL_BLEND);
//        glEnable(GL_ALPHA_TEST);
//
//        glColor4f(1f, 1f, 1f, 1f);
    }
}
