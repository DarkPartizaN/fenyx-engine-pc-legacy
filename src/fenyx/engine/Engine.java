package fenyx.engine;

import java.util.LinkedList;

import fenyx.engine.api.Controllable;
import fenyx.engine.api.Runtime;
import fenyx.engine.render.Renderer;
import fenyx.engine.render.Color;
import fenyx.engine.ui.GuiHud;
import fenyx.engine.ui.UI;
import fenyx.engine.utils.ResourceUtils;
import fenyx.engine.world.Time;
import fenyx.engine.world.World;

/**
 *
 * @author KiQDominaN
 */
public final class Engine {

    //Engine states
    public static final int IN_CREATED = -1, IN_PAUSED = 0, IN_STOPING = 1, IN_GAME = 2, IN_GUI = 3, IN_DEAD = 4;
    private final LinkedList<Integer> states = new LinkedList<>();
    //FPS
    private int fps, tmp_fps;
    private long fps_update;
    private long last_frametime;
    private Color fps_color;
    //World
    public World world;
    //GUI
    private final LinkedList<UI> gui = new LinkedList<>();//For GUI change
    private GuiHud current_hud;

    public void create(World world) {
        this.world = world;

        setState(IN_CREATED);
    }

    public void start() {
        last_frametime = System.currentTimeMillis();

        //Time
        Time.checkTime();

        hideGUI();
        setState(IN_GAME);
    }

    public void frame() {
        switch (states.getFirst()) {
            case IN_GAME:
                processGame();
                break;
            case IN_GUI:
                processGui();
                break;
            case IN_PAUSED:
                pause();
                break;
            case IN_STOPING:
                stop();
                break;
        }

        //FPS
        long currentFrameTime = System.currentTimeMillis();
        if (currentFrameTime - fps_update >= 1000) {
            fps_update = currentFrameTime;
            fps = tmp_fps;
            tmp_fps = 0;
        } else
            tmp_fps++;

        if (fps < 10)
            fps_color = new Color(0xffaa0000);
        else if (fps < 20)
            fps_color = new Color(0xffaaaa00);
        else if (fps < 30)
            fps_color = new Color(0xff55aa00);
        else
            fps_color = new Color(0xff00aa00);

        Renderer.drawString("FPS:".concat(String.valueOf(fps)), 2, 2, fps_color);

        //Mouse position
        Runtime.mouse_world.x = (int) (Runtime.mouse_pos.x + world.getCamera().getWorldX());
        Runtime.mouse_world.y = (int) (Runtime.mouse_pos.y + world.getCamera().getWorldY());

        //Delta time
        Runtime.frametime = (currentFrameTime - last_frametime) / 10f;
        last_frametime = currentFrameTime;

        if (Runtime.keyPressed(Controllable.TAKE_SCREENSHOT)) ResourceUtils.take_screenshot();
    }

    private void processGame() {
        //Time
        Time.checkTime();
        //World
        world.update();
        world.draw();

        //HUD events (disabled under console)
        if (current_hud != null) {
            current_hud.input();
            current_hud.update();
            current_hud.draw();
        }
    }

    private void processGui() {
        if (!gui.isEmpty()) {
            gui.getLast().input();
            gui.getLast().update();
        }
        if (!gui.isEmpty()) //We need handle potential self-removing here
            gui.getLast().draw();
    }

    public void setHUD(GuiHud hud) {
        hud.setCallback(this);

        current_hud = hud;
    }

    public void showGUI(UI gui) {
        gui.setCallback(this);

        this.gui.push(gui);

        setState(IN_GUI);
    }

    public void hideGUI() {
        if (!gui.isEmpty()) gui.pop();

        restoreState();
    }

    public void loadGame(boolean quickave) {
    }

    public void saveGame(boolean quickave) {
    }

    private void pause() {
    }

    private void stop() {
        states.clear();

        setState(IN_DEAD);
    }

    public void setState(int state) {
        states.push(state);
    }

    public void restoreState() {
        states.pop();
    }

    public int getState() {
        return states.getLast();
    }
}
