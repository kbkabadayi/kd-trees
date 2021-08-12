import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;

import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> ts;

    public PointSET() {
        ts = new TreeSet<>();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return ts.size();
    }

    public void insert(Point2D p) {
        checkIfNull(p);

        ts.add(p);
    }

    public boolean contains(Point2D p) {
        checkIfNull(p);

        return ts.contains(p);
    }

    public void draw() {
        for (Point2D p : ts) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        checkIfNull(rect);

        Queue<Point2D> q = new Queue<>();
        for (Point2D p : ts) {
            if (rect.contains(p)) {
                q.enqueue(p);
            }
        }
        return q;
    }

    public Point2D nearest(Point2D p) {
        checkIfNull(p);

        double nearest = Double.POSITIVE_INFINITY;
        Point2D nearestPoint = null;
        for (Point2D point : ts) {
            double distance = point.distanceSquaredTo(p);
            if (distance < nearest) {
                nearest = distance;
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }

    private void checkIfNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {

    }
}
