package fenyx.engine.ui;

import fenyx.engine.render.Color;

/**
 *
 * @author KiQDominaN
 */
public abstract class UIElement extends UI {

    protected Color color = Color.white;
    public UI parent_gui;
    public UIElement parent_element;

    public void setColor(Color c) {
        color = c;
    }

    public void onClick() {
    }

    public void onFocus() {
    }

    public void onFocusLost() {
    }

    public void onKeyPressed() {
    }
}
