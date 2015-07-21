package fenyx.engine.ui;

import java.util.ArrayList;

import fenyx.engine.api.Runtime;
import fenyx.engine.render.Color;
import fenyx.engine.render.Renderer;

/**
 *
 * @author KiQDominaN
 */
public class UITextBox extends UIText {

    private double _y;
    private ArrayList<String> strings;
    public double scroll_speed = 0.25;

    public void setText(String s) {
        if (s == null) {
            strings = null;
            return;
        }

        int spacecount = 0;
        int start = 0, end;

        s = s.replaceAll("\n", " \n ").concat(" ");

        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == ' ') spacecount++;

        strings = new ArrayList<>();

        if (spacecount > 0) {
            while (start < s.length()) {
                end = s.indexOf(" ", start);
                strings.add(s.substring(start, end).concat(" "));
                start = end + 1;
            }
        }
    }

    public void init() {
        setPosition(0, 0);
        resetScroll();
    }

    public void setPosition(int x, int y) {
        super.setPosition(x, y);

        resetScroll();
    }

    public void resetScroll() {
        _y = y;
    }

    public void draw() {
        if (!isEmpty()) {
            Color c = Color.black;
            c.a = 0.5f;

            Renderer.fillRect(x, y, width, height, c);

            int __y = (int) _y;
            int __x = x;
            int str_width;

            Renderer.clipRect(x, y, width, height);

            for (String string : strings) {
                str_width = font.stringWidth(string);

                if (__x + str_width > x + width) {
                    __x = x;
                    __y += font.getHeight();
                }

                if (__y + font.getHeight() > y && __y < y + height) Renderer.drawString(font, string, __x, __y, color);

                __x += str_width;
            }

            if (_y > y || __y + font.getHeight() < y + height) scroll_speed = -scroll_speed;
            if (strings.size() * font.getHeight() > height) _y += scroll_speed;

            Renderer.clipRect(0, 0, Runtime.screen_width, Runtime.screen_height);
        }
    }

    public boolean isEmpty() {
        return (strings == null || strings.isEmpty());
    }
}
