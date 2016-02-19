package jtransc.game.math;

public class Point {
    public double x;
    public double y;

    public Point() {
        this(0, 0);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point setTo(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public void copyFrom(Point that) {
        this.x = that.x;
        this.y = that.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point that = (Point) o;
        return this.x == that.x && this.y == that.y;

    }

    @Override
    public int hashCode() {
        return (int) (31 * Double.doubleToLongBits(x) + Double.doubleToLongBits(y));
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}
