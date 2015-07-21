package fenyx.engine.utils;

import fenyx.engine.api.Controllable;
import fenyx.engine.api.Runtime;
import fenyx.engine.geom.Vector2;
import fenyx.engine.geom.Vector3;
import fenyx.engine.render.Polygon;
import fenyx.engine.render.ShaderProgram;
import fenyx.engine.render.Texture;
import fenyx.engine.render.Vertex;
import fenyx.engine.render.smd.Animation;
import fenyx.engine.render.smd.Bone;
import fenyx.engine.render.smd.Controller;
import fenyx.engine.render.smd.Frame;
import fenyx.engine.render.smd.Model;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

/**
 *
 * @author KiQDominaN
 */
public final class ResourceUtils {

    //Resource root directories
    public static final String root_dir = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath().concat("/");
    public static final String images_dir = "res/gfx/";
    public static final String maps_dir = "res/maps/";
    public static final String fonts_dir = "res/fonts/";
    public static final String models_dir = "res/models/";
    public static final String shaders_dir = "res/shaders/";

    private static final HashMap<String, BufferedImage> cached_images = new HashMap<>(); //Image cache
    private static final HashMap<String, Texture> cached_textures = new HashMap<>(); //Textures cache
    private static final HashMap<String, ShaderProgram> cached_shaders = new HashMap<>(); //Shaders cache

    private static final BufferedImage null_image = create_null_image(); //Emo-texture for missed images :)
    private static final BufferedImage watermark_image = load_awt_image("launcher/watermark.png"); //For screenshots

    public static final ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    public static final ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

    private static BufferedImage create_null_image() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 32, 32);

        g.setColor(Color.PINK);
        for (int y = 0; y < 32; y += 16) {
            for (int x = 0; x < 32; x += 16) {
                g.fillRect(x, y, 8, 8);
                g.fillRect(x + 8, y + 8, 8, 8);
            }
        }

        return img;
    }

    public static boolean file_exists(String path) {
        return new File(path).exists();
    }

    public static BufferedImage load_awt_image(String imageID) {
        if (!cached_images.containsKey(imageID)) {
            try {
                cached_images.put(imageID, ImageIO.read(new File(root_dir.concat(images_dir).concat(imageID))));
            } catch (IOException ex) {
                return null_image;
            }
        }

        return cached_images.get(imageID);
    }

    public static Texture load_texture(String imageID) {
        if (!cached_textures.containsKey(imageID)) {
            BufferedImage source_image = load_awt_image(imageID), target_image;
            WritableRaster raster;

            if (source_image.getColorModel().hasAlpha()) {
                raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, source_image.getWidth(), source_image.getHeight(), 4, null);
                target_image = new BufferedImage(glAlphaColorModel, raster, false, null);
            } else {
                raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, source_image.getWidth(), source_image.getHeight(), 3, null);
                target_image = new BufferedImage(glColorModel, raster, false, null);
            }

            // copy the source image into the produced image
            Graphics2D g = target_image.createGraphics();
            g.setColor(new Color(0f, 0f, 0f, 0f));
            g.fillRect(0, 0, source_image.getWidth(), source_image.getHeight());
            g.drawImage(source_image, 0, 0, null);

            byte[] data = ((DataBufferByte) target_image.getRaster().getDataBuffer()).getData();

            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.order(ByteOrder.nativeOrder());
            buffer.put(data, 0, data.length);
            buffer.flip();

            Texture tmp = new Texture();
            tmp.width = target_image.getWidth();
            tmp.height = target_image.getHeight();

            glBindTexture(GL_TEXTURE_2D, tmp.id); //Bind texture ID

            //Setup wrap mode
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            //Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            //Send texel data to OpenGL
            if (source_image.getColorModel().hasAlpha())
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, target_image.getWidth(), target_image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            else
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, target_image.getWidth(), target_image.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);

            cached_textures.put(imageID, tmp);
        }

        return cached_textures.get(imageID);
    }

    public static Font load_ttf(String font_name, int font_size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(root_dir.concat(fonts_dir).concat(font_name))).deriveFont((float) font_size);
        } catch (FontFormatException | IOException ex) {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            return gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics().getFont().deriveFont((int) font_size);
        }
    }

    public static void take_screenshot() {
        glReadBuffer(GL_FRONT);

        int width = Runtime.screen_width;
        int height = Runtime.screen_height;
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);

        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        String date = new SimpleDateFormat("HHmmssddMMyyyy").format(new Date());
        String name = "screenshot_".concat(date).concat(".png");

        try {
            File file = new File(root_dir.concat("screenshots/")); // The file to save to.
            if (!file.exists()) file.mkdir();

            file = new File(root_dir.concat("screenshots/").concat(name));
            if (!file.exists()) file.createNewFile();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int i = (x + (width * y)) * bpp;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }

            Graphics2D g = image.createGraphics();
            g.drawImage(watermark_image, 2, image.getHeight() - watermark_image.getHeight() - 2, null);
            g.dispose();

            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            //TODO: Engine error handling
        }

        //TODO: Engine developer messages
        //String msg = "Screenshot saved at " + (System.currentTimeMillis() - ms) + " ms";
        //AFTRuntime.game_font.drawString(msg, AFTRuntime.screen_width - AFTRuntime.game_font.stringWidth(msg) - 2, (int) (AFTRuntime.screen_height - AFTRuntime.game_font.fontHeight() - 2), Color.YELLOW);
        Runtime.resetKey(Controllable.TAKE_SCREENSHOT); //For prevent chain of screenshots

        //Runtime.console.info_print("Screenshot ".concat(name).concat(" saved!"));
    }

    public static String load_file(String file) {
        InputStream is;
        String tmp = new String();
        int c;

        try {
            is = new FileInputStream(root_dir.concat(file));

            while ((c = is.read()) != -1) tmp += ((char) c);

            is.close();
        } catch (IOException e) {
        }

        return tmp;
    }

    public static String[] load_lines(String file) {
        InputStream is;
        Scanner scan;
        ArrayList<String> tmp = new ArrayList<>();

        try {
            is = new FileInputStream(root_dir.concat(file));
            scan = new Scanner(is);

            while (scan.hasNextLine()) tmp.add(scan.nextLine().concat("\n"));

            is.close();
        } catch (IOException e) {
        }

        return tmp.toArray(new String[tmp.size()]);
    }

    public static ShaderProgram load_shader(String name) {
        if (!cached_shaders.containsKey(name)) {
            ShaderProgram tmp = new ShaderProgram();
            tmp.setVertexShader(load_file(shaders_dir.concat(name).concat("/vertex.glsl")));
            tmp.setFragmentShader(load_file(shaders_dir.concat(name).concat("/fragment.glsl")));
            tmp.compile();

            cached_shaders.put(name, tmp);
        }

        return cached_shaders.get(name);
    }

    private static Model loadmodel;

    public static Model load_model(String name) {
        loadmodel = new Model(); //Clear model

        String[] smd = load_lines(models_dir.concat(name));
        String[] tok;
        String s, tmp;
        float x, y, z;
        Frame f = new Frame();

        for (int line = 0; line < smd.length; line++) {
            s = smd[line].trim();
            s = StringUtils.replace(s, "  ", " ");

            //Parse tokens
            if (s.startsWith("version")) continue;

            if (s.startsWith("nodes")) {
                while (true) {
                    s = smd[++line].trim();
                    s = StringUtils.replace(s, "  ", " ");

                    if (s.equals("end")) break;

                    tok = StringUtils.splitString(s, " ");

                    Bone b = new Bone();
                    b.id = Integer.parseInt(tok[0]);

                    int parent = Integer.parseInt(tok[tok.length - 1]);

                    if (parent == -1)
                        loadmodel.root = b;
                    else {
                        Bone p = loadmodel.bones.get(parent);
                        b.parent = p;
                        p.childs.add(b);
                    }

                    tmp = "";
                    for (int i = 1; i < tok.length - 1; i++) tmp = tmp.concat(tok[i]).concat(" ");
                    tmp = tmp.trim();
                    tmp = StringUtils.replace(tmp, "\"", "");
                    b.name = tmp;

                    if (b.name.equals("Bip01 Head")) {
                        Controller c = new Controller();
                        c.name = "Head";
                        loadmodel.addController(b, c);
                    }

                    if (b.parent == null) {
                        Controller c = new Controller();
                        c.name = "Root";
                        loadmodel.addController(b, c);
                    }

                    loadmodel.bones.add(b);
                }
            }

            if (s.startsWith("skeleton")) {
                line++;

                f = new Frame();

                while (true) {
                    s = smd[++line].trim();
                    s = StringUtils.replace(s, "  ", " ");

                    if (s.equals("end")) break;

                    tok = StringUtils.splitString(s, " ");

                    Bone b = loadmodel.bones.get(Integer.valueOf(tok[0]));

                    x = -Float.parseFloat(tok[1]);
                    y = Float.parseFloat(tok[2]);
                    z = Float.parseFloat(tok[3]);

                    f.pos.put(b.id, new Vector3(x, y, z));

                    x = -MathUtils.deg(Float.parseFloat(tok[4]));
                    y = MathUtils.deg(Float.parseFloat(tok[5]));
                    z = MathUtils.deg(Float.parseFloat(tok[6]));

                    f.angles.put(b.id, new Vector3(x, y, z));
                }
            }

            if (s.startsWith("triangles")) {
                while (true) {
                    s = smd[++line].trim();
                    s = StringUtils.replace(s, "  ", " ");

                    if (s.equals("end")) break;

                    Polygon p = new Polygon();
                    p.tex = load_texture("tex/".concat(name.substring(0, name.indexOf("/") + 1)).concat(s));

                    for (int j = 0; j < p.vertices.length; j++) {
                        s = smd[++line].trim();
                        s = StringUtils.replace(s, "  ", " ");

                        tok = StringUtils.splitString(s, " ");

                        Vertex v = new Vertex();

                        x = -Float.parseFloat(tok[1]);
                        y = Float.parseFloat(tok[2]);
                        z = Float.parseFloat(tok[3]);

                        v.pos = new Vector3();
                        v.origin = new Vector3(x, y, z);

                        x = -Float.parseFloat(tok[4]);
                        y = Float.parseFloat(tok[5]);
                        z = Float.parseFloat(tok[6]);

                        v.norm = new Vector3(x, y, z);
                        v.uv = new Vector2(Float.parseFloat(tok[7]), 1f - Float.parseFloat(tok[8]));

                        p.vertices[j] = v;

                        Bone b = loadmodel.bones.get(Integer.valueOf(tok[0]));
                        b.assigned.add(v);
                    }

                    loadmodel.mesh.add(p);
                }
            }
        }

        loadmodel.root_frame = f;
        loadmodel.addAnimation(load_animation("gordon/look_idle.smd"));
        loadmodel.addAnimation(load_animation("gordon/walk.smd"));

        loadmodel.init_matrices();

        return loadmodel;
    }

    public static Animation load_animation(String name) {
        Animation anim = new Animation();
        anim.name = name.substring(name.indexOf("/") + 1, name.length() - 4);

        String[] smd = load_lines(models_dir.concat(name));
        String[] tok;
        String s;
        float x, y, z;
        int bone_num = 0;

        for (int line = 0; line < smd.length; line++) {
            s = smd[line].trim();
            s = StringUtils.replace(s, "  ", " ");

            //Parse tokens
            if (s.startsWith("nodes")) {
                while (true) {
                    s = smd[++line].trim();
                    s = StringUtils.replace(s, "  ", " ");

                    if (s.equals("end")) break;

                    bone_num++;
                }
            }

            if (s.startsWith("skeleton")) {
                while (true) {
                    s = smd[++line].trim().replace("  ", " ");

                    if (s.equals("end")) break;

                    if (s.startsWith("time")) {
                        Frame f = new Frame();

                        for (int i = 0; i < bone_num; i++) {
                            s = smd[++line].trim();
                            s = StringUtils.replace(s, "  ", " ");

                            tok = StringUtils.splitString(s, " ");

                            int bone = Integer.valueOf(tok[0]);

                            x = -Float.parseFloat(tok[1]);
                            if (bone == 0) y = 0;
                            else y = Float.parseFloat(tok[2]);
                            z = Float.parseFloat(tok[3]);

                            Vector3 pos = new Vector3(x, y, z);

                            x = -MathUtils.deg(Float.parseFloat(tok[4]));
                            y = MathUtils.deg(Float.parseFloat(tok[5]));
                            z = MathUtils.deg(Float.parseFloat(tok[6]));

                            Vector3 angles = new Vector3(x, y, z);

                            f.pos.put(bone, pos);
                            f.angles.put(bone, angles);
                        }

                        anim.frames.add(f);
                    }
                }
            }
        }

        return anim;
    }
}
