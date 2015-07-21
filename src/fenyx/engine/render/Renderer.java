package fenyx.engine.render;

import fenyx.engine.api.Runtime;
import fenyx.engine.geom.Matrix;
import fenyx.engine.geom.Point;
import fenyx.engine.geom.Shape;
import fenyx.engine.render.smd.Bone;
import fenyx.engine.render.smd.Model;
import fenyx.engine.ui.UIFont;
import fenyx.engine.world.Camera;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author KiQDominaN
 */
public class Renderer {

    private static Matrix projection = new Matrix(), view = new Matrix();
    private static ShaderProgram current_shader;

    public static Matrix getView() {
        return view;
    }

    public static Matrix getProjection() {
        return projection;
    }

    public static void refreshView() {
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glLoadMatrixf(view.get());
    }

    public static void setOrtho() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        float l = -Runtime.screen_width / 2f;
        float r = -l;
        float b = -Runtime.screen_height / 2f;
        float t = -b;
        float zn = -1;
        float zf = 1;

        float[] m = new float[16];

        m[0] = 2f / (r - l);
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = 2f / (b - t);
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = 2f / (zf - zn);
        m[11] = 0;

        m[12] = (l + r) / (l - r) + (l * m[0]);
        m[13] = (t + b) / (b - t) + (b * m[5]);
        m[14] = (zf + zn) / (zn - zf);
        m[15] = 1;

        projection.set(m);

        glLoadMatrixf(projection.get());

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void setPerspective(float aspect, float fov, float znear, float zfar) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        float f = 1f / (float) Math.tan(Math.toRadians(fov) / 2f);
        float zrange = znear - zfar;

        float[] m = new float[16];

        m[0] = f / aspect;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = f;
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = (zfar + znear) / zrange;
        m[11] = (zfar * znear) / zrange;

        m[12] = 0;
        m[13] = 0;
        m[14] = -1f;
        m[15] = 1;

        projection.set(m);

        glLoadMatrixf(projection.get());

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void setCamera(Camera cam) {
        setPerspective(cam.aspect, cam.fov, cam.znear, cam.zfar);

        view = Matrix.scale(0.1f, -0.1f, 0.1f);
        view = Matrix.mul(view, Matrix.rotate(cam.getAngles().x, 1, 0, 0));
        view = Matrix.mul(view, Matrix.rotate(cam.getAngles().y, 0, 1, 0));
        view = Matrix.mul(view, Matrix.rotate(cam.getAngles().z, 0, 0, 1));
        view = Matrix.mul(view, Matrix.translate(cam.getWorldX(), cam.getWorldY(), cam.getWorldZ()));

        glLoadMatrixf(view.get());
    }

    public static void clipRect(int x, int y, int w, int h) {
        glScissor(x, Runtime.screen_height - y - h, w, h);
    }

    //
    //3D DRAWING
    //
    public static void drawPolygon(Polygon p) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glBindTexture(GL_TEXTURE_2D, p.tex.id);
        glEnable(GL_TEXTURE_2D);

        glBegin(GL_POLYGON);
        {
            for (Vertex v : p.vertices) {
                glTexCoord2f(v.uv.x, v.uv.y);
                glNormal3f(v.norm.x, v.norm.y, v.norm.z);
                glVertex3f(v.pos.x, v.pos.y, v.pos.z);
            }
        }
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void drawModel(Model m) {
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);

        glDepthFunc(GL_LEQUAL);
        glEnable(GL_DEPTH_TEST);

        for (Polygon p : m.mesh) {
            glBindTexture(GL_TEXTURE_2D, p.tex.id);

            glBegin(GL_TRIANGLES);
            {
                for (Vertex v : p.vertices) {
                    glTexCoord2f(v.uv.x, v.uv.y);
                    glNormal3f(v.norm.x, v.norm.y, v.norm.z);
                    glVertex3f(v.pos.x, v.pos.y, v.pos.z);
                }
            }
            glEnd();
        }

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
    }

    public static void drawSkeleton(Model m) {
        glPointSize(4);

        //Bones
        glBegin(GL_POINTS);
        {
            for (Bone b : m.bones) {
                glColor3f(0, 0, 1);
                glVertex3f(b.pos.x, b.pos.y, b.pos.z);
            }
        }
        glEnd();

        glColor3f(1, 1, 0);

        Bone b2;

        glBegin(GL_LINES);
        {
            for (Bone b : m.bones) {
                if (b.parent != null) {
                    b2 = b.parent;

                    glVertex3f(b.pos.x, b.pos.y, b.pos.z);
                    glVertex3f(b2.pos.x, b2.pos.y, b2.pos.z);
                }
            }
        }
        glEnd();

        glColor4f(1, 1, 1, 1);
        glPointSize(1);
    }

    //SHADER SYSTEM
    public static void useShader(ShaderProgram shader) {
        current_shader = shader;

        if (shader == null) {
            glUseProgram(0);
            return;
        }

        glUseProgram(shader.id);
    }

    //
    //2D DRAWING
    //
    public static void drawPoint(Point p, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_POINTS);
        {
            glVertex2f(p.x, p.y);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawPoint(float x, float y, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_POINTS);
        {
            glVertex2f(x, y);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawLine(Point a, Point b, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_LINES);
        {
            glVertex2f(a.x, a.y);
            glVertex2f(b.x, b.y);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawLine(float x1, float y1, float x2, float y2, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_LINES);
        {
            glVertex2f(x1, y1);
            glVertex2f(x2, y2);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawStraight(float x, float y, float w, float h, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_LINES);
        {
            glVertex2f(x, y);
            glVertex2f(x + w, y + h);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawRect(Point a, Point b, Point c, Point d, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(a.x, a.y);
            glVertex2f(b.x, b.y);
            glVertex2f(c.x, c.y);
            glVertex2f(d.x, d.y);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawRect(float x, float y, float w, float h, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(x - 1, y);
            glVertex2f(x + w, y);
            glVertex2f(x + w, y + h);
            glVertex2f(x, y + h);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void fillRect(Point a, Point b, Point c, Point d, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_QUADS);
        {
            glVertex2f(a.x, a.y);
            glVertex2f(b.x, b.y);
            glVertex2f(c.x, c.y);
            glVertex2f(d.x, d.y);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void fillRect(float x, float y, float w, float h, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_QUADS);
        {
            glVertex2f(x - 1, y);
            glVertex2f(x + w, y);
            glVertex2f(x + w, y + h);
            glVertex2f(x, y + h);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawShape(Shape shape, Color color) {
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_LINE_LOOP);
        {
            for (Point p : shape.getPoints())
                glVertex2d(p.x, p.y);
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawString(String s, int x, int y, Color color) {
        drawString(UIFont.getDefault(), s, x, y, color);
    }

    public static void drawString(UIFont font, String s, int x, int y, Color color) {
        if (s == null || s.length() <= 0) return;

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, font.getFontTexture().id);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(color.r, color.g, color.b, color.a);

        glBegin(GL_QUADS);
        {
            for (char c : s.toCharArray()) {
                if (c == '\n') y += font.getHeight();

                float width = font.charWidth(c);
                float height = font.getHeight();
                float u = 1f / font.getFontImageWidth() * font.getCharX(c);
                float v = 1f / font.getFontImageHeight() * font.getCharY(c);
                float u2 = u + 1f / font.getFontImageWidth() * width;
                float v2 = v + 1f / font.getFontImageHeight() * height;

                glTexCoord2f(u, v);
                glVertex2f(x, y);

                glTexCoord2f(u2, v);
                glVertex2f(x + width, y);

                glTexCoord2f(u2, v2);
                glVertex2f(x + width, y + height);

                glTexCoord2f(u, v2);
                glVertex2f(x, y + height);

                x += width;
            }
        }
        glEnd();

        glColor4f(1, 1, 1, 1);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public static void drawImage(Texture tex, int x, int y) {
        drawImage(tex, x, y, tex.width, tex.height);
    }

    public static void drawImage(Texture tex, int x, int y, int w, int h) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(x, y);
            glTexCoord2f(1, 0);
            glVertex2f(x + w, y);
            glTexCoord2f(1, 1);
            glVertex2f(x + w, y + h);
            glTexCoord2f(0, 1);
            glVertex2f(x, y + h);
        }
        glEnd();

        glBindTexture(GL_TEXTURE_2D, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public static void drawRegion(TextureRegion tex, int x, int y) {
        drawRegion(tex, x, y, (int) (tex.u2 - tex.u), (int) (tex.v2 - tex.v));
    }

    public static void drawRegion(TextureRegion tex, int x, int y, int w, int h) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(tex.u, tex.v);
            glVertex2f(x, y);
            glTexCoord2f(tex.u2, tex.v);
            glVertex2f(x + w, y);
            glTexCoord2f(tex.u2, tex.v2);
            glVertex2f(x + w, y + h);
            glTexCoord2f(tex.u, tex.v2);
            glVertex2f(x, y + h);
        }
        glEnd();

        glBindTexture(GL_TEXTURE_2D, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

}
