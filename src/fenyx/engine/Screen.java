package fenyx.engine;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import fenyx.engine.api.Runtime;

/**
 *
 * @author KiQDominaN
 */
public class Screen {

    //LWGL stuff here
    // We need to strongly reference callback instances.
    private GLFWErrorCallback error_callback;
    private GLFWKeyCallback key_callback;
    private GLFWMouseButtonCallback mousebuttons_callback;
    private GLFWCursorPosCallback cursor_callback;
    private GLFWCharCallback input_callback;
    // The window handle
    private long window_handle;

    //AfterEngine2 stuff here
    public Engine engine;

    public Screen(int width, int height, boolean fullscreen) {
        create(width, height, fullscreen);
    }

    private void create(int width, int height, boolean fullscreen) {
        if (width == 0) width = 640;
        if (height == 0) height = 480;

        System.out.println("LWJGL" + Sys.getVersion() + " starting!");
        System.out.println();

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(error_callback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // the window will be resizable

        // Create the window
        window_handle = glfwCreateWindow(width, height, "Fenyx Demo", fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

        if (window_handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        if (!fullscreen)
            glfwSetWindowPos(window_handle, (GLFWvidmode.width(vidmode) - width) / 2, (GLFWvidmode.height(vidmode) - height) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window_handle);
        // Enable v-sync
        glfwSwapInterval(1);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();

        glfwSetInputMode(window_handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetCallback(window_handle, key_callback = new KeyCallback());
        glfwSetCallback(window_handle, mousebuttons_callback = new MouseButtonsCallback());
        glfwSetCallback(window_handle, cursor_callback = new CursorCallback());
        glfwSetCallback(window_handle, input_callback = new CharCallback());

        //Engine stuff here
        Runtime.setupScreen(width, height);

        engine = new Engine();

        // Make the window visible
        glfwShowWindow(window_handle);
    }

    public void init() {
    }

    public void start() {
        // Setup GL
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_SCISSOR_TEST);

        glViewport(0, 0, Runtime.screen_width, Runtime.screen_height);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        loop();

        // Release window and window callbacks
        glfwDestroyWindow(window_handle);

        key_callback.release();
        mousebuttons_callback.release();
        cursor_callback.release();
        input_callback.release();

        // Terminate GLFW and release the GLFWerrorfun
        glfwTerminate();
        error_callback.release();

        System.gc();
        System.exit(0);
    }

    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window_handle) == GL_FALSE && engine.getState() != Engine.IN_DEAD) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            engine.frame();

            glfwSwapBuffers(window_handle); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private final class KeyCallback extends GLFWKeyCallback {

        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop

            if (action == GLFW_PRESS) Runtime.pressKey(key);
            if (action == GLFW_RELEASE) Runtime.resetKey(key);
        }
    }

    private final class CharCallback extends GLFWCharCallback {

        public void invoke(long window, int codepoint) {
            Runtime.inputChar((char) codepoint);
        }

    }

    private final class MouseButtonsCallback extends GLFWMouseButtonCallback {

        public void invoke(long window, int button, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) Runtime.pressKey(button);
            if (action == GLFW_RELEASE) Runtime.resetKey(button);
        }

    }

    private final class CursorCallback extends GLFWCursorPosCallback {

        public void invoke(long window, double xpos, double ypos) {
            if (window == window_handle) {
                Runtime.mouse_pos.x = (int) xpos;
                Runtime.mouse_pos.y = (int) ypos;
            }
        }
    }
}
