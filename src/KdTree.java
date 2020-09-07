/*
    Kd Tree

    2d-tree is a generalization of a BST to two-dimensional keys.
    Build a BST with points in the nodes, using x- and y-coordinates of the points as keys in strictly alternating sequence.

    IMPLEMENT: search and insert, draw

    Search and insert Algorithm for Kd Trees: The algorithms for search and insert are similar to those for BSTs, but
    at the root we use the x-coordinate (if the point to be inserted has a smaller x-coordinate than the point at the
    root, go left; otherwise go right); then at the next level, we use the y-coordinate (if the point to be inserted
    has a smaller y-coordinate than the point in the node, go left; otherwise go right); then at the next level the
    x-coordinate, and so forth.

    TODO:
    - for insert/search/etc., must implement for y-coordinates when vertical

 */

import edu.princeton.cs.algs4.Point2D;
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

//    public int size() {
//        return size(root);
//    }
//
//    private int size(Node x) {
//        if (x == null) return 0;
//        return x.size;
//    }

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(Point2D p) {
        root = insert(root, p, false);     // vertical = false, horizontal = true
    }

    private Node insert(Node x, Point2D p, boolean orient) {
        if (x == null) return new Node(p);
        int cmp = compareX(p.x(), x.p.x());
        if (cmp < 0 && !orient) x.lb = insert(x.lb, p, true);
        else if (cmp < 0 && orient) x.lb = insert(x.lb, p, false);
        if (cmp > 0 && !orient) x.rt = insert(x.rt, p, true);
        else if (cmp > 0 && orient) x.rt = insert(x.rt, p, false);
        return x;
    }

    private int compareX(double x1, double x2) {
        if (x1 < x2) return -1;
        else if (x1 > x2) return 1;
        return 0;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("");
        if (search(root, p) == null) return false;
        else return true;
    }

    public Point2D search(Point2D p) {
        return search(root, p);
    }

    private Point2D search(Node x, Point2D p) {
        if (x == null) return null;
        int cmp = compareX((p.x(), x.p.x());
        if (cmp < 0) return search(x.lb, p);
        else if (cmp > 0) return search(x.lb, p);
        else return x.p;
    }
}
