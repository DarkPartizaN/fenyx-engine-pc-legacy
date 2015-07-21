package fenyx.engine.ui;

import fenyx.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UIButton extends UIElement {

    private UIText label;
    private int text_x, text_y;

    public void setText(String label) {
        this.label = new UIText();
        this.label.setText(label);
    }

    public void setTextPosition(int x, int y) {
        text_x = x;
        text_y = y;
    }

    public void draw() {
        Renderer.drawImage(background, x, y);

        label.setPosition(x + text_x, y + text_y);
        label.setColor(color);
        label.draw();
    }

}
