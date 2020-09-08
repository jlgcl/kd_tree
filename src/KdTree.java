/*
    Kd Tree

    2d-tree is a generalization of a BST to two-dimensional keys.
    Build a BST with points in the nodes, using x- and y-coordinates of the points as keys in strictly alternating sequence.

    IMPLEMENT: search and insert, draw (can skip), range search & nearest-neighbor search

    * Search and insert Algorithm for Kd Trees *
     The algorithms for search and insert are similar to those for BSTs, but
    at the root we use the x-coordinate (if the point to be inserted has a smaller x-coordinate than the point at the
    root, go left; otherwise go right); then at the next level, we use the y-coordinate (if the point to be inserted
    has a smaller y-coordinate than the point in the node, go left; otherwise go right); then at the next level the
    x-coordinate, and so forth.

    Each node corresponds to an axis-aligned rectangle in the unit square, which encloses all of the points in its
    subtree. The root corresponds to the unit square; the left and right children of the root corresponds to the two
    rectangles split by the x-coordinate of the point at the root, and so forth.
        - for example, in the specification, the rectangle for (0.7, 0.2) is (0, 0, 1, 1), rectangle for (0.5, 0.4) is
        (0, 0, 0.7, 1) in other words, the left side of the root (0.7, 0.2), and so forth.

    TODO:
    - for insert/search/etc., must implement for y-coordinates when vertical - COMPLETE
    - skip the draw() method - primarily used for debugging

 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {
    private Node root;
    private int size;

    /* NOTE: unlike BST, this Node class is static because it does not refer to a generic "Key" or "Value" that depends
    on the object associated with the outer class. */
    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // axis-aligned rectangle corresponding to this node
        private Node lb, rt;    // left/bottom subtree & right/top subtree
        // private int size;    // no rank or select operations, so no need for subtree size

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    /// INSERTION ///
    public void insert(Point2D p) {
        RectHV rootRect = new RectHV(0, 0, 1, 1);   // for example) insert (0.7, 0.2) will create (0, 0, 1, 1) rectangle
        root = insert(root, p, false, rootRect);     // vertical = false, horizontal = true
    }

    private Node insert(Node h, Point2D p, boolean orient, RectHV rect) {
        if (h == null) {
            size++;
            return new Node(p, rect);
        }
        // vertical
        if (!orient) {
            int cmp = compareX(p.x(), h.p.x());
            // per specification example:
            // after the root insertion, the unit rectangle for the left is (0, 0, 0.7, 1) from (0, 0, 1, 1).
            // the rectangle for the root is (0, 0, 1, 1)
            // the unit rectangle for the right is (0.7, 0, 1, 1)
            // the rectangle for (0.5, 0.4) is (0, 0, 0.7, 1), this is now the coordinate's RectHV: (h.rect.xmin(), h.rect.ymin(), h.rect.xmax(), h.rect.ymax())
            RectHV xRectLeft = new RectHV(h.rect.xmin(), h.rect.ymin(), h.p.x(), h.rect.ymax());    // go through the example in the specification for this rule
            RectHV xRectRight = new RectHV(h.p.x(), h.rect.ymin(), h.rect.xmax(), h.rect.ymax());
            if (cmp < 0) h.lb = insert(h.lb, p, true, xRectLeft);
            else if (cmp > 0) h.rt = insert(h.rt, p, false, xRectRight);
        }
        // horizontal
        else {
            int cmp = compareX(p.y(), h.p.y());
            // per specification example, after insert(0.5, 0.4):
            // the rectangle for the below is (0, 0, 0.7, 0.4)
            // the rectangle for the above is (0, 0.4, 0.7, 1)
            RectHV yRectBelow = new RectHV(h.rect.xmin(), h.rect.ymin(), h.rect.xmax(), h.p.y());
            RectHV yRectAbove = new RectHV(h.rect.xmin(), h.p.y(), h.rect.xmax(), h.rect.ymax());
            if (cmp < 0) h.lb = insert(h.lb, p, true, yRectBelow);
            else if (cmp > 0) h.rt = insert(h.rt, p, false, yRectAbove);
        }
        return h;
    }

    private int compareX(double coord1, double coord2) {
        if (coord1 < coord2) return -1;
        else if (coord1 > coord2) return 1;
        return 0;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("");
        if (search(p) == null) return false;
        else return true;
    }

    public Point2D search(Point2D p) {
        return search(root, p, false);
    }

    // each recursion must return Point2D (different from insert which returns a Node)
    private Point2D search(Node h, Point2D p, boolean orient) {
        if (h == null) return null;
        if (!orient) {
            int cmp = compareX(p.x(), h.p.x());
            if (cmp < 0) return search(h.lb, p, true);
            else if (cmp > 0) return search(h.rt, p, false);
        } else {
            int cmp = compareX(p.y(), h.p.y());
            if (cmp < 0) return search(h.lb, p, true);
            else if (cmp > 0) return search(h.rt, p, false);
        }
        return h.p;
    }

    /// RANGE SEARCH ///
    public Iterable<Point2D> range(RectHV queryRect) {
        if (queryRect == null) throw new IllegalArgumentException("null input");
        Queue<Point2D> points = new Queue<Point2D>();
        range(root, queryRect, points);
        return points;
    }

    // follow the lecture instructions:
    /*
        - check if point in node lies in given rectangle
        - recursively search left/bottom (if any could fall in rectangle).
        - recursively search right/top (if any could fall in rectangle).
     */
    private void range(Node h, RectHV queryRect, Queue<Point2D> points) {
        if (h == null) return;
        if (queryRect.contains(h.p)) points.enqueue(h.p);
        /* exit condition below; if queryRect doesn't intersect the current unit rectangle, then the we are looking at the
        wrong side OR non-existent unit rectangle that contains the points.
         */
        if (!h.rect.intersects(queryRect)) return;
        // searches the left subtree first, then the right subtree
        range(h.lb, queryRect, points);
        range(h.rt, queryRect, points);
    }

    /// NEAREST NEIGHBOR SEARCH ///
    // NOTE: if a point is contained in (0.5, 0.4) RectHV, then it is also contained inside the root's rectangle.
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("input null");
        double currentDist = Double.POSITIVE_INFINITY;  // or could be set to null;
        return nearest(root, p, currentDist);
    }

    // only rule out the rectangles at the root, but after, we must search both left & right
    // CORRECTION: compare distances to squared distances of rect & point.
    private Point2D nearest(Node h, Point2D p, double currentDist) {
        if (h == null) return null;
        if (h.p.distanceTo(p) < currentDist) currentDist = h.p.distanceTo(p);
        // searches the left subtree first, then the right subtree if left rectangle contains the point, vice-versa:
        if (h.rt != null && h.rt.rect.contains(p)) {
            nearest(h.rt, p, currentDist);
            nearest(h.lb, p, currentDist);
        } else {
            nearest(h.lb, p, currentDist);
            nearest(h.rt, p, currentDist);
        }
        return h.p;
    }
    // may need to provide additional cases...

}
