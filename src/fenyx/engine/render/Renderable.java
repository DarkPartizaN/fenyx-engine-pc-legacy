package fenyx.engine.render;

/**
 *
 * @author KiQDominaN
 */
public abstract class Renderable {
    protected ShaderProgram shader;

    public abstract void setupShader();
    public abstract void render();
}
