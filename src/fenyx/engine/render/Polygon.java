package fenyx.engine.render;

import fenyx.engine.geom.Vector3;

/**
 *
 * @author KiQDominaN
 */
public class Polygon {

    public Vertex[] vertices;
    public Texture tex;

    public Polygon() {
        vertices = new Vertex[3];
    }

    public Polygon(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public Vector3 calc_normal() {
        Vector3 edge1 = vertices[1].pos.sub(vertices[0].pos);
        Vector3 edge2 = vertices[2].pos.sub(vertices[0].pos);

        return edge1.cross(edge2).normalize();
    }
}
