package com.mygdx.game;

public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            return p.x == this.x && p.y == this.y;
        }
        return false;
    }

    public int hashCode() {
        long var1 = java.lang.Double.doubleToLongBits(this.x);
        var1 ^= java.lang.Double.doubleToLongBits(this.y) * 31L;
        return (int)var1 ^ (int)(var1 >> 32);
    }
}
