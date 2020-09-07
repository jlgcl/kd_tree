/*
    Princeton Algs4 - Week#5 Geometric Application of BSTs

    BRUTE FORCE IMPLEMENTATION
        - uses SET.java that uses TreeSet. SET.java uses methods from TreeSet such as contains(), etc.
            - this is the brute force approach.

    IMPORTANT LEARNED:
    - in SET.java, since SET<Key extends Comparable<Key>> implements Iterable<Key>, we can declare a new instance
    using SET<Point2D> newSet = new SET<Point2D>();
        - this proves that the SET class is an iterable, containing more than 1 element.
        - also, SET is in the context of a TreeSet, hence a collection.
    - Iterable<Key> return item requires a collection as the return item.
        - confusion) the lecture mentions that iterable has a method that returns an iterator, which has has/hasNext (that is for an interface).
 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.ArrayList;

public class PointSET {
    private SET<Point2D> newSet;

    public PointSET()                               // construct an empty set of points
    {
        newSet = new SET<Point2D>();
    }

    public boolean isEmpty()                      // is the set empty?
    {
        return newSet.isEmpty();
    }

    public int size()                         // number of points in the set
    {
        return newSet.size();
    }

    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        if (p == null) throw new IllegalArgumentException("input is null");
        newSet.add(p);
    }

    public boolean contains(Point2D p)            // does the set contain point p?
    {
        if (p == null) throw new IllegalArgumentException("input is null);
        return newSet.contains(p);
    }

    public void draw()                         // draw all points to standard draw
    {
        for (Point2D p : newSet) p.draw();
    }

    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        if (rect == null) throw new IllegalArgumentException("input null");
        ArrayList<Point2D> rectContains = new ArrayList<Point2D>();
        for (Point2D point : newSet) {
            if (rect.contains(point)) {
                rectContains.add(point);
            }
        }
        return rectContains;
    }

    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        if (p == null) throw new IllegalArgumentException("input is null");
        Point2D closest = null;

        for (Point2D point : newSet) {
            if (closest == null) closest = point;
            else if (p.distanceTo(point) < p.distanceTo(closest)) closest = point;
        }
        return closest;
    }

    public static void main(String[] args)                  // unit testing of the methods (optional)
    {
    }
}
