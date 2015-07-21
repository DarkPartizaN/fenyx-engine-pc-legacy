package fenyx.engine.ui;

import fenyx.engine.render.Texture;
import fenyx.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UIImage extends UIElement {

    public UIImage(Texture image) {
        this.background = image;
    }

    public void draw() {
        Renderer.drawImage(background, x, y);
    }

    public Texture getImage() {
        return background;
    }
}
