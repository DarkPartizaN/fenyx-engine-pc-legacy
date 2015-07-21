package fenyx.engine.world;

import java.util.ArrayList;

import fenyx.engine.ai.Actor;
import fenyx.engine.api.Runtime;
import fenyx.engine.render.Framebuffer;
import fenyx.engine.render.Renderer;
import fenyx.engine.geom.Shape;
import fenyx.engine.render.Color;

/**
 *
 * @author KiQDominaN
 */
public class World {

    public int x, y, world_width, world_height;
    private Framebuffer fb_world, fb_light;
    private ArrayList<PointObject> objects, visible_objects;
    private ArrayList<Light> lights;
    private Light ambient;
    private Camera camera;
    private Actor actor;

    public void createDefaultLocation() {
        fb_world = new Framebuffer(Runtime.screen_width, Runtime.screen_height);
        fb_light = new Framebuffer(Runtime.screen_width, Runtime.screen_height);

        int tx = 50, ty = 50, tw = 64, th = 64, tr = 4;

        x = y = 0;
        world_width = tx * tw;
        world_height = ty * th;

        objects = new ArrayList<>();
        visible_objects = new ArrayList<>();
        lights = new ArrayList<>();

        camera = new Camera(new Shape.Rect(0, 0, Runtime.screen_width, Runtime.screen_height));
        camera.setWorldPosition(0, 0, -10);

        ambient = new Light(Light.AMBIENT);
        ambient.color = new Color(0xffffffff);
    }

    public void update() {
        removeDeadObjects();
        checkCollisions();

        //Think
        for (PointObject obj : objects) {
            obj.update();

            //Check out of world
            if (obj.checkout_world) {
//                if (obj.getBBOX().getX() < 0) obj.setWorldPosition(obj.getBBOX().getBounds().x / 2, obj.getWorldY(), 0);
//                if (obj.getBBOX().getY() < 0) obj.setWorldPosition(obj.getWorldX(), obj.getBBOX().getBounds().y / 2, 0);
//                if (obj.getBBOX().getX() + obj.getBBOX().getBounds().x >= world_width) obj.setWorldPosition(world_width - obj.getBBOX().getBounds().x / 2, obj.getWorldY(), 0);
//                if (obj.getBBOX().getY() + obj.getBBOX().getBounds().y >= world_height) obj.setWorldPosition(obj.getWorldX(), world_height - obj.getBBOX().getBounds().y / 2, 0);
            }
        }

        //Camera offset
        camera.update();
    }

    public void draw() {
        sortObjects();

        Renderer.setCamera(camera);

        //Draw world
        fb_world.bind();

        //Renderer.drawSkeleton(gordon);
//
//        getBackground().render();
        for (PointObject obj : objects) obj.render();
//
        fb_world.unbind();
        //Draw lights
//        fb_light.bind();
//
//        for (Light l : getLights()) l.render();
//        getAmbientLight().render();
//
        //fb_light.unbind();
        Renderer.setOrtho();
        //Blend framebuffers
        fb_world.draw();
        //Renderer.drawString("Cam pos: " + getCamera().getWorldX() + "," + getCamera().getWorldY() + "," + getCamera().getWorldZ(), 0, 20, Color.green);
        //fb_light.blend(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_COLOR);
    }

    public void addObject(PointObject obj) {
        obj.setupShader();
        objects.add(obj);

        for (PointObject p : obj.getChilds()) addObject(p);
    }

    public void removeObject(PointObject obj) {
        objects.remove(obj);
    }

    public Light getAmbientLight() {
        return ambient;
    }

    public ArrayList<Light> getLights() {
        return lights;
    }

    public ArrayList<PointObject> getVisible() {
        return visible_objects;
    }

    public PointObject getObjectByName(String name) {
        for (PointObject obj : objects)
            if (obj.name.equals(name))
                return obj;

        return null;
    }

    private void removeDeadObjects() {
        for (PointObject p : objects) if (p.life <= 0) objects.remove(p);
    }

    public int getWidth() {
        return world_width;
    }

    public int getHeight() {
        return world_height;
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    //Some old :(
    //but nice code :)
    public void sortObjects() {
        visible_objects.clear();
        lights.clear();

        for (PointObject p : objects) {
            if (p.visible) {
//                if (p.getBBOX().getX() + p.getBBOX().getBounds().x > camera.getWorldX()
//                        && p.getBBOX().getX() < camera.getWorldX() + camera.getWidth()
//                        && p.getBBOX().getY() + p.getBBOX().getBounds().y > camera.getWorldY()
//                        && p.getBBOX().getY() < camera.getWorldY() + camera.getHeight()) {

                if (p instanceof Light) lights.add((Light) p);
                else visible_objects.add(p);
//                }
            }
        }

        PointObject obj1, obj2;
        for (int j = 0; j < visible_objects.size(); j++) {
            for (int i = visible_objects.size() - 2; i >= j; i--) {
                obj1 = visible_objects.get(i);
                obj2 = visible_objects.get(i + 1);

                if (obj1.layer < obj2.layer) {
                    visible_objects.set(i, obj2);
                    visible_objects.set(i + 1, obj1);
                }
            }
        }
    }

    //Physics bleat!
    private void checkCollisions() {
//        PointObject obj1, obj2;
//
//        for (int i = 0; i < objects.size(); i++) {
//            obj1 = objects.get(i);
//            if (!obj1.solid && !obj1.trigger) continue;
//
//            for (int j = i + 1; j < objects.size(); j++) {
//                obj2 = objects.get(j);
//                if (!obj2.solid && !obj2.trigger) continue;
//
//                if (Shape.intersects(obj1.getCBOX(), obj2.getCBOX())) {
//                    obj1.collides.add(obj2);
//                    obj2.collides.add(obj1);
//
//                    obj1.touch(obj2);
//                    obj2.touch(obj1);
//                } else {
//                    obj1.collides.remove(obj2);
//                    obj2.collides.remove(obj1);
//                }
//            }
//
//            obj1.checkCollisions();
//        }
    }

}
