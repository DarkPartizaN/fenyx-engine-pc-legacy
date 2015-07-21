package fenyx.engine.ui;

import fenyx.engine.render.Texture;

/**
 *
 * @author KiQDominaN
 */
public class UICursor extends UIElement {

    protected Texture custom_cursor;
    protected int size = 12, indent = 5;

    public void setSize(int size) {
        this.size = size;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void setCustomImage(Texture cursor) {
        custom_cursor = cursor;
    }

    public void resetCustomImage() {
        custom_cursor = null;
    }
}
