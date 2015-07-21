package fenyx.engine.render;

import fenyx.engine.geom.Matrix;
import fenyx.engine.geom.Vector2;
import fenyx.engine.geom.Vector3;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author KiQDominaN
 */
public class ShaderProgram {

    public int id;
    private int vid = -1, fid = -1;
    private String vertex_source, fragment_source;

    public ShaderProgram() {
        id = glCreateProgram();
    }

    public void setVertexShader(String shader) {
        vertex_source = shader;

        if (vid == -1) vid = glCreateShader(GL_VERTEX_SHADER);
    }

    public void setFragmentShader(String shader) {
        fragment_source = shader;

        if (fid == -1) fid = glCreateShader(GL_FRAGMENT_SHADER);
    }

    public void compile() {
        glShaderSource(vid, vertex_source);
        glShaderSource(fid, fragment_source);

        glCompileShader(vid);
        if (glGetShaderi(vid, GL_COMPILE_STATUS) != GL_TRUE)
            throw new RuntimeException(glGetShaderInfoLog(vid));

        glCompileShader(fid);
        if (glGetShaderi(fid, GL_COMPILE_STATUS) != GL_TRUE)
            throw new RuntimeException(glGetShaderInfoLog(fid));

        glAttachShader(id, vid);
        glAttachShader(id, fid);

        glLinkProgram(id);
        glValidateProgram(id);
    }

    public void setUniform(String name, int value) {
        int loc = glGetUniformLocation(id, name);
        glUniform1i(loc, value);
    }

    public void setUniform(String name, int value, int value2) {
        int loc = glGetUniformLocation(id, name);
        glUniform2i(loc, value, value2);
    }

    public void setUniform(String name, int value, int value2, int value3) {
        int loc = glGetUniformLocation(id, name);
        glUniform3i(loc, value, value2, value3);
    }

    public void setUniform(String name, int value, int value2, int value3, int value4) {
        int loc = glGetUniformLocation(id, name);
        glUniform4i(loc, value, value2, value3, value4);
    }

    public void setUniform(String name, float value) {
        int loc = glGetUniformLocation(id, name);
        glUniform1f(loc, value);
    }

    public void setUniform(String name, float value, float value2) {
        int loc = glGetUniformLocation(id, name);
        glUniform2f(loc, value, value2);
    }

    public void setUniform(String name, float value, float value2, float value3) {
        int loc = glGetUniformLocation(id, name);
        glUniform3f(loc, value, value2, value3);
    }

    public void setUniform(String name, float value, float value2, float value3, float value4) {
        int loc = glGetUniformLocation(id, name);
        glUniform4f(loc, value, value2, value3, value4);
    }

    public void setUniform(String name, Color c) {
        setUniform(name, c.r, c.g, c.b, c.a);
    }

    public void setUniform(String name, Vector2 vec) {
        setUniform(name, vec.x, vec.y);
    }

    public void setUniform(String name, Vector3 vec) {
        setUniform(name, vec.x, vec.y, vec.z);
    }

    public void setUniform(String name, Matrix mat) {
        int loc = glGetUniformLocation(id, name);
        glUniformMatrix4fv(loc, false, mat.get());
    }
}
