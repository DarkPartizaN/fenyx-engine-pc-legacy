package fenyx.engine;

import java.io.File;

import fenyx.engine.utils.ResourceUtils;

/**
 *
 * @author KiQDominaN
 */
public class App {

    public Screen screen;

    public App() {
        String osName = System.getProperty("os.name").toLowerCase().substring(0, System.getProperty("os.name").indexOf(' '));
        String osBit = System.getProperty("sun.arch.data.model");

        if (osBit.equals("32")) osBit = "86";//HACKHACK

        System.out.println("Platform: ".concat(osName).concat(osBit));

        System.setProperty("org.lwjgl.librarypath", new File(ResourceUtils.root_dir.concat("/lib/native/").concat(osName).concat("/x").concat(osBit)).getAbsolutePath());
        System.out.println("Native library path: ".concat(System.getProperty("org.lwjgl.librarypath")));
    }

}
