import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(Point2D p) {
        checkIfNull(p);

        if (!contains(p)) {
            root = insert(root, p, 0);
            size++;
        }
    }

    private Node insert(Node head, Point2D p, int counter) {
        if (head == null) {
            head = new Node(p, true);
            return head;
        }

        if (counter % 2 == 0) {
            if (p.x() < head.point.x()) {
                if (head.left == null) {
                    head.left = new Node(p, false);
                }
                else {
                    insert(head.left, p, counter + 1);
                }
            }
            else {
                if (head.right == null) {
                    head.right = new Node(p, false);
                }
                else {
                    insert(head.right, p, counter + 1);
                }
            }
        }
        else {
            if (p.y() < head.point.y()) {
                if (head.left == null) {
                    head.left = new Node(p, true);
                }
                else {
                    insert(head.left, p, counter + 1);
                }
            }
            else {
                if (head.right == null) {
                    head.right = new Node(p, true);
                }
                else {
                    insert(head.right, p, counter + 1);
                }
            }
        }
        return head;
    }

    public void draw() {
        draw(root, new RectHV(0, 0, 1, 1));
    }

    private void draw(Node pointer, RectHV rectHV) {
        if (pointer == null) {
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.02);
        pointer.point.draw();

        StdDraw.setPenRadius(0.001);
        if (pointer.vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(pointer.point.x(), rectHV.ymin(), pointer.point.x(), rectHV.ymax());
            draw(pointer.left,
                 new RectHV(rectHV.xmin(), rectHV.ymin(), pointer.point.x(), rectHV.ymax()));
            draw(pointer.right,
                 new RectHV(pointer.point.x(), rectHV.ymin(), rectHV.xmax(), rectHV.ymax()));
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rectHV.xmin(), pointer.point.y(), rectHV.xmax(), pointer.point.y());
            draw(pointer.left,
                 new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), pointer.point.y()));
            draw(pointer.right,
                 new RectHV(rectHV.xmin(), pointer.point.y(), rectHV.xmax(), rectHV.ymax()));
        }
    }

    public boolean contains(Point2D p) {
        checkIfNull(p);

        Node pointer = root;
        while (pointer != null) {
            if (pointer.vertical) {
                if (p.x() < pointer.point.x()) {
                    pointer = pointer.left;
                }
                else if (p.x() > pointer.point.x()) {
                    pointer = pointer.right;
                }
                else {
                    if (Double.compare(p.y(), pointer.point.y()) == 0) {
                        return true;
                    }
                    pointer = pointer.right;
                }
            }
            else {
                if (p.y() < pointer.point.y()) {
                    pointer = pointer.left;
                }
                else if (p.y() > pointer.point.y()) {
                    pointer = pointer.right;
                }
                else {
                    if (Double.compare(p.x(), pointer.point.x()) == 0) {
                        return true;
                    }
                    pointer = pointer.right;
                }
            }
        }
        return false;
    }

    public Iterable<Point2D> range(RectHV rect) {
        checkIfNull(rect);

        Queue<Point2D> q = new Queue<>();
        return range(root, rect, q);
    }

    private Iterable<Point2D> range(Node pointer, RectHV rect, Queue<Point2D> q) {
        if (pointer == null) {
            return q;
        }


        if (!rect.contains(pointer.point)) {
            if (pointer.vertical) {
                if (rect.xmax() < pointer.point.x()) {
                    range(pointer.left, rect, q);
                }
                else if (rect.xmin() > pointer.point.x()) {
                    range(pointer.right, rect, q);
                }
                else {
                    range(pointer.left, rect, q);
                    range(pointer.right, rect, q);
                }
            }
            else {
                if (rect.ymax() < pointer.point.y()) {
                    range(pointer.left, rect, q);
                }
                else if (rect.ymin() > pointer.point.y()) {
                    range(pointer.right, rect, q);
                }
                else {
                    range(pointer.left, rect, q);
                    range(pointer.right, rect, q);
                }
            }
        }
        else {
            q.enqueue(pointer.point);
            range(pointer.left, rect, q);
            range(pointer.right, rect, q);
        }
        return q;
    }

    public Point2D nearest(Point2D p) {
        checkIfNull(p);
        return isEmpty() ? null : nearest(root, root, p).point;
    }

    private Node nearest(Node pointer, Node champion, Point2D p) {
        if (pointer == null) {
            return champion;
        }

        double minDistance = champion.point.distanceSquaredTo(p);
        double thisDistance = pointer.point.distanceSquaredTo(p);
        double rectYDistance = Math.abs(p.y() - pointer.point.y()) * Math
                .abs(p.y() - pointer.point.y());
        double rectXDistance = Math.abs(p.x() - pointer.point.x()) * Math
                .abs(p.x() - pointer.point.x());

        if (thisDistance < minDistance) {
            champion = pointer;
        }

        // dikse
        if (pointer.vertical) {
            // point soldaysa
            if (p.x() < pointer.point.x()) {
                // soldaki adam daha yakınsa
                champion = nearest(pointer.left, champion, p);
                if (champion.point.distanceSquaredTo(p) >= rectXDistance) {
                    champion = nearest(pointer.right, champion, p);
                }
            }
            else {
                // sağdaki adam daha yakınsa
                champion = nearest(pointer.right, champion, p);
                if (champion.point.distanceSquaredTo(p) >= rectXDistance) {
                    champion = nearest(pointer.left, champion, p);
                }
            }
        }
        // yataysa
        else {
            // point alttaysa
            if (p.y() < pointer.point.y()) {
                // alttaki adam daha yakınsa
                champion = nearest(pointer.left, champion, p);
                if (champion.point.distanceSquaredTo(p) >= rectYDistance) {
                    champion = nearest(pointer.right, champion, p);
                }
            }
            // üstteyse
            else {
                // üstteki adam daha yakınsa
                champion = nearest(pointer.right, champion, p);
                if (champion.point.distanceSquaredTo(p) >= rectYDistance) {
                    champion = nearest(pointer.left, champion, p);
                }
            }
        }
        return champion;
    }

    private void checkIfNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }


    private class Node {
        Point2D point;
        Node left;
        Node right;
        boolean vertical;

        public Node(Point2D point, boolean vertical) {
            this.point = point;
            this.vertical = vertical;
        }
    }
}

