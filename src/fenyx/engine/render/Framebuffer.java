package fenyx.engine.render;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;

/**
 *
 * @author KiQDominaN
 */
public class Framebuffer {

    public int id;
    public Texture tex, depth_tex;

    public Framebuffer(int width, int height) {
        id = glGenFramebuffersEXT();
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);

        tex = new Texture(width, height);
        glBindTexture(GL_TEXTURE_2D, tex.id);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, tex.width, tex.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        // Poor filtering. Needed !
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, tex.id, 0);
        
        depth_tex = new Texture(width, height);
        glBindTexture(GL_TEXTURE_2D, depth_tex.id);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, tex.width, tex.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, depth_tex.width, depth_tex.height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, 0);

        // Poor filtering. Needed !
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, depth_tex.id, 0);

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void unbind() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public void draw() {
        glEnable(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, tex.id);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);
            glTexCoord2f(1, 1);
            glVertex2f(tex.width, 0);
            glTexCoord2f(1, 0);
            glVertex2f(tex.width, tex.height);
            glTexCoord2f(0, 0);
            glVertex2f(0, tex.height);
        }
        glEnd();

        glDisable(GL_TEXTURE_2D);
    }

    public void blend(int src, int dst) {
        glEnable(GL_BLEND);
        glBlendFunc(src, dst);

        draw();

        glDisable(GL_BLEND);
    }

}
