package fenyx.engine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

/**
 *
 * @author KiQDominaN
 */
public class Texture {

    public int id;
    public int width, height;

    public Texture() {
        id = glGenTextures();
    }

    public Texture(int width, int height) {
        id = glGenTextures();

        this.width = width;
        this.height = height;

        glBindTexture(GL_TEXTURE_2D, id); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public TextureRegion getRegion(float x, float y, float x2, float y2) {
        TextureRegion tmp = new TextureRegion(x, y, x2, y2);
        tmp.id = id;

        return tmp;
    }
}
