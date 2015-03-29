package thebetweenlands.utils;

public class Point2D extends java.awt.geom.Point2D
{
    public double x, y;

    public Point2D(double x, double y)
    {
        setLocation(x, y);
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setLocation(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
}