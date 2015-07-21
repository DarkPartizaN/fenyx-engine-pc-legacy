package fenyx.engine.geom;

import fenyx.engine.utils.MathUtils;

/**
 *
 * @author KiQDominaN
 */
public abstract class Shape {

    protected float x, y, w, h;
    protected Matrix t = new Matrix();
    protected Point[] points;

    protected abstract void reset();

    public void translate(float x, float y) {
        t = Matrix.translate(x, y, 0);
    }

    public void rotate(float angle) {
        //t.rotate(angle, w / 2, h / 2, 0);
    }

    public void rotate(float angle, float x, float y) {
        //t.rotate(angle, x, y, 0);
    }

    public Matrix getMatrix() {
        return t;
    }

    public Point[] getPoints() {
        return points;
    }

    public float getX() {
        float min_x = points[0].x;
        for (Point p : points) if (p.x < min_x) min_x = p.x;

        return min_x;
    }

    public float getY() {
        float min_y = points[0].y;
        for (Point p : points) if (p.y < min_y) min_y = p.y;

        return min_y;
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public Point getBounds() {
        float minx, maxx;
        float miny, maxy;

        minx = maxx = getX();
        miny = maxy = getY();

        for (Point p : points) {
            if (p.x > maxx) maxx = p.x;
            if (p.y > maxy) maxy = p.y;
        }

        return new Point(maxx - minx, maxy - miny);
    }

    public void refresh() {
        reset();

        t.transform(points);
    }

    // Checks if the two polygons are intersecting.
    public static final boolean intersects(Shape a, Shape b) {
        for (int x = 0; x < 2; x++) {
            Shape s = (x == 0) ? a : b;

            for (int i = 0; i < s.getPoints().length; i++) {
                int j = (i + 1) % s.getPoints().length;

                Point p1 = s.getPoints()[i];
                Point p2 = s.getPoints()[j];
                Vector2 normal = new Vector2(p2.y - p1.y, p1.x - p2.x).normalize();

                float minA = 0;
                float maxA = 0;

                for (Point p : a.getPoints()) {
                    float projected = normal.x * p.x + normal.y * p.y;

                    if (minA == 0 || projected < minA) minA = projected;
                    if (maxA == 0 || projected > maxA) maxA = projected;
                }

                float minB = 0;
                float maxB = 0;

                for (Point p : b.getPoints()) {
                    float projected = normal.x * p.x + normal.y * p.y;

                    if (minB == 0 || projected < minB)
                        minB = projected;
                    if (maxB == 0 || projected > maxB)
                        maxB = projected;
                }

                if (maxA < minB || maxB < minA)
                    return false;
            }
        }

        return true;
    }

    public static class Rect extends Shape {

        public Rect(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            points = new Point[4];

            points[0] = new Point(x, y);
            points[1] = new Point(x + w, y);
            points[2] = new Point(x + w, y + h);
            points[3] = new Point(x, y + h);
        }

        protected void reset() {
            points[0].x = x;
            points[0].y = y;

            points[1].x = x + w;
            points[1].y = y;

            points[2].x = x + w;
            points[2].y = y + h;

            points[3].x = x;
            points[3].y = y + h;
        }
    }

    public static class Circle extends Shape {

        private final int radius;
        private final float delta;

        public Circle(float centerx, float centery, int radius, int precize) {
            this.x = centerx;
            this.y = centery;
            this.radius = radius;
            delta = 360f / precize;

            points = new Point[precize];

            double angle = 0;
            for (int i = 0; i < precize; i++) {
                points[i] = new Point(x + radius * MathUtils.cos(angle), y - radius * MathUtils.sin(angle));

                angle += delta;
            }
        }

        protected void reset() {
            double angle = 0;
            for (Point p : points) {
                p.x = x + radius * MathUtils.cos(angle);
                p.y = y - radius * MathUtils.sin(angle);

                angle += delta;
            }
        }
    }
}
