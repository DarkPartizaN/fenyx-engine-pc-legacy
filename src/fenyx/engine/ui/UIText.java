package fenyx.engine.ui;

import fenyx.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UIText extends UIElement {

    protected String text = new String();

    public void draw() {
        if (!isEmpty()) Renderer.drawString(font, text, x, y, color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        width = font.stringWidth(text);
        height = (int) font.getHeight();
    }

    public void setFont(UIFont font) {
        this.font = font;
    }

    public boolean isEmpty() {
        return (text == null || text.isEmpty());
    }

    public boolean equals(String s) {
        return s.equals(text);
    }

}
