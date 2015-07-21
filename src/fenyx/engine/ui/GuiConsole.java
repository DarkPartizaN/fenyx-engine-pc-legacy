package fenyx.engine.ui;

import java.util.LinkedList;

import fenyx.engine.render.Color;
import fenyx.engine.Engine;
import fenyx.engine.api.Controllable;
import fenyx.engine.api.Runtime;
import fenyx.engine.render.Renderer;
import fenyx.engine.utils.StringUtils;

/**
 *
 * @author KiQDominaN
 */
public class GuiConsole extends UI {

    public final static int CON_FADEIN = 0, CON_FADEOUT = 1, CON_ACTIVE = 2, CON_HIDDEN = 3;
    private int state = CON_HIDDEN;
    private final float slide_speed = 8f;
    private final long key_delay = 80, carret_delay = 350;
    private long last_key_press = System.currentTimeMillis();
    private long last_carret_blink = System.currentTimeMillis();
    private float _y;
    private int curr_line, curr_comm;
    private int lines_max;

    private final LinkedList<UIText> lines = new LinkedList<>();
    private final LinkedList<UIText> commands = new LinkedList<>();

    private final UIText start_line = new UIText();
    private final UIText carret = new UIText();
    private final UIText current_line = new UIText();

    public void init() {
        x = y = 0;
        width = Runtime.screen_width;
        height = Runtime.screen_height / 2;
        _y = -height - 1;

        lines_max = (int) (height / font.getHeight());

        start_line.setText(">>");
        carret.setText("_");

        addElement(start_line);
        addElement(current_line);
        addElement(carret);
    }

    public void input() {
    }

    public void draw() {
        if (state == CON_HIDDEN) return;

        Renderer.fillRect(x, _y, width, height, new Color(0x88000000));

        super.draw();

        if (!lines.isEmpty()) {
            int line = lines.size() + 1;
            for (UIText t : lines) {
                t.setPosition(x + 2, (int) (_y + lines_max * font.getHeight() - font.getHeight() * line));
                t.draw();

                line--;
            }
        }
    }

    public void update() {
        super.update();

        switch (state) {
            case CON_FADEIN:
                fadein();
                process();
                break;
            case CON_FADEOUT:
                fadeout();
                break;
            case CON_ACTIVE:
                process();
                break;
            case CON_HIDDEN:
                return;
        }

        start_line.setPosition(x + 2, (int) (_y + lines_max * font.getHeight() - font.getHeight()));
        current_line.setPosition(x + 2 + font.stringWidth(">>"), (int) (_y + lines_max * font.getHeight() - font.getHeight()));
        carret.setPosition(x + 2 + font.stringWidth(">>") + current_line.width, (int) (_y + lines_max * font.getHeight() - font.getHeight()));
    }

    private void process() {
        long curr_time = System.currentTimeMillis();
        boolean delay = curr_time - last_key_press > key_delay;

        if ((curr_time - last_carret_blink > carret_delay)) {
            if (carret.equals("_")) carret.setText("");
            else carret.setText("_");

            last_carret_blink = curr_time;
        }

        if (Runtime.keyPressed(Controllable.KEY_ANY) && !Runtime.keyPressed(Controllable.CONSOLE) && delay) {
            String add = Runtime.getInput();
            if (!add.isEmpty()) {
                Runtime.resetKey(Runtime.lastKey());
                current_line.setText(current_line.getText().concat(add));
            }

            if (Runtime.keyPressed(Controllable.ARROWUP)) {
                curr_line--;

                if (curr_line < 0) curr_line = 0;

                if (!commands.isEmpty()) current_line.setText(commands.get(curr_line).getText());

                last_key_press = curr_time;
            }

            if (Runtime.keyPressed(Controllable.ARROWDOWN)) {
                curr_line++;

                if (curr_line > lines.size() - 1) {
                    curr_line = lines.size();
                    current_line.setText("");
                } else if (!commands.isEmpty()) current_line.setText(commands.get(curr_line).getText());

                last_key_press = curr_time;
            }

            if (Runtime.keyPressed(Controllable.BACKSPACE) && !current_line.isEmpty()) {
                current_line.setText(current_line.getText().substring(0, current_line.getText().length() - 1));
                last_key_press = curr_time;
            }

            if (Runtime.keyPressed(Controllable.ENTER)) {
                Runtime.resetKey(Controllable.ENTER);

                con_print(current_line.getText());

                current_line.setText("");

                commands.add(current_line);
                execute(StringUtils.splitString(StringUtils.replace(lines.getLast().getText(), "  ", " "), " "));
            }
        }
    }

    private void execute(String[] cmd) {
        switch (cmd[0].toLowerCase()) {
            case "exit":
            case "quit":
                callback.setState(Engine.IN_STOPING);
                break;
            case "test":
                debug_print("Hey, it's console test!!!");
                break;
        }

        curr_line = lines.size();
    }

    public int getState() {
        return state;
    }

    public boolean isActive() {
        return state == CON_ACTIVE;
    }

    public void show() {
        state = CON_FADEIN;
    }

    public void hide() {
        state = CON_FADEOUT;
    }

    private void fadein() {
        _y += slide_speed;

        if (_y > y) {
            _y = y;
            state = CON_ACTIVE;
        }
    }

    private void fadeout() {
        _y -= slide_speed;

        if (_y < -height - 1) {
            _y = -height - 1;
            state = CON_HIDDEN;
        }
    }

    public void con_print(String s) {
        UIText t = new UIText();
        t.setText(s);
        lines.add(t);
    }

    public void info_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.green);
        t.setText("[I] ".concat(s));
        lines.add(t);
    }

    public void debug_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.cyan);
        t.setText("[D] ".concat(s));
        lines.add(t);
    }

    public void err_print(String s) {
        UIText t = new UIText();
        t.setColor(Color.red);
        t.setText("[W] ".concat(s));
        lines.add(t);
    }

}
