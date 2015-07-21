package fenyx.engine.ui;

import java.util.ArrayList;

import fenyx.engine.Engine;
import fenyx.engine.api.Controllable;
import fenyx.engine.api.Runtime;
import fenyx.engine.render.Texture;
import fenyx.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UI {

    protected int x, y, width, height;
    protected Texture background;
    private boolean fit_background;
    private ArrayList<UIElement> elements = new ArrayList<>();
    protected UIFont font = UIFont.getDefault();
    protected UICursor cursor;
    public Engine callback;

    public void init() {
    }

    public void update() {
        for (UIElement e : elements) {
            if (Runtime.mouse_pos.x > e.x
                    && Runtime.mouse_pos.x < e.x + e.width
                    && Runtime.mouse_pos.y > e.y
                    && Runtime.mouse_pos.y < e.y + e.height) {
                e.onFocus();

                if (Runtime.keyPressed(Controllable.SHOOT)) {
                    e.onClick();
                    continue;
                }

                if (Runtime.keyPressed(Controllable.KEY_ANY)) e.onKeyPressed();
            }

            e.update();
        }
    }

    public void input() {
    }

    public void draw() {
        //Draw tile background
        if (background != null) {
            //Tiled background
            if (!fit_background) {
                for (int y = this.y; y < height; y += background.height)
                    for (int x = this.x; x < width; x += background.width)
                        Renderer.drawImage(background, x, y);
            } else
                //or fit background
                                Renderer.drawImage(background, x, y, width, height);
        }

        for (UIElement e : elements) e.draw();
    }

    public void addElement(UIElement e) {
        e.parent_gui = this;
        e.setPosition(e.x + x, e.y + y);
        elements.add(e);
    }

    public void removeElement(UIElement e) {
        e.parent_gui = null;
        e.setPosition(e.x - x, e.y - y);
        elements.remove(e);
    }

    public void setCallback(Engine engine) {
        callback = engine;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setBackground(Texture background) {
        this.background = background;
    }

    public void fitBackground(boolean fit) {
        fit_background = fit;
    }

}
